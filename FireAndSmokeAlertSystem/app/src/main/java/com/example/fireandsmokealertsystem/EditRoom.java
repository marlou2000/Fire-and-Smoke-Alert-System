package com.example.fireandsmokealertsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditRoom extends AppCompatActivity {

    String deviceID;
    boolean changeClicked = false;
    int dropDownLoop = 0;
    boolean areaDropDownClicked = false;
    int selectedPosition = 0;
    String selectedArea = "";
    String areaValue = "";
    int currentAreaPosition = 0;

    List<String> area = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_room);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_items , area);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        deviceID = getIntent().getStringExtra("deviceID");

        LinearLayout areaLayout = findViewById(R.id.areaLayout);

        LinearLayout.LayoutParams areaLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        areaLayoutParams.setMargins(0,7,0,0);
        areaLayout.setLayoutParams(areaLayoutParams);

        getInformation();

        //change area button
        LinearLayout changeArea = findViewById(R.id.changeArea);
        TextView buttonText = findViewById(R.id.buttonText);

        changeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout cancelButtonLayout = findViewById(R.id.cancelButtonLayout);
                cancelButtonLayout.setVisibility(View.VISIBLE);
                float scale = getResources().getDisplayMetrics().density;

                if(!changeClicked){
                    area.clear();
                    Spinner spinner = findViewById(R.id.spinner);
                    spinner.setAdapter(adapter);

                    changeClicked = true;
                    TextView areaTextView = findViewById(R.id.area);

                    Button cancelButton = findViewById(R.id.cancelButton);
                    LinearLayout spinnerLayout = findViewById(R.id.spinnerLayout);
                    LinearLayout areaLayout = findViewById(R.id.areaLayout);

                    LinearLayout.LayoutParams areaLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    areaLayoutParams.setMargins(0,7,0,0);
                    areaLayout.setLayoutParams(areaLayoutParams);

                    cancelButton.setVisibility(View.VISIBLE);
                    areaTextView.setVisibility(View.GONE);
                    spinnerLayout.setVisibility(View.VISIBLE);

                    generateArea();

                    buttonText.setText("Save");
                }

                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditRoom.this);
                    builder.setMessage(R.string.popup_message)
                            .setTitle("Change Area")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    updateArea(selectedArea);

                                    changeClicked = false;
                                    TextView areaTextView = findViewById(R.id.area);

                                    areaTextView.setVisibility(View.VISIBLE);

                                    Button cancelButton = findViewById(R.id.cancelButton);
                                    LinearLayout spinnerLayout = findViewById(R.id.spinnerLayout);
                                    LinearLayout areaLayout = findViewById(R.id.areaLayout);

                                    LinearLayout.LayoutParams areaLayoutParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    areaLayoutParams.setMargins(0,7,0,0);
                                    areaLayout.setLayoutParams(areaLayoutParams);

                                    spinnerLayout.setVisibility(View.GONE);
                                    cancelButton.setVisibility(View.GONE);
                                    buttonText.setText("Change");
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        //cancel changes
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeClicked = false;
                TextView areaTextView = findViewById(R.id.area);
                LinearLayout spinnerLayout = findViewById(R.id.spinnerLayout);
                LinearLayout areaLayout = findViewById(R.id.areaLayout);

                LinearLayout.LayoutParams areaLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                areaLayoutParams.setMargins(0,7,0,0);
                areaLayout.setLayoutParams(areaLayoutParams);

                spinnerLayout.setVisibility(View.GONE);
                areaTextView.setVisibility(View.VISIBLE);

                buttonText.setText("Change");

                cancelButton.setVisibility(View.GONE);
            }
        });

        //bottom navigations button
        LinearLayout dashBoardButton = findViewById(R.id.dashboard);
        dashBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDashboard = new Intent(EditRoom.this, Dashboard.class);
                startActivity(intentDashboard);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout roomsButton = findViewById(R.id.rooms );
        roomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRooms = new Intent(EditRoom.this, Rooms.class);
                startActivity(intentRooms);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout settingsButton = findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSettings = new Intent(EditRoom.this, Settings.class);
                startActivity(intentSettings);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });
    }

    private void getInformation(){

        DatabaseReference familyNameReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID/" + deviceID);
        familyNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object area = snapshot.child("Area").getValue();
                Object fire = snapshot.child("Fire").getValue();
                Object smoke = snapshot.child("Smoke").getValue();
                areaValue = area.toString();

                TextView deviceIDTextView = findViewById(R.id.deviceID);
                TextView areaTextView = findViewById(R.id.area);
                TextView fireTextView = findViewById(R.id.fire);
                TextView smokeTextView = findViewById(R.id.smoke);

                deviceIDTextView.setText(deviceID);
                areaTextView.setText(area.toString());
                fireTextView.setText(fire.toString());
                smokeTextView.setText(smoke.toString() + " PPM");

                DatabaseReference appReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/DeviceID/" + deviceID);
                appReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                        // Handle child added
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                        if (snapshot.getKey().equals("Smoke")) {
                            Object smokeValue = snapshot.getValue();
                            smokeTextView.setText(smokeValue.toString() + " PPM");
                        }

                        if (snapshot.getKey().equals("Fire")) {
                            Object fireValue = snapshot.getValue();
                            fireTextView.setText(fireValue.toString());
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

    private void generateArea(){

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_items , area);

        dropDownLoop = 0;

        DatabaseReference familyNameReference = FirebaseDatabase.getInstance().getReference("Home/Area");
        familyNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object areaObject = childSnapshot.getValue();
                    area.add(areaObject.toString());
                }

                for (int i = 0; i < area.size(); i++) {
                    if (area.get(i).equals(areaValue)) {
                        currentAreaPosition = i;
                        break;
                    }
                }

                LinearLayout areaLayout = findViewById(R.id.areaLayout);

                LinearLayout.LayoutParams areaLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                areaLayoutParams.setMargins(0,0,0,0);
                areaLayout.setLayoutParams(areaLayoutParams);

                Spinner spinner = findViewById(R.id.spinner);
                spinner.setAdapter(adapter);
                spinner.setSelection(currentAreaPosition);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedPosition = position;
                        selectedArea =  parent.getItemAtPosition(selectedPosition).toString();
                        spinner.setSelection(selectedPosition);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateArea(String selectedAreaReceived){
        // Get a reference to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference areaReference = database.getReference("Home/HomeID/98801/DeviceID/" + deviceID);

        Toast.makeText(this, selectedAreaReceived, Toast.LENGTH_SHORT).show();
        // Update the value of the data
        areaReference.child("Area").setValue(selectedAreaReceived);
    }
}