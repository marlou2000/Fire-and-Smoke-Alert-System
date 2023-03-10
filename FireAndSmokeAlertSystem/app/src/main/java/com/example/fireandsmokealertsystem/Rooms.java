package com.example.fireandsmokealertsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Rooms extends AppCompatActivity {

    DatabaseReference reference;
    View root;
    boolean firstExecution = true, firstLoop = false;
    int numberOfLoops = 0, onChildCount = 0, loopChanges = 0, count = 0, totalRoom = 1, alertLoopSend = 0;
    private int linearLayoutId;
    private static final String CHANNEL_ID = "channelID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rooms);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notificationChannel";
            String description = "Alert notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager;
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        firstExecution = true;
        getDeviceIDList();

        //bottom navigations button
        LinearLayout dashBoardButton = findViewById(R.id.dashboard);
        dashBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDashboard = new Intent(Rooms.this, Dashboard.class);
                startActivity(intentDashboard);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSettings = new Intent(Rooms.this, Settings.class);
                startActivity(intentSettings);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });
    }

    private void getDeviceIDList(){

        LinearLayout roomContainerLayout = findViewById(R.id.roomContainerLayout);

        DatabaseReference refreshDeviceIDListReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID");
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

                DatabaseReference homeIDListGenerateLayoutReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID/");
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
                        Drawable iconLivingDrawable = getResources().getDrawable(R.drawable.living_room);
                        Drawable iconBedRoomDrawable = getResources().getDrawable(R.drawable.room);
                        Drawable iconArrowRightDrawable = getResources().getDrawable(R.drawable.arrow_right);

                        float scale = getResources().getDisplayMetrics().density;

                        //new layout
                        int paddingDp = 15; // margin in dp
                        int paddingPx = (int) (paddingDp * scale + 0.5f);
                        LinearLayout newLinearLayout = new LinearLayout(Rooms.this);
                        LinearLayout.LayoutParams newLinearLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        newLinearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                        newLinearLayout.setLayoutParams(newLinearLayoutParams);
                        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        TypedValue outValue = new TypedValue();
                        Rooms.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        newLinearLayout.setBackgroundResource(outValue.resourceId);
                        newLinearLayout.setClickable(true);
                        roomContainerLayout.addView(newLinearLayout);

                        int iconHeightInDp = 40; // margin in dp
                        int iconHeightInPx = (int) (iconHeightInDp * scale + 0.5f);
                        int iconWeightInDp = 40; // margin in dp
                        int iconWeightInPx = (int) (iconWeightInDp * scale + 0.5f);
                        TextView roomIcon = new TextView(Rooms.this);
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

                        else if(areaString.equals("Bed Room")){
                            roomIcon.setBackground(iconBedRoomDrawable);
                        }
                        newLinearLayout.addView(roomIcon);

                        //content layout
                        LinearLayout contentLinearLayout = new LinearLayout(Rooms.this);
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

                        TextView areaTextView = new TextView(Rooms.this);
                        LinearLayout.LayoutParams areaTextViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        areaTextView.setLayoutParams(areaTextViewParams);
                        areaTextView.setText(areaString);
                        int textSizeAreaInDp = 6; // margin in dp
                        int textSizeAreaInPx = (int) (textSizeAreaInDp * scale + 0.5f);
                        areaTextView.setTextSize(textSizeAreaInPx);
                        areaTextView.setTextColor(Color.BLACK);
                        contentLinearLayout.addView(areaTextView);

                        TextView deviceIDTextView = new TextView(Rooms.this);
                        LinearLayout.LayoutParams deviceIDTextViewParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        deviceIDTextView.setLayoutParams(deviceIDTextViewParams);
                        deviceIDTextView.setText(deviceIDString);
                        int textSizeDeviceIDInDp = 6; // margin in dp
                        int textSizeDeviceIDInPx = (int) (textSizeDeviceIDInDp * scale + 0.5f);
                        deviceIDTextView.setTextSize(textSizeDeviceIDInPx);
                        deviceIDTextView.setTextColor(Color.BLACK);
                        contentLinearLayout.addView(deviceIDTextView);

                        //Edit icon linear layout
                        LinearLayout editLinearLayout = new LinearLayout(Rooms.this);
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

                        int editIconHeightInDp = 20; // margin in dp
                        int editIconHeightInPx = (int) (editIconHeightInDp * scale + 0.5f);
                        int editIconWeightInDp = 20; // margin in dp
                        int editIconWeightInPx = (int) (editIconWeightInDp * scale + 0.5f);
                        TextView editIcon = new TextView(Rooms.this);
                        LinearLayout.LayoutParams editIconParams = new LinearLayout.LayoutParams(
                                editIconWeightInPx,
                                editIconHeightInPx
                        );
                        editIcon.setLayoutParams(editIconParams);
                        editIcon.setBackground(iconArrowRightDrawable);
                        editLinearLayout.addView(editIcon);

                        //Edit icon linear layout
                        LinearLayout verticalLine = new LinearLayout(Rooms.this);
                        LinearLayout.LayoutParams verticalLineParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1
                        );
                        verticalLine.setBackgroundColor(Color.parseColor("#9B9A9A"));
                        verticalLine.setLayoutParams(verticalLineParams);
                        roomContainerLayout.addView(verticalLine);

                        newLinearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intentEditRoom = new Intent(Rooms.this, EditRoom.class);
                                intentEditRoom.putExtra("deviceID", deviceIDString);
                                startActivity(intentEditRoom);
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
                DatabaseReference homeIDListReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID");
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

                            Drawable iconKitchenDrawable = getResources().getDrawable(R.drawable.kitchen);
                            Drawable iconDiningDrawable = getResources().getDrawable(R.drawable.dining);
                            Drawable iconLivingDrawable = getResources().getDrawable(R.drawable.living_room);
                            Drawable iconBedRoomDrawable = getResources().getDrawable(R.drawable.room);
                            Drawable iconArrowRightDrawable = getResources().getDrawable(R.drawable.arrow_right);
                            float scale = getResources().getDisplayMetrics().density;

                            //new layout
                            //new layout
                            int paddingDp = 15; // margin in dp
                            int paddingPx = (int) (paddingDp * scale + 0.5f);
                            LinearLayout newLinearLayout = new LinearLayout(Rooms.this);
                            LinearLayout.LayoutParams newLinearLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            newLinearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
                            newLinearLayout.setLayoutParams(newLinearLayoutParams);
                            newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            TypedValue outValue = new TypedValue();
                            Rooms.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                            newLinearLayout.setBackgroundResource(outValue.resourceId);
                            newLinearLayout.setClickable(true);
                            roomContainerLayout.addView(newLinearLayout);

                            int iconHeightInDp = 40; // margin in dp
                            int iconHeightInPx = (int) (iconHeightInDp * scale + 0.5f);
                            int iconWeightInDp = 40; // margin in dp
                            int iconWeightInPx = (int) (iconWeightInDp * scale + 0.5f);
                            TextView roomIcon = new TextView(Rooms.this);
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

                            else if(areaString.equals("Bed Room")){
                                roomIcon.setBackground(iconBedRoomDrawable);
                            }
                            newLinearLayout.addView(roomIcon);

                            //content layout
                            LinearLayout contentLinearLayout = new LinearLayout(Rooms.this);
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

                            TextView areaTextView = new TextView(Rooms.this);
                            LinearLayout.LayoutParams areaTextViewParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            areaTextView.setLayoutParams(areaTextViewParams);
                            areaTextView.setText(areaString);
                            int textSizeAreaInDp = 6; // margin in dp
                            int textSizeAreaInPx = (int) (textSizeAreaInDp * scale + 0.5f);
                            areaTextView.setTextSize(textSizeAreaInPx);
                            areaTextView.setTextColor(Color.BLACK);
                            contentLinearLayout.addView(areaTextView);

                            TextView deviceIDTextView = new TextView(Rooms.this);
                            LinearLayout.LayoutParams deviceIDTextViewParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            deviceIDTextView.setLayoutParams(deviceIDTextViewParams);
                            deviceIDTextView.setText(deviceIDString);
                            int textSizeDeviceIDInDp = 6; // margin in dp
                            int textSizeDeviceIDInPx = (int) (textSizeDeviceIDInDp * scale + 0.5f);
                            deviceIDTextView.setTextSize(textSizeDeviceIDInPx);
                            deviceIDTextView.setTextColor(Color.BLACK);
                            contentLinearLayout.addView(deviceIDTextView);

                            //Edit icon linear layout
                            LinearLayout editLinearLayout = new LinearLayout(Rooms.this);
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

                            int editIconHeightInDp = 20; // margin in dp
                            int editIconHeightInPx = (int) (editIconHeightInDp * scale + 0.5f);
                            int editIconWeightInDp = 20; // margin in dp
                            int editIconWeightInPx = (int) (editIconWeightInDp * scale + 0.5f);
                            TextView editIcon = new TextView(Rooms.this);
                            LinearLayout.LayoutParams editIconParams = new LinearLayout.LayoutParams(
                                    editIconWeightInPx,
                                    editIconHeightInPx
                            );
                            editIcon.setLayoutParams(editIconParams);
                            editIcon.setBackground(iconArrowRightDrawable);
                            editLinearLayout.addView(editIcon);

                            //Edit icon linear layout
                            LinearLayout verticalLine = new LinearLayout(Rooms.this);
                            LinearLayout.LayoutParams verticalLineParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            verticalLine.setBackgroundColor(Color.parseColor("#9B9A9A"));
                            verticalLine.setLayoutParams(verticalLineParams);
                            roomContainerLayout.addView(verticalLine);

                            newLinearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intentEditRoom = new Intent(Rooms.this, EditRoom.class);
                                    intentEditRoom.putExtra("deviceID", deviceIDString);
                                    startActivity(intentEditRoom);
                                }
                            });

                        }
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
        RemoteViews notificationLayout = new RemoteViews(this.getPackageName(), R.layout.notification);

        // Set the text for the notification
        notificationLayout.setTextViewText(R.id.message, "Attention there is a " + fireOrSmoke + " in the " + area + " having Device ID of " + deviceID);

        // Set the content view of the notification to the custom layout
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Rooms.this, "channelID")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContent(notificationLayout);

        int notificationId = new Random().nextInt();

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Rooms.this);
        notificationManager.notify(notificationId, builder.build());

    }
}