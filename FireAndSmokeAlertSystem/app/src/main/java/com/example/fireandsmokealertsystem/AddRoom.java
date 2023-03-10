package com.example.fireandsmokealertsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRoom extends AppCompatActivity {

    int dropDownLoop = 0;
    boolean areaDropDownClicked = false;
    boolean deviceIDDropDownClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_room);

        Drawable input_layoutDrawable = getResources().getDrawable(R.drawable.input_layout);
        Drawable input_layout_clickedDrawable = getResources().getDrawable(R.drawable.input_layout_clicked);

        LinearLayout selectArea = findViewById(R.id.selectArea);
        LinearLayout areaListLayout = findViewById(R.id.areaListLayout);
        LinearLayout selectDeviceID = findViewById(R.id.selectDeviceID);
        LinearLayout deviceIDListLayout = findViewById(R.id.deviceIDListLayout);

        selectArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaListLayout.removeAllViews();
                
                if(!areaDropDownClicked){
                    areaDropDownClicked = true;
                    selectArea.setBackground(input_layout_clickedDrawable);
                    areaListLayout.setVisibility(View.VISIBLE);
                    generateArea();
                }

                else {
                    areaDropDownClicked = false;
                    selectArea.setBackground(input_layoutDrawable);
                    areaListLayout.setVisibility(View.GONE);
                }

            }
        });


        selectDeviceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deviceIDListLayout.removeAllViews();

                if(!deviceIDDropDownClicked){
                    deviceIDDropDownClicked = true;
                    selectDeviceID.setBackground(input_layout_clickedDrawable);
                    deviceIDListLayout.setVisibility(View.VISIBLE);
                    generateDeviceID();
                }

                else {
                    deviceIDDropDownClicked = false;
                    selectDeviceID.setBackground(input_layoutDrawable);
                    deviceIDListLayout.setVisibility(View.GONE);
                }

            }
        });

        //bottom navigations button
        LinearLayout dashBoardButton = findViewById(R.id.dashboard);
        dashBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDashboard = new Intent(AddRoom.this, Dashboard.class);
                startActivity(intentDashboard);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout roomsButton = findViewById(R.id.rooms );
        roomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRooms = new Intent(AddRoom.this, Rooms.class);
                startActivity(intentRooms);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSettings = new Intent(AddRoom.this, Settings.class);
                startActivity(intentSettings);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });
    }

    private void generateArea(){
        LinearLayout areaListLayout = findViewById(R.id.areaListLayout);

        dropDownLoop = 0;

        DatabaseReference familyNameReference = FirebaseDatabase.getInstance().getReference("Home/Area");
        familyNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object area = childSnapshot.getValue();

                    float scale = getResources().getDisplayMetrics().density;

                    TextView areaTextView = new TextView(AddRoom.this);
                    LinearLayout.LayoutParams areaTextViewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );
                    areaTextView.setLayoutParams(areaTextViewParams);
                    areaTextViewParams.gravity = Gravity.CENTER;
                    areaTextView.setTextColor(Color.parseColor("#FF6A16"));
                    areaTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    if(dropDownLoop == 0){
                        int paddingInDp = 15; // margin in dp
                        int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                        areaTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
                        dropDownLoop++;
                    }

                    else {
                        int paddingLeftBottomRightInDp = 15; // margin in dp
                        int paddingLeftBottomRightInPx = (int) (paddingLeftBottomRightInDp * scale + 0.5f);
                        areaTextView.setPadding(paddingLeftBottomRightInPx, 0, paddingLeftBottomRightInPx, paddingLeftBottomRightInPx);
                    }
                    areaTextView.setText(area.toString());
                    TypedValue outValue = new TypedValue();
                    AddRoom.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    areaTextView.setBackgroundResource(outValue.resourceId);
                    areaTextView.setClickable(true);
                    areaListLayout.addView(areaTextView);

                    areaTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout selectArea = findViewById(R.id.selectArea);
                            Drawable input_layoutDrawable = getResources().getDrawable(R.drawable.input_layout);
                            selectArea.setBackground(input_layoutDrawable);
                            areaListLayout.removeAllViews();
                            areaDropDownClicked = false;
                            TextView areaValue = findViewById(R.id.areaValue);
                            areaValue.setText(area.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateDeviceID(){
        LinearLayout deviceIDListLayout = findViewById(R.id.deviceIDListLayout);

        dropDownLoop = 0;

        DatabaseReference familyNameReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID");
        familyNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object deviceID = childSnapshot.getKey();

                    float scale = getResources().getDisplayMetrics().density;

                    TextView deviceIDTextView = new TextView(AddRoom.this);
                    LinearLayout.LayoutParams deviceIDTextViewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );
                    deviceIDTextView.setLayoutParams(deviceIDTextViewParams);
                    deviceIDTextViewParams.gravity = Gravity.CENTER;
                    deviceIDTextView.setTextColor(Color.parseColor("#FF6A16"));
                    deviceIDTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    if(dropDownLoop == 0){
                        int paddingInDp = 15; // margin in dp
                        int paddingInPx = (int) (paddingInDp * scale + 0.5f);
                        deviceIDTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
                        dropDownLoop++;
                    }

                    else {
                        int paddingLeftBottomRightInDp = 15; // margin in dp
                        int paddingLeftBottomRightInPx = (int) (paddingLeftBottomRightInDp * scale + 0.5f);
                        deviceIDTextView.setPadding(paddingLeftBottomRightInPx, 0, paddingLeftBottomRightInPx, paddingLeftBottomRightInPx);
                    }
                    deviceIDTextView.setClickable(true);
                    TypedValue outValue = new TypedValue();
                    AddRoom.this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    deviceIDTextView.setBackgroundResource(outValue.resourceId);
                    deviceIDTextView.setText(deviceID.toString());
                    deviceIDListLayout.addView(deviceIDTextView);

                    deviceIDTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout selectDeviceID = findViewById(R.id.selectDeviceID);
                            Drawable input_layoutDrawable = getResources().getDrawable(R.drawable.input_layout);
                            selectDeviceID.setBackground(input_layoutDrawable);
                            deviceIDListLayout.removeAllViews();
                            deviceIDDropDownClicked = false;
                            TextView deviceIDValue = findViewById(R.id.deviceIDValue);
                            deviceIDValue.setText(deviceID.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}