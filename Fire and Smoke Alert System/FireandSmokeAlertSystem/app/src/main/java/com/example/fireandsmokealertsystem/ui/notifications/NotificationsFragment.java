package com.example.fireandsmokealertsystem.ui.notifications;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.fireandsmokealertsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationsFragment extends Fragment {

    boolean numberLayoutClicked = false, changePincodeLayoutClicked = false, informationLayoutClicked = false;

    View root;
    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);

        //click event for list of numbers
        TextView numberTextView = root.findViewById(R.id.numberTextView);
        numberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout numberContainer = root.findViewById(R.id.numberContainer);

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
        TextView pincodeTextView = root.findViewById(R.id.pincodeTextView);
        pincodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout changePincodeInputCointaner = root.findViewById(R.id.changePincodeInputCointaner);

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
        TextView houseInformationTextView = root.findViewById(R.id.houseInformationTextView);
        houseInformationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout informationCointaner = root.findViewById(R.id.informationCointaner);

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

        return root;
    }

    private void displayNumber(){
        LinearLayout numberContainer = root.findViewById(R.id.numberContainer);

        DatabaseReference homeIDListGenerateLayoutReference = FirebaseDatabase.getInstance().getReference("Home/98801/Numbers");
        homeIDListGenerateLayoutReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberContainer.removeAllViews();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Object number = childSnapshot.getValue();
                    String numberString = number.toString();

                    LinearLayout newLayout = new LinearLayout(getActivity());
                    LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    newLayout.setLayoutParams(newLayoutParams);
                    numberContainer.addView(newLayout);

                    TextView numberTextview = new TextView(getActivity());
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