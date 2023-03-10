package com.example.fireandsmokealertsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

public class Home extends AppCompatActivity {

    DatabaseReference reference;
    boolean firstExecution = true, firstLoop = false;
    int numberOfLoops = 0, onChildCount = 0, loopChanges = 0, count = 0, totalRoom = 1, alertLoopSend = 0;
    private int linearLayoutId;
    private static final String CHANNEL_ID = "channelID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

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
    }

    private void getDeviceIDList(){

        LinearLayout new_layout_container = findViewById(R.id.new_layout_container);

        DatabaseReference homeIDListReference = FirebaseDatabase.getInstance().getReference("Home/98801/App");
        homeIDListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                totalRoom = 1;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object deviceID = childSnapshot.getKey();
                    String deviceIDString = deviceID.toString();
                    int deviceIDInt = Integer.parseInt(deviceIDString);

                    totalRoom++;

                    alertLoopSend = 0;

                    Drawable drawableSmokeIcon = getResources().getDrawable(R.drawable.smoke);
                    Drawable drawableSmokeAlertIcon = getResources().getDrawable(R.drawable.smoke_alert);
                    Drawable drawableFireIcon = getResources().getDrawable(R.drawable.fire);
                    Drawable drawableFireAlertIcon = getResources().getDrawable(R.drawable.fire_alert);


                }

                String totalRoomString = Integer.toString(totalRoom);
                TextView totalRoomTextview = findViewById(R.id.totalRoom);
                totalRoomTextview.setText(totalRoomString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference familyNameReference = FirebaseDatabase.getInstance().getReference("Home/98801/Information");
        familyNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                totalRoom = 1;

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object familyName = snapshot.child("Head").child("Last_name").getValue();
                    String familyNameString = familyName.toString();

                    totalRoom++;

                    TextView familyNameTextView = findViewById(R.id.familyName);
                    familyNameTextView.setText(familyNameString + "'s House");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference refreshDeviceIDListReference = FirebaseDatabase.getInstance().getReference("Home/98801/App");
        refreshDeviceIDListReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {

                if(onChildCount == 0){
                    new_layout_container.removeAllViews();
                }

                onChildCount++;

                Object childName = snapshot.getKey();
                int deviceID = Integer.parseInt(childName.toString());

                DatabaseReference homeIDListGenerateLayoutReference = FirebaseDatabase.getInstance().getReference("Home/98801/App/");
                homeIDListGenerateLayoutReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int countLoop = 0;

                        Object alert = snapshot.child(childName.toString()).child("Alert").getValue();
                        String alertString = alert.toString();
                        int alertInt = Integer.parseInt(alertString);
                        Object area = snapshot.child(childName.toString()).child("Area").getValue();
                        String areaString = area.toString();
                        Object fire = snapshot.child(childName.toString()).child("Fire").getValue();
                        String fireString = fire.toString();
                        int fireInt = Integer.parseInt(fireString);
                        Object smoke = snapshot.child(childName.toString()).child("Smoke").getValue();
                        String smokeString = smoke.toString();
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

                        if(loopChanges >= 2){
                            loopChanges = 0;
                        }

                        if(loopChanges == 0){

                            if(numberOfLoops >= 1){
                                //new layout row
                                int marginInDp = 10; // margin in dp
                                int marginInPx = (int) (marginInDp * scale + 0.5f);
                                LinearLayout rowLinearLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1
                                );
                                rowLinearLayoutParams.setMargins(marginInPx,0,marginInPx,0);
                                rowLinearLayout.setLayoutParams(rowLinearLayoutParams);
                                rowLinearLayout.setBackground(drawableRoundedBordersWhite);
                                rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                new_layout_container.addView(rowLinearLayout);
                            }

                            else {
                                //new layout row
                                int marginInDp = 10; // margin in dp
                                int marginInPx = (int) (marginInDp * scale + 0.5f);
                                LinearLayout rowLinearLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        1
                                );
                                rowLinearLayoutParams.setMargins(marginInPx,25,marginInPx,0);
                                rowLinearLayout.setLayoutParams(rowLinearLayoutParams);
                                rowLinearLayout.setBackground(drawableRoundedBordersWhite);
                                rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                new_layout_container.addView(rowLinearLayout);
                            }

                            numberOfLoops++;
                            count++;
                        }

                        if(!firstLoop){
                            int lastChildIndex = new_layout_container.getChildCount() - 1;
                            LinearLayout lastCreatedLayout = (LinearLayout) new_layout_container.getChildAt(lastChildIndex);

                            int heightInDp = 150; // margin in dp
                            int heightInPx = (int) (heightInDp * scale + 0.5f);
                            LinearLayout newLayout = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    heightInPx,
                                    1
                            );
                            newLayout.setOrientation(LinearLayout.VERTICAL);
                            int marginInDp = 10; // margin in dp
                            int marginInPx = (int) (marginInDp * scale + 0.5f);
                            newLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                            newLayout.setLayoutParams(newLayoutParams);
                            int elevationInDp = 8; // margin in dp
                            int elevationInPx = (int) (elevationInDp * scale + 0.5f);
                            newLayout.setElevation(elevationInPx);
                            newLayout.setBackground(drawableRoundedBordersOrange);
                            newLayout.setOrientation(LinearLayout.VERTICAL);
                            lastCreatedLayout.addView(newLayout);

                            TextView electricalRoomTextview = new TextView(Home.this);
                            LinearLayout.LayoutParams electricalRoomTextviewParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            electricalRoomTextview.setLayoutParams(electricalRoomTextviewParams);
                            electricalRoomTextview.setGravity(Gravity.CENTER);
                            electricalRoomTextview.setTextColor(Color.WHITE);
                            electricalRoomTextview.setTypeface(Typeface.DEFAULT_BOLD);
                            int textSizeInDp = 8; // margin in dp
                            int textSizeInPx = (int) (textSizeInDp * scale + 0.5f);
                            electricalRoomTextview.setTextSize(textSizeInPx);
                            electricalRoomTextview.setText("Electrical Room");
                            int paddingInDp = 5; // margin in dp
                            int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                            int paddingLeftRightInDp = 15; // margin in dp
                            int paddingLeftRightInPx = (int) (paddingLeftRightInDp * scale + 0.5f);
                            electricalRoomTextview.setPadding(paddingLeftRightInPx, paddingInPx, paddingLeftRightInPx, paddingInPx);
                            newLayout.addView(electricalRoomTextview);

                            firstLoop = true;
                            loopChanges++;
                        }

                        if(loopChanges >= 0){
                            int lastChildIndex = new_layout_container.getChildCount() - 1;
                            LinearLayout lastCreatedLayout = (LinearLayout) new_layout_container.getChildAt(lastChildIndex);

                            int heightInDp = 150; // margin in dp
                            int heightInPx = (int) (heightInDp * scale + 0.5f);
                            LinearLayout newLayout = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    heightInPx,
                                    1
                            );
                            newLayout.setOrientation(LinearLayout.VERTICAL);
                            int marginInDp = 10; // margin in dp
                            int marginInPx = (int) (marginInDp * scale + 0.5f);
                            newLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                            newLayout.setLayoutParams(newLayoutParams);
                            int elevationInDp = 8 ; // margin in dp
                            int elevationInPx = (int) (elevationInDp * scale + 0.5f);
                            newLayout.setElevation(elevationInPx);
                            newLayout.setBackground(drawableRoundedBordersWhite);
                            newLayout.setOrientation(LinearLayout.VERTICAL);
                            lastCreatedLayout.addView(newLayout);

                            LinearLayout newLayout1 = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams newLayoutParams1 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            newLayout1.setOrientation(LinearLayout.VERTICAL);
                            newLayout.setGravity(Gravity.CENTER);
                            int paddingInDp = 10; // margin in dp
                            int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                            newLayout1.setLayoutParams(newLayoutParams1);
                            newLayout1.setPadding(0,paddingInPx,0,paddingInPx);
                            newLayout1.setBackground(drawableRoundedTopBorder);
                            newLayout1.setOrientation(LinearLayout.VERTICAL);
                            newLayout.addView(newLayout1);

                            TextView areaTextview = new TextView(Home.this);
                            LinearLayout.LayoutParams areaTextviewParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            areaTextview.setLayoutParams(areaTextviewParams);
                            areaTextview.setGravity(Gravity.CENTER);
                            areaTextview.setTextColor(Color.WHITE);
                            int textSizeInDp = 8; // margin in dp
                            int textSizeInPx = (int) (textSizeInDp * scale + 0.5f);
                            areaTextview.setTextSize(textSizeInPx);
                            areaTextview.setTypeface(Typeface.DEFAULT_BOLD);
                            areaTextview.setText(areaString);
                            int paddingLeftInDp = 5; // margin in dp
                            int paddingLeftInPx = (int) (paddingLeftInDp * scale + 0.5f);
                            areaTextview.setPadding(paddingLeftInPx, 0, 0, 0);
                            newLayout1.addView(areaTextview);

                            int heightInDp1 = 1; // margin in dp
                            int heightInPx1 = (int) (heightInDp1 * scale + 0.5f);
                            LinearLayout horizontalLineLayout = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams horizontalLineLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    heightInPx1
                            );
                            horizontalLineLayout.setLayoutParams(horizontalLineLayoutParams);
                            horizontalLineLayout.setBackgroundColor(Color.parseColor("#E3E1E1"));
                            newLayout.addView(horizontalLineLayout);

                            LinearLayout sensorValueLayout = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams sensorValueLayoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            sensorValueLayout.setLayoutParams(sensorValueLayoutParams);
                            sensorValueLayout.setOrientation(LinearLayout.HORIZONTAL);
                            sensorValueLayout.setBackground(drawableRoundedBordersWhite);
                            newLayout.addView(sensorValueLayout);

                            //for smoke layout
                            LinearLayout sensorValueLayout1 = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams sensorValueLayout1Params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            sensorValueLayout1.setLayoutParams(sensorValueLayout1Params);
                            sensorValueLayout1.setGravity(Gravity.CENTER);
                            if(smokeInt > 200){
                                sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorderAlert);
                            }

                            else {
                                sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorder);
                            }

                            sensorValueLayout1.setOrientation(LinearLayout.VERTICAL);
                            sensorValueLayout.addView(sensorValueLayout1);

                            int widthInDp = 35; // margin in dp
                            int widthInPx = (int) (widthInDp * scale + 0.5f);
                            int heightInDp2 = 35; // margin in dp
                            int heightInPx2 = (int) (heightInDp2 * scale + 0.5f);
                            TextView smokeTextViewIcon = new TextView(Home.this);
                            LinearLayout.LayoutParams smokeTextViewIconParams = new LinearLayout.LayoutParams(
                                    widthInPx,
                                    heightInPx2
                            );
                            smokeTextViewIcon.setGravity(Gravity.CENTER);
                            linearLayoutId = View.generateViewId();
                            smokeTextViewIcon.setId(linearLayoutId);
                            smokeTextViewIcon.setLayoutParams(smokeTextViewIconParams);
                            if(smokeInt > 200){
                                smokeTextViewIcon.setBackground(drawableSmokeAlertIcon);
                            }

                            else {
                                smokeTextViewIcon.setBackground(drawableSmokeIcon);
                            }
                            smokeTextViewIcon.setTextColor(Color.BLACK);
                            sensorValueLayout1.addView(smokeTextViewIcon);

                            TextView smokeTextViewText = new TextView(Home.this);
                            LinearLayout.LayoutParams smokeTextViewTextParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            smokeTextViewText.setGravity(Gravity.CENTER);
                            int textSizeInDp1 = 6; // margin in dp
                            int textSizeInPx1 = (int) (textSizeInDp1 * scale + 0.5f);
                            int paddingTopInDp = 5; // margin in dp
                            int paddingTopInPx = (int) (paddingTopInDp * scale + 0.5f);
                            smokeTextViewText.setTextSize(textSizeInPx1);
                            smokeTextViewText.setPadding(0, paddingTopInPx,0,0);
                            smokeTextViewText.setLayoutParams(smokeTextViewTextParams);
                            if(smokeInt > 200){
                                smokeTextViewText.setTextColor(Color.WHITE);
                            }

                            else {
                                smokeTextViewText.setTextColor(Color.BLACK);
                            }
                            Typeface italicTypefaceSmoke = Typeface.defaultFromStyle(Typeface.ITALIC);
                            smokeTextViewText.setTypeface(italicTypefaceSmoke);
                            smokeTextViewText.setText(smokeString);
                            sensorValueLayout1.addView(smokeTextViewText);

                            int widthInDp1 = 1; // margin in dp
                            int widthInPx1 = (int) (widthInDp1 * scale + 0.5f);
                            LinearLayout verticalLineLayout = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams verticalLineLayoutParams = new LinearLayout.LayoutParams(
                                    widthInPx1,
                                    LinearLayout.LayoutParams.MATCH_PARENT
                            );
                            verticalLineLayout.setLayoutParams(verticalLineLayoutParams);
                            verticalLineLayout.setBackgroundColor(Color.parseColor("#E3E1E1"));
                            sensorValueLayout.addView(verticalLineLayout);

                            //for fire layout
                            LinearLayout sensorValueLayout2 = new LinearLayout(Home.this);
                            LinearLayout.LayoutParams sensorValueLayout2Params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            sensorValueLayout2.setLayoutParams(sensorValueLayout2Params);
                            sensorValueLayout2.setGravity(Gravity.CENTER);
                            if(fireInt == 0){
                                sensorValueLayout2.setBackground(drawableRoundedBottomRightBorderAlert);
                            }

                            else {
                                sensorValueLayout2.setBackground(drawableRoundedBottomRightBorder);
                            }
                            sensorValueLayout2.setOrientation(LinearLayout.VERTICAL);
                            sensorValueLayout.addView(sensorValueLayout2);

                            int widthInDp2 = 35; // margin in dp
                            int widthInPx2 = (int) (widthInDp2 * scale + 0.5f);
                            int heightInDp3 = 35; // margin in dp
                            int heightInPx3 = (int) (heightInDp3 * scale + 0.5f);
                            TextView fireTextViewIcon = new TextView(Home.this);
                            LinearLayout.LayoutParams fireTextViewIconParams = new LinearLayout.LayoutParams(
                                    widthInPx2,
                                    heightInPx3
                            );
                            fireTextViewIcon.setGravity(Gravity.CENTER);
                            fireTextViewIcon.setLayoutParams(fireTextViewIconParams);
                            if(fireInt == 0){
                                fireTextViewIcon.setBackground(drawableFireAlertIcon);
                            }

                            else {
                                fireTextViewIcon.setBackground(drawableFireIcon);
                            }
                            fireTextViewIcon.setTextColor(Color.BLACK);
                            sensorValueLayout2.addView(fireTextViewIcon);

                            TextView fireTextViewText = new TextView(Home.this);
                            LinearLayout.LayoutParams fireTextViewTextParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            fireTextViewText.setGravity(Gravity.CENTER);
                            int textSizeInDp2 = 6; // margin in dp
                            int textSizeInPx2 = (int) (textSizeInDp2 * scale + 0.5f);
                            int paddingTopInDp1 = 5; // margin in dp
                            int paddingTopInPx1 = (int) (paddingTopInDp1 * scale + 0.5f);
                            fireTextViewText.setTextSize(textSizeInPx2);
                            fireTextViewText.setPadding(0, paddingTopInPx1,0,0);
                            fireTextViewText.setLayoutParams(fireTextViewTextParams);
                            Typeface italicTypefaceFire = Typeface.defaultFromStyle(Typeface.ITALIC);
                            fireTextViewText.setTypeface(italicTypefaceFire);
                            if(fireInt == 0){
                                fireTextViewText.setTextColor(Color.WHITE);
                            }

                            else {
                                fireTextViewText.setTextColor(Color.BLACK);
                            }
                            fireTextViewText.setText(fireString);
                            sensorValueLayout2.addView(fireTextViewText);

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
                                            sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorderAlert);
                                            smokeTextViewIcon.setBackground(drawableSmokeAlertIcon);
                                            smokeTextViewText.setText(smokeValue.toString());
                                            smokeTextViewText.setTextColor(Color.WHITE);
                                        }

                                        else {
                                            sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorder);
                                            smokeTextViewIcon.setBackground(drawableSmokeIcon);
                                            smokeTextViewText.setText(smokeValue.toString());
                                            smokeTextViewText.setTextColor(Color.BLACK);
                                        }
                                    }

                                    if (snapshot.getKey().equals("Fire")) {
                                        Object fireValue = snapshot.getValue();
                                        int fireInt = Integer.parseInt(fireValue.toString());
                                        String fireAlert = "fire";

                                        if(fireInt == 0){
                                            showAlertNotification(areaString, fireAlert, childName.toString());
                                            sensorValueLayout2.setBackground(drawableRoundedBottomRightBorderAlert);
                                            fireTextViewIcon.setBackground(drawableFireAlertIcon);
                                            fireTextViewText.setText(fireValue.toString());
                                            fireTextViewText.setTextColor(Color.WHITE);
                                        }

                                        else {
                                            sensorValueLayout2.setBackground(drawableRoundedBottomRightBorder);
                                            fireTextViewIcon.setBackground(drawableFireIcon);
                                            fireTextViewText.setText(fireValue.toString());
                                            fireTextViewText.setTextColor(Color.BLACK);
                                        }
                                    }

                                    if (snapshot.getKey().equals("Area")) {
                                        Object areaValue = snapshot.getValue();

                                        areaTextview.setText(areaValue.toString());
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
                        loopChanges++;
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
                        new_layout_container.removeAllViews();

                        loopChanges = 0;
                        firstLoop = false;
                        totalRoom = 1;
                        numberOfLoops = 0;

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            totalRoom++;

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

                            if(loopChanges >= 2){
                                loopChanges = 0;
                            }

                            if(loopChanges == 0){

                                if(numberOfLoops >= 1){
                                    //new layout row
                                    int marginInDp = 10; // margin in dp
                                    int marginInPx = (int) (marginInDp * scale + 0.5f);
                                    LinearLayout rowLinearLayout = new LinearLayout(Home.this);
                                    LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1
                                    );
                                    rowLinearLayoutParams.setMargins(marginInPx,0,marginInPx,0);
                                    rowLinearLayout.setLayoutParams(rowLinearLayoutParams);
                                    rowLinearLayout.setBackground(drawableRoundedBordersWhite);
                                    rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    new_layout_container.addView(rowLinearLayout);
                                }

                                else {
                                    //new layout row
                                    int marginInDp = 10; // margin in dp
                                    int marginInPx = (int) (marginInDp * scale + 0.5f);
                                    LinearLayout rowLinearLayout = new LinearLayout(Home.this);
                                    LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1
                                    );
                                    rowLinearLayoutParams.setMargins(marginInPx,25,marginInPx,0);
                                    rowLinearLayout.setLayoutParams(rowLinearLayoutParams);
                                    rowLinearLayout.setBackground(drawableRoundedBordersWhite);
                                    rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    new_layout_container.addView(rowLinearLayout);
                                }

                                numberOfLoops++;
                                count++;
                            }

                            if(!firstLoop){

                                int lastChildIndex = new_layout_container.getChildCount() - 1;
                                LinearLayout lastCreatedLayout = (LinearLayout) new_layout_container.getChildAt(lastChildIndex);

                                int heightInDp = 150; // margin in dp
                                int heightInPx = (int) (heightInDp * scale + 0.5f);
                                LinearLayout newLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        heightInPx,
                                        1
                                );
                                newLayout.setOrientation(LinearLayout.VERTICAL);
                                int marginInDp = 10; // margin in dp
                                int marginInPx = (int) (marginInDp * scale + 0.5f);
                                newLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                                newLayout.setLayoutParams(newLayoutParams);
                                int elevationInDp = 8; // margin in dp
                                int elevationInPx = (int) (elevationInDp * scale + 0.5f);
                                newLayout.setElevation(elevationInPx);
                                newLayout.setBackground(drawableRoundedBordersOrange);
                                newLayout.setOrientation(LinearLayout.VERTICAL);
                                lastCreatedLayout.addView(newLayout);

                                TextView electricalRoomTextview = new TextView(Home.this);
                                LinearLayout.LayoutParams electricalRoomTextviewParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                electricalRoomTextview.setLayoutParams(electricalRoomTextviewParams);
                                electricalRoomTextview.setGravity(Gravity.CENTER);
                                electricalRoomTextview.setTextColor(Color.WHITE);
                                electricalRoomTextview.setTypeface(Typeface.DEFAULT_BOLD);
                                int textSizeInDp = 8; // margin in dp
                                int textSizeInPx = (int) (textSizeInDp * scale + 0.5f);
                                electricalRoomTextview.setTextSize(textSizeInPx);
                                electricalRoomTextview.setText("Electrical Room");
                                int paddingInDp = 5; // margin in dp
                                int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                                int paddingLeftRightInDp = 15; // margin in dp
                                int paddingLeftRightInPx = (int) (paddingLeftRightInDp * scale + 0.5f);
                                electricalRoomTextview.setPadding(paddingLeftRightInPx, paddingInPx, paddingLeftRightInPx, paddingInPx);
                                newLayout.addView(electricalRoomTextview);

                                firstLoop = true;
                                loopChanges++;
                            }

                            if(loopChanges >= 0){
                                int lastChildIndex = new_layout_container.getChildCount() - 1;
                                LinearLayout lastCreatedLayout = (LinearLayout) new_layout_container.getChildAt(lastChildIndex);

                                int heightInDp = 150; // margin in dp
                                int heightInPx = (int) (heightInDp * scale + 0.5f);
                                LinearLayout newLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        heightInPx,
                                        1
                                );
                                newLayout.setOrientation(LinearLayout.VERTICAL);
                                int marginInDp = 10; // margin in dp
                                int marginInPx = (int) (marginInDp * scale + 0.5f);
                                newLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                                newLayout.setLayoutParams(newLayoutParams);
                                int elevationInDp = 8 ; // margin in dp
                                int elevationInPx = (int) (elevationInDp * scale + 0.5f);
                                newLayout.setElevation(elevationInPx);
                                newLayout.setBackground(drawableRoundedBordersWhite);
                                newLayout.setOrientation(LinearLayout.VERTICAL);
                                lastCreatedLayout.addView(newLayout);

                                LinearLayout newLayout1 = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams newLayoutParams1 = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                newLayout1.setOrientation(LinearLayout.VERTICAL);
                                newLayout.setGravity(Gravity.CENTER);
                                int paddingInDp = 10; // margin in dp
                                int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                                newLayout1.setLayoutParams(newLayoutParams1);
                                newLayout1.setPadding(0,paddingInPx,0,paddingInPx);
                                newLayout1.setBackground(drawableRoundedTopBorder);
                                newLayout1.setOrientation(LinearLayout.VERTICAL);
                                newLayout.addView(newLayout1);

                                TextView areaTextview = new TextView(Home.this);
                                LinearLayout.LayoutParams areaTextviewParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                areaTextview.setLayoutParams(areaTextviewParams);
                                areaTextview.setGravity(Gravity.CENTER);
                                areaTextview.setTextColor(Color.WHITE);
                                int textSizeInDp = 8; // margin in dp
                                int textSizeInPx = (int) (textSizeInDp * scale + 0.5f);
                                areaTextview.setTextSize(textSizeInPx);
                                areaTextview.setTypeface(Typeface.DEFAULT_BOLD);
                                areaTextview.setText(areaString);
                                int paddingLeftInDp = 5; // margin in dp
                                int paddingLeftInPx = (int) (paddingLeftInDp * scale + 0.5f);
                                areaTextview.setPadding(paddingLeftInPx, 0, 0, 0);
                                newLayout1.addView(areaTextview);

                                int heightInDp1 = 1; // margin in dp
                                int heightInPx1 = (int) (heightInDp1 * scale + 0.5f);
                                LinearLayout horizontalLineLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams horizontalLineLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        heightInPx1
                                );
                                horizontalLineLayout.setLayoutParams(horizontalLineLayoutParams);
                                horizontalLineLayout.setBackgroundColor(Color.parseColor("#E3E1E1"));
                                newLayout.addView(horizontalLineLayout);

                                LinearLayout sensorValueLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams sensorValueLayoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                sensorValueLayout.setLayoutParams(sensorValueLayoutParams);
                                sensorValueLayout.setOrientation(LinearLayout.HORIZONTAL);
                                sensorValueLayout.setBackground(drawableRoundedBordersWhite);
                                newLayout.addView(sensorValueLayout);

                                //for smoke layout
                                LinearLayout sensorValueLayout1 = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams sensorValueLayout1Params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                sensorValueLayout1.setLayoutParams(sensorValueLayout1Params);
                                if(smokeInt > 200){
                                    sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorderAlert);
                                }

                                else {
                                    sensorValueLayout1.setBackground(drawableRoundedBottomLeftBorder);
                                }
                                sensorValueLayout1.setGravity(Gravity.CENTER);
                                sensorValueLayout1.setOrientation(LinearLayout.VERTICAL);
                                sensorValueLayout.addView(sensorValueLayout1);

                                int widthInDp = 35; // margin in dp
                                int widthInPx = (int) (widthInDp * scale + 0.5f);
                                int heightInDp2 = 35; // margin in dp
                                int heightInPx2 = (int) (heightInDp2 * scale + 0.5f);
                                TextView smokeTextViewIcon = new TextView(Home.this);
                                LinearLayout.LayoutParams smokeTextViewIconParams = new LinearLayout.LayoutParams(
                                        widthInPx,
                                        heightInPx2
                                );
                                smokeTextViewIcon.setGravity(Gravity.CENTER);
                                smokeTextViewIcon.setLayoutParams(smokeTextViewIconParams);
                                if(smokeInt > 200){
                                    smokeTextViewIcon.setBackground(drawableSmokeAlertIcon);
                                }

                                else {
                                    smokeTextViewIcon.setBackground(drawableSmokeIcon);
                                }
                                smokeTextViewIcon.setTextColor(Color.BLACK);
                                sensorValueLayout1.addView(smokeTextViewIcon);

                                TextView smokeTextViewText = new TextView(Home.this);
                                LinearLayout.LayoutParams smokeTextViewTextParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                smokeTextViewText.setGravity(Gravity.CENTER);
                                int textSizeInDp1 = 6; // margin in dp
                                int textSizeInPx1 = (int) (textSizeInDp1 * scale + 0.5f);
                                int paddingTopInDp = 5; // margin in dp
                                int paddingTopInPx = (int) (paddingTopInDp * scale + 0.5f);
                                smokeTextViewText.setTextSize(textSizeInPx1);
                                smokeTextViewText.setPadding(0, paddingTopInPx,0,0);
                                smokeTextViewText.setLayoutParams(smokeTextViewTextParams);
                                if(smokeInt > 200){
                                    smokeTextViewText.setTextColor(Color.WHITE);
                                }

                                else {
                                    smokeTextViewText.setTextColor(Color.BLACK);
                                }

                                Typeface italicTypefaceSmoke = Typeface.defaultFromStyle(Typeface.ITALIC);
                                smokeTextViewText.setTypeface(italicTypefaceSmoke);
                                smokeTextViewText.setText(smokeString);
                                sensorValueLayout1.addView(smokeTextViewText);

                                int widthInDp1 = 1; // margin in dp
                                int widthInPx1 = (int) (widthInDp1 * scale + 0.5f);
                                LinearLayout verticalLineLayout = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams verticalLineLayoutParams = new LinearLayout.LayoutParams(
                                        widthInPx1,
                                        LinearLayout.LayoutParams.MATCH_PARENT
                                );
                                verticalLineLayout.setLayoutParams(verticalLineLayoutParams);
                                verticalLineLayout.setBackgroundColor(Color.parseColor("#E3E1E1"));
                                sensorValueLayout.addView(verticalLineLayout);

                                //for fire layout
                                LinearLayout sensorValueLayout2 = new LinearLayout(Home.this);
                                LinearLayout.LayoutParams sensorValueLayout2Params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                sensorValueLayout2.setLayoutParams(sensorValueLayout2Params);
                                sensorValueLayout2.setGravity(Gravity.CENTER);
                                if(fireInt == 0){
                                    sensorValueLayout1.setBackground(drawableRoundedBottomRightBorderAlert);
                                }

                                else {
                                    sensorValueLayout1.setBackground(drawableRoundedBottomRightBorder);
                                }
                                sensorValueLayout2.setOrientation(LinearLayout.VERTICAL);
                                sensorValueLayout.addView(sensorValueLayout2);

                                int widthInDp2 = 35; // margin in dp
                                int widthInPx2 = (int) (widthInDp2 * scale + 0.5f);
                                int heightInDp3 = 35; // margin in dp
                                int heightInPx3 = (int) (heightInDp3 * scale + 0.5f);
                                TextView fireTextViewIcon = new TextView(Home.this);
                                LinearLayout.LayoutParams fireTextViewIconParams = new LinearLayout.LayoutParams(
                                        widthInPx2,
                                        heightInPx3
                                );
                                fireTextViewIcon.setGravity(Gravity.CENTER);
                                fireTextViewIcon.setLayoutParams(fireTextViewIconParams);
                                if(fireInt == 0){
                                    smokeTextViewIcon.setBackground(drawableFireAlertIcon);
                                }

                                else {
                                    fireTextViewIcon.setBackground(drawableFireIcon);
                                }
                                fireTextViewIcon.setTextColor(Color.BLACK);
                                sensorValueLayout2.addView(fireTextViewIcon);

                                TextView fireTextViewText = new TextView(Home.this);
                                LinearLayout.LayoutParams fireTextViewTextParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );
                                fireTextViewText.setGravity(Gravity.CENTER);
                                int textSizeInDp2 = 6; // margin in dp
                                int textSizeInPx2 = (int) (textSizeInDp2 * scale + 0.5f);
                                int paddingTopInDp1 = 5; // margin in dp
                                int paddingTopInPx1 = (int) (paddingTopInDp1 * scale + 0.5f);
                                fireTextViewText.setTextSize(textSizeInPx2);
                                fireTextViewText.setPadding(0, paddingTopInPx1,0,0);
                                fireTextViewText.setLayoutParams(fireTextViewTextParams);
                                Typeface italicTypefaceFire = Typeface.defaultFromStyle(Typeface.ITALIC);
                                fireTextViewText.setTypeface(italicTypefaceFire);
                                if(fireInt == 0){
                                    fireTextViewText.setTextColor(Color.WHITE);
                                }

                                else {
                                    fireTextViewText.setTextColor(Color.BLACK);
                                }

                                fireTextViewText.setText(fireString);
                                sensorValueLayout2.addView(fireTextViewText);
                            }
                            loopChanges++;
                        }

                        String totalRoomString = Integer.toString(totalRoom);
                        TextView totalRoomTextview = findViewById(R.id.totalRoom);
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
        firstLoop = false;
        loopChanges = 0;
        numberOfLoops = 0;

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, "channelID")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContent(notificationLayout);

        int notificationId = new Random().nextInt();

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);
        notificationManager.notify(notificationId, builder.build());

    }
}