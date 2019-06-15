package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.SharingsFileHandler;
import com.example.ofir.social_geha.AdminGivenData;
import com.example.ofir.social_geha.Identity.FictitiousIdentityGenerator;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.example.ofir.social_geha.Person.fromGenderEnumToGenderIndex;
import static com.example.ofir.social_geha.Person.fromReligionToReligionIndex;
import static com.example.ofir.social_geha.Person.fromStringToGenderEnum;
import static com.example.ofir.social_geha.Person.fromStringToReligion;
import static com.example.ofir.social_geha.Person.kindStringToKindEnum;
import static com.example.ofir.social_geha.Person.languagesEnumToLanguageString;
import static com.example.ofir.social_geha.Person.languagesStringToLanguageEnum;


public class SettingsInfoEditActivity extends AppCompatActivity {
    // All Clickable button's in the activity
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
    String initialName = "";
    private Person currentPerson;

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
        setUpFieldValues();
    }

    /**
     * Given a person p, the loaded fields (such as, name, bday etc.. will be already loaded and
     * initialized in the class fields.
     *
     * @param p the given preson
     */
    private void updateFieldsAccordingToPerson(Person p) {
        selectedLanguages = languagesEnumToLanguageString(p.getSpokenLanguages());
        for (int i = 0; i < languagesAll.length; i++) {
            languagesAll_BooleanSelection[i] = false;
        }
        for (int i = 0; i < languagesAll.length; i++) {
            Iterator iterator = selectedLanguages.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(languagesAll[i])) {
                    languagesAll_BooleanSelection[i] = true;
                }
            }
        }
        initialName = givenName = p.getRealName();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(p.getBirthDate()));
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        gender_index = fromGenderEnumToGenderIndex(p.getGender(), SettingsInfoEditActivity.this);
        gender = (gender_index != -1) ? allGenders[gender_index] : null;

        bio = p.getDescription();
        religion_index = fromReligionToReligionIndex(p.getReligion());
        religion = allReligions[religion_index];
    }

    /**
     * If the user already existed, calls updateFieldsAccordingToPerson to load his data
     */
    private void setUpFieldValues() {
        if (getIntent().getStringExtra("code") == null)
            Database.getInstance().getdb().collection("users").document(Database.getInstance().getLoggedInUserID()).get()
                    .addOnCompleteListener(task -> {
                        currentPerson = task.getResult().toObject(Person.class);
                        updateFieldsAccordingToPerson(currentPerson);
                    });
    }

    /**
     * When clicking done, the data will be written
     */
    private void setUpDoneButton() {
        doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(v -> {
            if (getIntent().getStringExtra("code") != null) {
                if (checkAllFilled()) {
                    // we need to preform register
                    calendar.set(year, month, dayOfMonth);
                    register(getIntent().getStringExtra("code"), givenName, calendar, gender,
                            religion, selectedLanguages.toArray(new String[]{}), bio);
                }
            } else {
                calendar.set(year, month, dayOfMonth);
                updateDBWithUserInfo(givenName, calendar, gender,
                        religion, selectedLanguages.toArray(new String[]{}), bio);
                if (!givenName.equals(initialName)) {
                    // send control messages
                    for (String uid : new SharingsFileHandler(SettingsInfoEditActivity.this).getUIDs())
                        Database.getInstance().sendControlMessage("NAME_CHANGE#" + givenName,
                                currentPerson.getUserID(), uid);
                }
                onBackPressed();
            }
        });
    }

    /**
     * Writes the given paramets to the user's db info
     *
     * @param givenName
     * @param calendar
     * @param gender
     * @param religion
     * @param langs
     * @param bio
     */
    private void updateDBWithUserInfo(String givenName, Calendar calendar, String gender, String religion, String[] langs, String bio) {
        currentPerson.setBirthDate(calendar.getTimeInMillis()).setDescription(bio).setRealName(givenName).setGender(fromStringToGenderEnum(gender, SettingsInfoEditActivity.this, false))
                .setReligion(fromStringToReligion(religion, false)).setSpokenLanguages(languagesStringToLanguageEnum(langs));
        Database.getInstance().addUserPerson(currentPerson);
    }

    /**
     * Performs Registration action after clicking done and goes to the main activity
     *
     * @param code
     * @param name
     * @param c
     * @param gender
     * @param religion
     * @param languages
     * @param bio
     */
    public void register(final String code, final String name, final Calendar c, final String gender,
                         final String religion, final String[] languages, final String bio) {
        final ProgressDialog progressDialog = new ProgressDialog(SettingsInfoEditActivity.this);
        progressDialog.setTitle(this.getString(R.string.strings_register));
        progressDialog.setMessage(this.getString(R.string.strings_please_wait));
        String email = code.concat("@geha-technion.temp.com");
        progressDialog.show();
        allGenders = getResources().getStringArray(R.array.gender_preferences);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, "ABCDDD123456")
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    long date = c.getTimeInMillis();
                    Person.Religion mReligion = fromStringToReligion(religion, false);
                    for (String lang : languages) {
                        Log.d("REGISTER_LANG", lang);
                    }

                    for (boolean flag : languagesAll_BooleanSelection) {
                        Log.d("REGISTER_LANG_CHECKED", String.valueOf(flag));
                    }

                    Log.d("REGISTER_DATE", String.valueOf(date));


                    List<Person.Language> spokenLanguages = languagesStringToLanguageEnum(languages);
                    AdminGivenData adminGivenData = (AdminGivenData) getIntent().getSerializableExtra("adminGivenData");
                    Person.Kind kind = kindStringToKindEnum(adminGivenData.getKind());
                    Person p = new Person(name, FictitiousIdentityGenerator.generateAnonymousIdentity(
                            fromStringToGenderEnum(gender, SettingsInfoEditActivity.this, false)),
                            date, fromStringToGenderEnum(gender, SettingsInfoEditActivity.this, false), mReligion, spokenLanguages,
                            kind, Database.getInstance().getLoggedInUserID(), bio, false, new ArrayList<Integer>());
                    p.setAdminGivenData(adminGivenData);
                    if (task.isSuccessful()) {
                        Database.getInstance().addUserPerson(p);
                        setResult(RESULT_OK);
                        finish();
                        Intent myIntent = new Intent(SettingsInfoEditActivity.this, activity_main_drawer.class);
                        SettingsInfoEditActivity.this.startActivity(myIntent);
                    } else {
                        Toast.makeText(SettingsInfoEditActivity.this, SettingsInfoEditActivity.this.getString(R.string.strings_reg_ex) +
                                task.getException(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(SettingsInfoEditActivity.this, "שם המשתמש כבר תפוס!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Button handler set up
     */
    private void setUpBioButton() {
        bioButton = findViewById(R.id.bio_button);
        bioButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this, R.style.AlertDialogCustomWhite);
            mBuilder.setTitle(R.string.additional_info);

            // Set up the input
            final EditText input = new EditText(SettingsInfoEditActivity.this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
            input.setText(bio);
            mBuilder.setView(input);

            // Set up the buttons
            mBuilder.setPositiveButton(R.string.ok_label, (dialog, which) -> bio = input.getText().toString());
            mBuilder.setNegativeButton(R.string.dismiss_label, (dialog, which) -> dialog.cancel());

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
    }

    /**
     * Button handler set up
     */
    private void setUpReligionButton() {
        allReligions = getResources().getStringArray(R.array.religious_preferences);
        religionButton = findViewById(R.id.religion_button);
        religionButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this, R.style.AlertDialogCustom);
            mBuilder.setTitle(R.string.religious);
            mBuilder.setSingleChoiceItems(allReligions, religion_index, (dialogInterface, i) -> {
                religion = (allReligions[i]);
                religion_index = i;
                dialogInterface.dismiss();
            });
            mBuilder.setPositiveButton(R.string.ok_label, (dialog, which) -> {

            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

    }

    /**
     * Button handler set up
     */
    private void setUpGenderButton() {
        allGenders = getResources().getStringArray(R.array.gender_preferences);
        genderButton = findViewById(R.id.gender_button);
        genderButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this, R.style.AlertDialogCustom);
            mBuilder.setTitle(R.string.gender);
            mBuilder.setSingleChoiceItems(allGenders, gender_index, (dialogInterface, i) -> {
                gender = (allGenders[i]);
                gender_index = i;
                dialogInterface.dismiss();
            });
            mBuilder.setPositiveButton(R.string.ok_label, (dialog, which) -> {

            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();

        });

    }

    /**
     * Button handler set up
     */
    private void setBdayButton() {
        bdayButton = findViewById(R.id.bday_button);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(SettingsInfoEditActivity.this,
                (datePicker, y, m, d) -> {
                    year = y;
                    month = m;
                    dayOfMonth = d;
                }, year, month, dayOfMonth);
        bdayButton.setOnClickListener(v -> datePickerDialog.show());
    }

    /**
     * Button handler set up
     */
    private void setUpNameButton() {
        nameButton = findViewById(R.id.name_button);
        nameButton.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this, R.style.AlertDialogCustomWhite);
            mBuilder.setTitle(R.string.settings_info_edit_name_btn);

            // Set up the input
            final EditText input = new EditText(SettingsInfoEditActivity.this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            input.setText(givenName);
            mBuilder.setView(input);

            // Set up the buttons
            mBuilder.setPositiveButton(R.string.ok_label, (dialog, which) -> givenName = input.getText().toString());
            mBuilder.setNegativeButton(R.string.dismiss_label, (dialog, which) -> dialog.cancel());

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });
    }

    /**
     * Button handler set up
     */
    private void setUpLangButton() {
        langButton = findViewById(R.id.language_button);
        languagesAll = getResources().getStringArray(R.array.languages);
        languagesAll_BooleanSelection = new boolean[languagesAll.length];

        langButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsInfoEditActivity.this, R.style.AlertDialogCustom);
                mBuilder.setMultiChoiceItems(languagesAll, languagesAll_BooleanSelection, (dialogInterface, position, isChecked) -> {
                    if (isChecked) {
                        selectedLanguages.add(languagesAll[position]);
                    } else {
                        selectedLanguages.remove(languagesAll[position]);
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, (dialogInterface, which) -> {
                    //TODO: Replace this with what to do on click
//                        String item = "";
//                        for (int i = 0; i < selectedLanguages.size(); i++) {
//                            item = item + languagesAll[selectedLanguages.get(i)];
//                            if (i != selectedLanguages.size() - 1) {
//                                item = item + ", ";
//                            }
//                        }
////                        mItemSelected.setText(item);
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, (dialogInterface, i) -> dialogInterface.dismiss());

                mBuilder.setNeutralButton(R.string.select_all_label, (dialogInterface, which) -> {
                    for (int i = 0; i < languagesAll_BooleanSelection.length; i++) {
                        languagesAll_BooleanSelection[i] = true;
                        selectedLanguages.add(languagesAll[i]);
//                            mItemSelected.setText("");
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    /**
     * Aux function to check all fields filled
     *
     * @return Whether all fields were filled
     */
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
            Toast.makeText(SettingsInfoEditActivity.this, error, Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }
}
