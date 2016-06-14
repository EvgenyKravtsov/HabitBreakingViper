package evgenykravtsov.habitbreaking.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.Utils;
import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.presenter.RegistrationViewPresenter;
import evgenykravtsov.habitbreaking.view.RegistrationView;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private RegistrationViewPresenter presenter;
    private ProgressDialog progressDialog;
    private Calendar dateOfBirth;

    //// BUTTERKNIFE BINDS

    @BindDrawable(R.drawable.date_picker_divider)
    Drawable datePickerDivider;

    @BindString(R.string.no_internet_connection_warning)
    String noInternetConnectionWarning;
    @BindString(R.string.registration_success_message)
    String registrationSuccessMessage;
    @BindString(R.string.email_already_registered_waring)
    String emailAlreadyRegisteredWarning;
    @BindString(R.string.server_side_error_warning)
    String serverSideErrorWarning;
    @BindString(R.string.sending_process_warning)
    String sendingProcessWarning;
    @BindString(R.string.incorrect_name_warning)
    String incorrectNameWarning;
    @BindString(R.string.incorrect_gender_warning)
    String incorrectGenderWarning;
    @BindString(R.string.incorrect_date_of_birth_warning)
    String incorrectDateOfBirthWarning;
    @BindString(R.string.hint_date_of_birth)
    String hintDateOfBitrh;

    @BindView(R.id.registration_activity_container_view)
    FrameLayout containerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.registration_activity_name_edit_text)
    EditText nameEditText;
    @BindView(R.id.registration_activity_date_of_birth_button)
    Button dateOfBirthButton;
    @BindView(R.id.registration_activity_gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.registration_activity_male_radio_button)
    AppCompatRadioButton maleRadioButton;
    @BindView(R.id.registration_activity_female_radio_button)
    AppCompatRadioButton femaleRadioButton;
    @BindView(R.id.registration_activity_secret_question_spinner)
    Spinner secretQuestionSpinner;
    @BindView(R.id.registration_activity_send_button)
    Button sendButton;

    @OnClick(R.id.registration_activity_date_of_birth_button)
    public void onClickDateOfBirthButton() {
        showDateOfBirthDialog();
    }

    @OnClick(R.id.registration_activity_send_button)
    public void onClickSendButton() {
        RegistrationDataEntity registrationDataEntity = getRegistrationDataFromViews();

        if (registrationDataEntity != null) {
            presenter.processRegistrationData(getRegistrationDataFromViews());
        }
    }

    //// ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        prepareToolbar();
        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareSecretQuestionSpinner();
        presenter = new RegistrationViewPresenter();
        presenter.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbind();
        presenter = null;
    }

    //// MENU LIFECYCLE

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    //// REGISTRATION VIEW INTERFACE

    @Override
    public void notifyNoInternetConnection() {
        hideProgress();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(noInternetConnectionWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyRegistrationSuccess() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(registrationSuccessMessage)
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegistrationActivity.this.finish();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void notifyDuplicateUserName() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(emailAlreadyRegisteredWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyConnectionError() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(serverSideErrorWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(sendingProcessWarning);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    //// PRIVATE METHODS

    private void prepareToolbar() {
        toolbar.setNavigationIcon(R.drawable.navigation_arrow);
        setSupportActionBar(toolbar);
    }

    private RegistrationDataEntity getRegistrationDataFromViews() {
        String name = nameEditText.getText().toString();
        if (!validateName(name)) {
            notifyWrongName();
            return null;
        }

        int genderButtonId = genderRadioGroup.getCheckedRadioButtonId();
        View genderButton = genderRadioGroup.findViewById(genderButtonId);
        int genderIndex = genderRadioGroup.indexOfChild(genderButton);
        if (!validateGender(genderIndex)) {
            notifyNoGenderChosen();
            return null;
        }

        if (!validateDateOfBirth()) {
            notifyWrongDateOfBirth();
            return null;
        }

        return new RegistrationDataEntity(name,
                genderIndex,
                dateOfBirth.getTimeInMillis() / 1000,
                Utils.getCurrentTimeUnixSeconds());
    }

    private boolean validateName(String name) {
        return !name.isEmpty();
    }

    private void notifyWrongName() {
        showSnackbar(incorrectNameWarning);
    }

    private boolean validateGender(int genderIndex) {
        return genderIndex == 0 || genderIndex == 1;
    }

    private void notifyNoGenderChosen() {
        showSnackbar(incorrectGenderWarning);
    }

    private boolean validateDateOfBirth() {
        return !dateOfBirthButton.getText().toString().equals(hintDateOfBitrh);
    }

    private void notifyWrongDateOfBirth() {
        showSnackbar(incorrectDateOfBirthWarning);
    }

    private void showSnackbar(String message) {
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void showDateOfBirthDialog() {
        @SuppressLint("InflateParams") View datePicketView = (DatePicker) getLayoutInflater()
                .inflate(R.layout.picker_date, null);

        final DatePicker datePicker = (DatePicker) datePicketView;
        changeDatePickerDividerColor(datePicker);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(datePicketView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        dateOfBirth = calendar;
                        dateOfBirthButton.setText(dateToFormattedString(calendar.getTime()));

                        // TODO Delete test code
                        Log.d(AppController.APP_TAG, dateToFormattedString(dateOfBirth.getTime()));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
    }

    private void changeDatePickerDividerColor(DatePicker datePicker) {
        LinearLayout linearLayoutFirst = (LinearLayout) datePicker.getChildAt(0);
        LinearLayout linearLayoutSecond = (LinearLayout) linearLayoutFirst.getChildAt(0);

        for (int i = 0; i < linearLayoutSecond.getChildCount(); i++) {
            NumberPicker numberPicker = (NumberPicker) linearLayoutSecond.getChildAt(i);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();

            for (Field field : pickerFields) {
                if (field.getName().equals("mSelectionDivider")) {
                    field.setAccessible(true);

                    try {
                        field.set(numberPicker, datePickerDivider);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }
    }

    private String dateToFormattedString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        return simpleDateFormat.format(date);
    }

    private void prepareSecretQuestionSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.secret_question_array, R.layout.spinner_item_secret_question);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_secret_question);
        secretQuestionSpinner.setAdapter(adapter);
    }

    private void setFonts() {
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        nameEditText.setTypeface(robotoTypeface);
        dateOfBirthButton.setTypeface(robotoTypeface);
        maleRadioButton.setTypeface(robotoTypeface);
        femaleRadioButton.setTypeface(robotoTypeface);
    }
}




























