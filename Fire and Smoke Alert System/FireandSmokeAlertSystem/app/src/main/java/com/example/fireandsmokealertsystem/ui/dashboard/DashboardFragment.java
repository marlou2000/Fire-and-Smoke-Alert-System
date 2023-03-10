package com.example.fireandsmokealertsystem.ui.dashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fireandsmokealertsystem.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class DashboardFragment extends Fragment {

    DatabaseReference reference;
    View root;
    boolean firstExecution = true, firstLoop = false;
    int numberOfLoops = 0, onChildCount = 0, loopChanges = 0, count = 0, totalRoom = 1, alertLoopSend = 0;
    private int linearLayoutId;
    private static final String CHANNEL_ID = "channelID";
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notificationChannel";
            String description = "Alert notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager;
            notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        firstExecution = true;
        getDeviceIDList();

        return root;
    }

    private void getDeviceIDList(){

        LinearLayout roomContainerLayout = root.findViewById(R.id.roomContainerLayout);

        DatabaseReference refreshDeviceIDListReference = FirebaseDatabase.getInstance().getReference("Home/98801/App");
        refreshDeviceIDListReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                if(onChildCount == 0){
                    roomContainerLayout.removeAllViews();
                }

                onChildCount++;

                Object childName = snapshot.getKey();
                int deviceID = Integer.parseInt(childName.toString());
                String deviceIDString = Integer.toString(deviceID);

                DatabaseReference homeIDListGenerateLayoutReference = FirebaseDatabase.getInstance().getReference("Home/98801/App/");
                homeIDListGenerateLayoutReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object alert = snapshot.child(childName.toString()).child("Alert").getValue();
                        String alertString = alert.toString();
                        int alertInt = Integer.parseInt(alertString);
                        Object area = snapshot.child(childName.toString()).child("Area").getValue();
                        String areaString = area.toString();

                        Drawable iconKitchenDrawable = getResources().getDrawable(R.drawable.kitchen);
                        Drawable iconDiningDrawable = getResources().getDrawable(R.drawable.dining);
                        Drawable iconLivingDrawable = getResources().getDrawable(R.drawable.room);
                        Drawable iconArrowRightDrawable = getResources().getDrawable(R.drawable.arrow_right);

                        float scale = getResources().getDisplayMetrics().density;

                        //new layout
                        int marginInDp = 15; // margin in dp
                        int marginInPx = (int) (marginInDp * scale + 0.5f);
                        LinearLayout newLinearLayout = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams newLinearLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        newLinearLayoutParams.setMargins(marginInPx,marginInPx,marginInPx,marginInPx);
                        newLinearLayout.setLayoutParams(newLinearLayoutParams);
                        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        roomContainerLayout.addView(newLinearLayout);

                        int iconHeightInDp = 60; // margin in dp
                        int iconHeightInPx = (int) (iconHeightInDp * scale + 0.5f);
                        int iconWeightInDp = 60; // margin in dp
                        int iconWeightInPx = (int) (iconWeightInDp * scale + 0.5f);
                        TextView roomIcon = new TextView(getActivity());
                        LinearLayout.LayoutParams roomIconParams = new LinearLayout.LayoutParams(
                                iconWeightInPx,
                                iconHeightInPx
                        );
                        roomIcon.setLayoutParams(roomIconParams);
                        if(areaString.equals("Kitchen")){
                            roomIcon.setBackground(iconKitchenDrawable);
                        }

                        else if(areaString.equals("Dining Area")){
                            roomIcon.setBackground(iconDiningDrawable);
                        }

                        else if(areaString.equals("Living Room")){
                            roomIcon.setBackground(iconLivingDrawable);
                        }
                        newLinearLayout.addView(roomIcon);

                        //content layout
                        LinearLayout contentLinearLayout = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams contentLinearLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        );
                        int paddingLeftInDp = 15; // margin in dp
                        int paddingLeftInPx = (int) (paddingLeftInDp * scale + 0.5f);
                        contentLinearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
                        contentLinearLayout.setPadding(paddingLeftInPx, 0,0,0);
                        contentLinearLayout.setLayoutParams(contentLinearLayoutParams);
                        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
                        newLinearLayout.addView(contentLinearLayout);

                        TextView areaTextView = new TextView(getActivity());
                        LinearLayout.LayoutParams areaTextViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        areaTextView.setLayoutParams(areaTextViewParams);
                        areaTextView.setText(areaString);
                        areaTextView.setTextColor(Color.BLACK);
                        contentLinearLayout.addView(areaTextView);

                        TextView deviceIDTextView = new TextView(getActivity());
                        LinearLayout.LayoutParams deviceIDTextViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        deviceIDTextView.setLayoutParams(deviceIDTextViewParams);
                        deviceIDTextView.setText(deviceIDString);
                        deviceIDTextView.setTextColor(Color.BLACK);
                        contentLinearLayout.addView(deviceIDTextView);

                        //Edit icon linear layout
                        LinearLayout editLinearLayout = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams editLinearLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        int paddingRightInDp = 10; // margin in dp
                        int paddingRightInPx = (int) (paddingRightInDp * scale + 0.5f);
                        editLinearLayout.setPadding(0, 0,paddingRightInPx,0);
                        editLinearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
                        editLinearLayout.setLayoutParams(editLinearLayoutParams);
                        newLinearLayout.addView(editLinearLayout);

                        int editIconHeightInDp = 30; // margin in dp
                        int editIconHeightInPx = (int) (editIconHeightInDp * scale + 0.5f);
                        int editIconWeightInDp = 30; // margin in dp
                        int editIconWeightInPx = (int) (editIconWeightInDp * scale + 0.5f);
                        TextView editIcon = new TextView(getActivity());
                        LinearLayout.LayoutParams editIconParams = new LinearLayout.LayoutParams(
                                editIconWeightInPx,
                                editIconHeightInPx
                        );
                        editIcon.setLayoutParams(editIconParams);
                        editIcon.setBackground(iconArrowRightDrawable);
                        editLinearLayout.addView(editIcon);

                        //Edit icon linear layout
                        LinearLayout verticalLine = new LinearLayout(getActivity());
                        LinearLayout.LayoutParams verticalLineParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1
                        );
                        int marginRightLeftInDp = 10; // margin in dp
                        int marginRightLeftInPx = (int) (marginRightLeftInDp * scale + 0.5f);
                        verticalLineParams.setMargins(marginRightLeftInPx, 0,marginRightLeftInPx,0);
                        verticalLine.setBackgroundColor(Color.parseColor("#9B9A9A"));
                        verticalLine.setLayoutParams(verticalLineParams);
                        roomContainerLayout.addView(verticalLine);

                        DatabaseReference appReference = FirebaseDatabase.getInstance().getReference("Home/98801/App/" + childName.toString());
                        appReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                                // Handle child added
                            }

                            @Override
                            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                                if (snapshot.getKey().equals("Smoke")) {
                                    Object smokeValue = snapshot.getValue();
                                    int smokeInt = Integer.parseInt(smokeValue.toString());
                                    String smokeAlert = "smoke";

                                    if(smokeInt > 200){
                                        showAlertNotification(areaString, smokeAlert, childName.toString());
                                    }

                                    else {
                                    }
                                }

                                if (snapshot.getKey().equals("Fire")) {
                                    Object fireValue = snapshot.getValue();
                                    int fireInt = Integer.parseInt(fireValue.toString());
                                    String fireAlert = "fire";

                                    if(fireInt == 0){
                                        showAlertNotification(areaString, fireAlert, childName.toString());
                                    }

                                    else {
                                    }
                                }

                                if (snapshot.getKey().equals("Area")) {
                                    Object areaValue = snapshot.getValue();

                                    areaTextView.setText(areaValue.toString());
                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot snapshot) {
                                // Handle child removed
                            }

                            @Override
                            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                                // Handle child moved
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Handle errors
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DatabaseReference homeIDListReference = FirebaseDatabase.getInstance().getReference("Home/98801/App");
                homeIDListReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        roomContainerLayout.removeAllViews();

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                            Object deviceID = childSnapshot.getKey();
                            String deviceIDString = deviceID.toString();
                            int deviceIDInt = Integer.parseInt(deviceIDString);

                            Object childValue = snapshot.child(deviceIDString).child("Area").getValue();
                            Object alertValue = snapshot.child(deviceIDString).child("Alert").getValue();
                            Object fire = snapshot.child(deviceIDString).child("Fire").getValue();
                            Object smoke = snapshot.child(deviceIDString).child("Smoke").getValue();
                            String areaString = childValue.toString();
                            String fireString = fire.toString();
                            String smokeString = smoke.toString();
                            int alertInt = Integer.parseInt(alertValue.toString());
                            int fireInt = Integer.parseInt(fireString);
                            int smokeInt = Integer.parseInt(smokeString);

                            Drawable drawableRoundedTopBorder = getResources().getDrawable(R.drawable.rounded_top_border_orange);
                            Drawable drawableRoundedBottomLeftBorder = getResources().getDrawable(R.drawable.rounded_bottom_left_border_white);
                            Drawable drawableRoundedBottomLeftBorderAlert = getResources().getDrawable(R.drawable.rounded_bottom_left_border_alert);
                            Drawable drawableRoundedBottomRightBorder = getResources().getDrawable(R.drawable.rounded_bottom_right_border_white);
                            Drawable drawableRoundedBottomRightBorderAlert = getResources().getDrawable(R.drawable.rounded_bottom_right_border_alert);
                            Drawable drawableRoundedBordersWhite = getResources().getDrawable(R.drawable.rounded_borders_gray);
                            Drawable drawableRoundedBordersOrange = getResources().getDrawable(R.drawable.rounded_borders_white_and_orange_background);
                            Drawable drawableSmokeIcon = getResources().getDrawable(R.drawable.smoke);
                            Drawable drawableSmokeAlertIcon = getResources().getDrawable(R.drawable.smoke_alert);
                            Drawable drawableFireIcon = getResources().getDrawable(R.drawable.fire);
                            Drawable drawableFireAlertIcon = getResources().getDrawable(R.drawable.fire_alert);
                            float scale = getResources().getDisplayMetrics().density;


                        }

                        String totalRoomString = Integer.toString(totalRoom);
                        TextView totalRoomTextview = root.findViewById(R.id.totalRoom);
                        totalRoomTextview.setText(totalRoomString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child moved within the App node
                // This method will not be triggered when a child is moved within the alert node
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }

            // implement other methods as needed
        });

        onChildCount = 0;
        loopChanges = 0;
    }

    private void showAlertNotification(String area, String fireOrSmoke, String deviceID){

        // Inflate the layout that contains the TextView
        View otherLayout = getLayoutInflater().inflate(R.layout.notification, null);

        // Get a reference to the TextView
        TextView message = otherLayout.findViewById(R.id.message);

        // Create a custom layout for the notification
        RemoteViews notificationLayout = new RemoteViews(getContext().getPackageName(), R.layout.notification);

        // Set the text for the notification
        notificationLayout.setTextViewText(R.id.message, "Attention there is a " + fireOrSmoke + " in the " + area + " having Device ID of " + deviceID);

        // Set the content view of the notification to the custom layout
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "channelID")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContent(notificationLayout);

        int notificationId = new Random().nextInt();

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(notificationId, builder.build());

    }
}