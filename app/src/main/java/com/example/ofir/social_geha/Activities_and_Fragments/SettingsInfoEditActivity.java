package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ofir.social_geha.R;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;


public class SettingsInfoEditActivity extends AppCompatActivity {
    Button nameButton;
    Button langButton;
    Button bdayButton;
    Button genderButton;
    Button religionButton;
    Button bioButton;
    Button doneButton;

    //Language related fields
    String[] languagesAll;
    boolean[] langCheckedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    //Name related Fields
    private String givenName;

    //Date related Fields
    Calendar calendar = Calendar.getInstance();
    int year;
    int month;
    int dayOfMonth;
    DatePickerDialog datePickerDialog;

    //Gender related Fields
    String[] allGenders;
    String gender;
    int gender_index =-1;

    //Religion related Fields
    String[] allReligions;
    String religion;
    int religion_index =-1;

    //Bio related Fields
    String bio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_info_edit);

        setUpNameButton();
        setBdayButton();
        setUpLangButton();
        setUpGenderButton();
        setUpReligionButton();
        setUpBioButton();
        setUpDoneButton();
    }

    private void setUpDoneButton() {
        doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //TODO: check if this is the right way to return to main menu
                Intent myIntent = new Intent(SettingsInfoEditActivity.this, mainScreen.class);
                SettingsInfoEditActivity.this.startActivity(myIntent);
            }
        });
    }

    private void setUpBioButton() {
        bioButton = findViewById(R.id.bio_button);
        bioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this,R.style.AlertDialogCustomWhite);
                mBuilder.setTitle(R.string.additional_info);

                // Set up the input
                final EditText input = new EditText(SettingsInfoEditActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                input.setText(bio);
                mBuilder.setView(input);

                // Set up the buttons
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bio = input.getText().toString();
                    }
                });
                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private void setUpReligionButton() {
        allReligions = getResources().getStringArray(R.array.religious_preferences);
        religionButton = findViewById(R.id.religion_button);
        religionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this,R.style.AlertDialogCustom);
                mBuilder.setTitle(R.string.religious);
                mBuilder.setSingleChoiceItems(allReligions, religion_index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        religion = (allReligions[i]);
                        religion_index = i;
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

    }

    private void setUpGenderButton() {
        allGenders = getResources().getStringArray(R.array.gender_preferences);
        genderButton = findViewById(R.id.gender_button);
        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this,R.style.AlertDialogCustom);
                mBuilder.setTitle(R.string.gender);
                mBuilder.setSingleChoiceItems(allGenders, gender_index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gender = (allGenders[i]);
                        gender_index = i;
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

    }
    

    private void setBdayButton() {
        bdayButton = (Button) findViewById(R.id.bday_button);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

         datePickerDialog = new DatePickerDialog(SettingsInfoEditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        year = y;
                        month = m;
                        dayOfMonth=d;
                    }
                }, year, month, dayOfMonth);
        bdayButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void setUpNameButton() {
        nameButton = (Button) findViewById(R.id.name_button);
        nameButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this,R.style.AlertDialogCustomWhite);
                mBuilder.setTitle(R.string.settings_info_edit_name_btn);

                // Set up the input
                final EditText input = new EditText(SettingsInfoEditActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setText(givenName);
                mBuilder.setView(input);

                // Set up the buttons
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        givenName = input.getText().toString();
                    }
                });
                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private void setUpLangButton() {
        langButton = (Button) findViewById(R.id.language_button);
        languagesAll = getResources().getStringArray(R.array.languages);
        langCheckedItems = new boolean[languagesAll.length];

        langButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this,R.style.AlertDialogCustom);
                mBuilder.setMultiChoiceItems(languagesAll, langCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            mUserItems.add(position);
                        } else {
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //TODO: Replace this with what to do on click
//                        String item = "";
//                        for (int i = 0; i < mUserItems.size(); i++) {
//                            item = item + languagesAll[mUserItems.get(i)];
//                            if (i != mUserItems.size() - 1) {
//                                item = item + ", ";
//                            }
//                        }
////                        mItemSelected.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < langCheckedItems.length; i++) {
                            langCheckedItems[i] = false;
                            mUserItems.clear();
//                            mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }
}
