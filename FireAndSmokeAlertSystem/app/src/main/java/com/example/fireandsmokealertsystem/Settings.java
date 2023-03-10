package com.example.fireandsmokealertsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    boolean numberLayoutClicked = false, changePincodeLayoutClicked = false, informationLayoutClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);

        //click event for list of numbers
        TextView numberTextView = findViewById(R.id.numberTextView);
        numberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout numberContainer = findViewById(R.id.numberContainer);

                if(!numberLayoutClicked){
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24);
                    Drawable[] drawables = numberTextView.getCompoundDrawables();
                    numberTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    numberContainer.setVisibility(View.VISIBLE);

                    numberLayoutClicked = true;
                }

                else {
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_right_24);
                    Drawable[] drawables = numberTextView.getCompoundDrawables();
                    numberTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    numberContainer.setVisibility(View.GONE);

                    numberLayoutClicked = false;
                }

                displayNumber();
            }
        });

        //click event for change pin code
        TextView pincodeTextView = findViewById(R.id.pincodeTextView);
        pincodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout changePincodeInputCointaner = findViewById(R.id.changePincodeInputCointaner);

                if(!changePincodeLayoutClicked){
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24);
                    Drawable[] drawables = pincodeTextView.getCompoundDrawables();
                    pincodeTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    changePincodeInputCointaner.setVisibility(View.VISIBLE);

                    changePincodeLayoutClicked = true;
                }

                else {
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_right_24);
                    Drawable[] drawables = pincodeTextView.getCompoundDrawables();
                    pincodeTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    changePincodeInputCointaner.setVisibility(View.GONE);

                    changePincodeLayoutClicked = false;
                }
            }
        });

        //click event for information
        TextView houseInformationTextView = findViewById(R.id.houseInformationTextView);
        houseInformationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout informationCointaner = findViewById(R.id.informationCointaner);

                if(!informationLayoutClicked){
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_drop_down_24);
                    Drawable[] drawables = houseInformationTextView.getCompoundDrawables();
                    houseInformationTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    informationCointaner.setVisibility(View.VISIBLE);

                    informationLayoutClicked = true;
                }

                else {
                    Drawable arrowDownIcon = getResources().getDrawable(R.drawable.baseline_arrow_right_24);
                    Drawable[] drawables = houseInformationTextView.getCompoundDrawables();
                    houseInformationTextView.setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], arrowDownIcon, drawables[3]);

                    informationCointaner.setVisibility(View.GONE);

                    informationLayoutClicked = false;
                }
            }
        });


        //bottom navigations button
        LinearLayout dashBoardButton = findViewById(R.id.dashboard);
        dashBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDashboard = new Intent(Settings.this, Dashboard.class);
                startActivity(intentDashboard);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });

        LinearLayout roomsButton = findViewById(R.id.rooms);
        roomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRooms = new Intent(Settings.this, Rooms.class);
                startActivity(intentRooms);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out   );
                finish();
            }
        });
    }

    private void displayNumber(){
        LinearLayout numberContainer = findViewById(R.id.numberContainer);

        DatabaseReference homeIDListGenerateLayoutReference = FirebaseDatabase.getInstance().getReference("Home/HomeID/98801/Numbers");
        homeIDListGenerateLayoutReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberContainer.removeAllViews();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object number = childSnapshot.getValue();
                    String numberString = number.toString();

                    LinearLayout newLayout = new LinearLayout(Settings.this);
                    LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    newLayout.setLayoutParams(newLayoutParams);
                    numberContainer.addView(newLayout);

                    TextView numberTextview = new TextView(Settings.this);
                    LinearLayout.LayoutParams numberTextviewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );
                    numberTextview.setLayoutParams(numberTextviewParams);
                    numberTextview.setText(numberString);
                    numberTextview.setAllCaps(false);
                    numberTextview.setTextColor(Color.BLACK);
                    Drawable editIcon = getResources().getDrawable(R.drawable.baseline_edit_24);
                    Drawable[] drawables = numberTextview.getCompoundDrawables();
                    numberTextview.setCompoundDrawablesWithIntrinsicBounds(editIcon, drawables[1], drawables[2], drawables[3]);
                    newLayout.addView(numberTextview);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}