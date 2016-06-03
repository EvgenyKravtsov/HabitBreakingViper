package evgenykravtsov.habitbreaking.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.Utils;
import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.presenter.RegistrationViewPresenter;
import evgenykravtsov.habitbreaking.view.RegistrationView;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private static final int MAX_AGE = 120; // years

    private RegistrationViewPresenter presenter;
    private ProgressDialog progressDialog;

    //// BUTTERKNIFE BINDS

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
    @BindString(R.string.incorrect_email_warning)
    String incorrectEmailWarning;
    @BindString(R.string.incorrect_gender_warning)
    String incorrectGenderWarning;
    @BindString(R.string.incorrect_age_warning)
    String incorrectAgeWarning;

    @BindView(R.id.registration_activity_container_view)
    FrameLayout containerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.registration_activity_email_edit_text)
    EditText emailEditText;
    @BindView(R.id.registration_activity_age_edit_text)
    EditText ageEditText;
    @BindView(R.id.registration_activity_gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.registration_activity_male_radio_button)
    AppCompatRadioButton maleRadioButton;
    @BindView(R.id.registration_activity_female_radio_button)
    AppCompatRadioButton femaleRadioButton;
    @BindView(R.id.registration_activity_send_button)
    Button sendButton;

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
        String email = emailEditText.getText().toString();
        if (!validateEmail(email)) {
            notifyWrongEmail();
            return null;
        }

        int genderButtonId = genderRadioGroup.getCheckedRadioButtonId();
        View genderButton = genderRadioGroup.findViewById(genderButtonId);
        int genderIndex = genderRadioGroup.indexOfChild(genderButton);
        if (!validateGender(genderIndex)) {
            notifyNoGenderChosen();
            return null;
        }

        String age = ageEditText.getText().toString();
        if (!validateAge(age)) {
            notifyWrongAge();
            return null;
        }

        return new RegistrationDataEntity(email,
                genderIndex,
                Integer.parseInt(age),
                Utils.getCurrentTimeUnixSeconds());
    }

    private boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void notifyWrongEmail() {
        showSnackbar(incorrectEmailWarning);
    }

    private boolean validateGender(int genderIndex) {
        return genderIndex == 0 || genderIndex == 1;
    }

    private void notifyNoGenderChosen() {
        showSnackbar(incorrectGenderWarning);
    }

    private boolean validateAge(String age) {
        if (age.isEmpty()) {
            return false;
        }

        int ageAsInt = Integer.parseInt(age);
        return ageAsInt > 0 && ageAsInt < MAX_AGE;
    }

    private void notifyWrongAge() {
        showSnackbar(incorrectAgeWarning);
    }

    private void showSnackbar(String message) {
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void setFonts() {
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        emailEditText.setTypeface(robotoTypeface);
        ageEditText.setTypeface(robotoTypeface);
        maleRadioButton.setTypeface(robotoTypeface);
        femaleRadioButton.setTypeface(robotoTypeface);
    }
}
