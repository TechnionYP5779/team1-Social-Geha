package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ofir.social_geha.FictitiousIdentityGenerator;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.ofir.social_geha.Person.fromGenderEnumToGenderIndex;
import static com.example.ofir.social_geha.Person.fromReligionToReligionIndex;
import static com.example.ofir.social_geha.Person.fromStringToGenderEnum;
import static com.example.ofir.social_geha.Person.fromStringToReligion;
import static com.example.ofir.social_geha.Person.languagesEnumToLanguageString;
import static com.example.ofir.social_geha.Person.languagesStringToLanguageEnum;


public class EditInfoFragment extends Fragment {
    Button nameButton;
    Button langButton;
    Button bdayButton;
    Button genderButton;
    Button religionButton;
    Button bioButton;
    Button doneButton;

    //Language relshated fields
    String[] languagesAll;
    boolean[] languagesAll_BooleanSelection;
    List<String> selectedLanguages = new ArrayList<>();

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
    int gender_index = -1;

    //Religion related Fields
    String[] allReligions;
    String religion;
    int religion_index = -1;

    //Bio related Fields
    String bio;
    private Person currentPerson;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((activity_main_drawer)getActivity()).setActionBarTitle("עריכת מידע");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_info, container, false);
        setUpNameButton(v);
        setBdayButton(v);
        setUpLangButton(v);
        setUpGenderButton(v);
        setUpReligionButton(v);
        setUpBioButton(v);
        setUpDoneButton(v);
        setUpFieldValues();
        return v;
    }


    private void updateFieldsAccordingToPerson(Person p) {
        selectedLanguages = languagesEnumToLanguageString(p.getSpokenLanguages());
        for (int i = 0; i < languagesAll.length; i++) {
            languagesAll_BooleanSelection[i] = false;
        }
        for (int i = 0; i < languagesAll.length; i++) {
            for (String selectedLanguage : selectedLanguages) {
                if (selectedLanguage.equals(languagesAll[i])) {
                    languagesAll_BooleanSelection[i] = true;
                }
            }
        }
        givenName = p.getRealName();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(p.getBirthDate()));
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        gender_index = fromGenderEnumToGenderIndex(p.getGender(), getActivity());
        gender = (gender_index != -1) ? allGenders[gender_index] : null;

        bio = p.getDescription();
        religion_index = fromReligionToReligionIndex(p.getReligion());
        religion = allReligions[religion_index];
    }

    private void setUpFieldValues() {
    Database.getInstance().getdb().collection("users").document(Database.getInstance().getLoggedInUserID()).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    currentPerson = task.getResult().toObject(Person.class);
                    updateFieldsAccordingToPerson(currentPerson);
                }
            });
    }

    private void setUpDoneButton(View v_parent) {
        doneButton = v_parent.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(year, month, dayOfMonth);
                updateDBWithUserInfo(givenName, calendar, gender,
                        religion, selectedLanguages.toArray(new String[]{}), bio);
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flMain,new HomeFragment(),((activity_main_drawer)getActivity()).ALL_CHATS_TAG);
                Toast.makeText(getActivity(),"הפרטים עודכנו בהצלחה",Toast.LENGTH_SHORT).show();
                ((activity_main_drawer)getActivity()).selectNavigationDrawer(R.id.nav_inbox);
                ft.commit();

            }
        });
    }

    private void updateDBWithUserInfo(String givenName, Calendar calendar, String gender, String religion, String[] langs, String bio) {
        currentPerson.setBirthDate(calendar.getTimeInMillis()).setDescription(bio).setRealName(givenName).setGender(fromStringToGenderEnum(gender, getActivity(), false))
                .setReligion(fromStringToReligion(religion, false)).setSpokenLanguages(languagesStringToLanguageEnum(langs));
        Database.getInstance().addUserPerson(currentPerson);
    }

    private void setUpBioButton(View v) {
        bioButton = v.findViewById(R.id.bio_button);
        bioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustomWhite);
                mBuilder.setTitle(R.string.additional_info);

                // Set up the input
                final EditText input = new EditText(getActivity());
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

    private void setUpReligionButton(View v) {
        allReligions = getResources().getStringArray(R.array.religious_preferences);
        religionButton = v.findViewById(R.id.religion_button);
        religionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
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

    private void setUpGenderButton(View v) {
        allGenders = getResources().getStringArray(R.array.gender_preferences);
        genderButton = v.findViewById(R.id.gender_button);
        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
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

    private void setBdayButton(View v) {
        bdayButton = (Button) v.findViewById(R.id.bday_button);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        year = y;
                        month = m;
                        dayOfMonth = d;
                    }
                }, year, month, dayOfMonth);
        bdayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void setUpNameButton(View v) {
        nameButton = (Button) v.findViewById(R.id.name_button);
        nameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustomWhite);
                mBuilder.setTitle(R.string.settings_info_edit_name_btn);

                // Set up the input
                final EditText input = new EditText(getActivity());
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

    private void setUpLangButton(View v) {
        langButton = (Button) v.findViewById(R.id.language_button);
        languagesAll = getResources().getStringArray(R.array.languages);
        languagesAll_BooleanSelection = new boolean[languagesAll.length];

        langButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
                mBuilder.setMultiChoiceItems(languagesAll, languagesAll_BooleanSelection, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (isChecked) {
                            selectedLanguages.add(languagesAll[position]);
                        } else {
                            selectedLanguages.remove(languagesAll[position]);
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //TODO: Replace this with what to do on click
//                        String item = "";
//                        for (int i = 0; i < selectedLanguages.size(); i++) {
//                            item = item + languagesAll[selectedLanguages.get(i)];
//                            if (i != selectedLanguages.size() - 1) {
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

                mBuilder.setNeutralButton(R.string.select_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < languagesAll_BooleanSelection.length; i++) {
                            languagesAll_BooleanSelection[i] = true;
                            selectedLanguages.add(languagesAll[i]);
//                            mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private boolean checkAllFilled() {
        String error = "";
        if (selectedLanguages.size() == 0)
            error = "אנא בחר לפחות שפה אחת";

        if (givenName == null || givenName.equals(""))
            error = "אנא מלא את שמך";

        if (gender == null || gender.equals(""))
            error = "אנא בחר מגדר";

        if (religion == null || religion.equals(""))
            error = "אנא בחר דת";

        if (bio == null || bio.equals(""))
            error = "אנא מלא משפט קצר אודותיך";

        if (!error.equals("")) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }
}
