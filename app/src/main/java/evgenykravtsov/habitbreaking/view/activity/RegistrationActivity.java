package evgenykravtsov.habitbreaking.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindDrawable;
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

    public static final String KEY_STATISTIC_RESTORE_INTENT = "key_statistic_restore_intent";

    private RegistrationViewPresenter presenter;
    private ProgressDialog progressDialog;
    private Calendar dateOfBirth;
    private boolean statisticRestoreIntent;

    //// BUTTERKNIFE BINDS

    @BindDrawable(R.drawable.date_picker_divider)
    Drawable datePickerDivider;

    @BindString(R.string.registration_activity_label_for_statistic_restore)
    String activityLabelForStatisticRestore;
    @BindString(R.string.registration_process_hint)
    String registrationProcessHint;
    @BindString(R.string.restore_process_hint)
    String restoreProcessHint;
    @BindString(R.string.no_internet_connection_warning)
    String noInternetConnectionWarning;
    @BindString(R.string.no_data_for_user_warning)
    String noDataForUserWarning;
    @BindString(R.string.registration_success_message)
    String registrationSuccessMessage;
    @BindString(R.string.statistic_restored_warning)
    String statisticRestoredWarning;
    @BindString(R.string.server_side_error_warning)
    String serverSideErrorWarning;
    @BindString(R.string.sending_process_warning)
    String sendingProcessWarning;
    @BindString(R.string.incorrect_name_warning)
    String incorrectNameWarning;
    @BindString(R.string.incorrect_date_of_birth_warning)
    String incorrectDateOfBirthWarning;
    @BindString(R.string.incorrect_gender_warning)
    String incorrectGenderWarning;
    @BindString(R.string.incorrect_secret_answer_warning)
    String incorrectSecretAnswerWarning;
    @BindString(R.string.hint_date_of_birth)
    String hintDateOfBitrh;
    @BindString(R.string.hint_month)
    String hintMonth;
    @BindString(R.string.hint_secret_question)
    String hintSecretQuestion;

    @BindView(R.id.registration_activity_container_view)
    FrameLayout containerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.registration_activity_name_edit_text)
    EditText nameEditText;
    @BindView(R.id.registration_activity_date_of_birth_hint_text_view)
    TextView dateOfBirthHintTextView;
    @BindView(R.id.registration_activity_day_edit_text)
    EditText dayEditText;
    @BindView(R.id.registration_activity_month_spinner)
    Spinner monthSpinner;
    @BindView(R.id.registration_activity_year_edit_text)
    EditText yearEditText;
    @BindView(R.id.registration_activity_gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.registration_activity_gender_hint_text_view)
    TextView genderHintTextView;
    @BindView(R.id.registration_activity_male_radio_button)
    AppCompatRadioButton maleRadioButton;
    @BindView(R.id.registration_activity_female_radio_button)
    AppCompatRadioButton femaleRadioButton;
    @BindView(R.id.registration_activity_secret_question_spinner)
    Spinner secretQuestionSpinner;
    @BindView(R.id.registration_activity_secret_answer_edit_text)
    EditText secretAnswerEditText;
    @BindView(R.id.registration_activity_send_button)
    Button sendButton;

    @OnClick(R.id.registration_activity_send_button)
    public void onClickSendButton() {
        RegistrationDataEntity registrationDataEntity = getRegistrationDataFromViews();

        if (registrationDataEntity != null) {
            if (statisticRestoreIntent) {
                presenter.processRestorationData(registrationDataEntity);
            } else {
                presenter.processRegistrationData(registrationDataEntity);
            }
        }
    }

    //// ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        Intent startingIntent = getIntent();
        Bundle bundle = startingIntent.getExtras();
        if (bundle != null) {
            statisticRestoreIntent = bundle.getBoolean(KEY_STATISTIC_RESTORE_INTENT);
        }

        prepareToolbar();
        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareMonthSpinner();
        prepareSecretQuestionSpinner();
        presenter = new RegistrationViewPresenter();
        presenter.bind(this);
        showActivityProcessHint();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbind();
        presenter = null;
    }

    ////

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrCoordinates[] = new int[2];
            w.getLocationOnScreen(scrCoordinates);
            float x = event.getRawX() + w.getLeft() - scrCoordinates[0];
            float y = event.getRawY() + w.getTop() - scrCoordinates[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
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
                        presenter.sendStatistic();
                        RegistrationActivity.this.finish();
                    }
                })
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
    public void notifyNoStatisticForUser() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(noDataForUserWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        hideProgress();
    }

    @Override
    public void notifyStatisticRestored() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticRestoredWarning)
                .setPositiveButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RegistrationActivity.this.finish();
                            }
                        })
                .create();
        dialog.show();
        hideProgress();
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

        if (statisticRestoreIntent) {
            toolbar.setTitle(activityLabelForStatisticRestore);
        }

        setSupportActionBar(toolbar);
    }

    private void prepareMonthSpinner() {
        String[] spinnerStrings = getResources().getStringArray(R.array.month_array);
        final Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        prepareRegistrationSpinner(monthSpinner, spinnerStrings, hintMonth, robotoTypeface);
    }

    private void prepareSecretQuestionSpinner() {
        String[] spinnerStrings = getResources().getStringArray(R.array.secret_question_array);
        final Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        prepareRegistrationSpinner(secretQuestionSpinner, spinnerStrings, hintSecretQuestion, robotoTypeface);
    }

    private void prepareRegistrationSpinner(Spinner spinner, String[] spinnerStrings,
                                            String hint, final Typeface typeface) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_registration) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                ((TextView) view).setTypeface(typeface);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view =  super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTypeface(typeface);
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_registration);
        for (String str : spinnerStrings) {
            adapter.add(str);
        }
        adapter.add(hint);

        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }

    private RegistrationDataEntity getRegistrationDataFromViews() {
        String name = nameEditText.getText().toString();
        if (!validateName(name)) {
            notifyWrongName();
            return null;
        }

        if (!validateDateOfBirth()) {
            notifyWrongDateOfBirth();
            return null;
        }

        int genderButtonId = genderRadioGroup.getCheckedRadioButtonId();
        View genderButton = genderRadioGroup.findViewById(genderButtonId);
        int genderIndex = genderRadioGroup.indexOfChild(genderButton);
        if (!validateGender(genderIndex)) {
            notifyNoGenderChosen();
            return null;
        }

        if (!validateSecretAnswer()) {
            notifyNoSecretAnswer();
            return null;
        }

        return new RegistrationDataEntity(name,
                genderIndex,
                dateOfBirth.getTimeInMillis() / 1000,
                Utils.getCurrentTimeUnixSeconds(),
                secretQuestionSpinner.getSelectedItem().toString(),
                secretAnswerEditText.getText().toString());
    }

    private boolean validateName(String name) {
        return !name.isEmpty();
    }

    private void notifyWrongName() {
        showSnackbar(incorrectNameWarning);
    }

    private boolean validateDateOfBirth() {
        String[] months = getResources().getStringArray(R.array.month_array);
        List<String> day31group = Arrays.asList(months[0], months[2], months[4], months[6],
                months[7], months[9], months[11]);
        List<String> day30group = Arrays.asList(months[3], months[5], months[8], months[10]);
        List<String> day29group = Collections.singletonList(months[1]);

        if (monthSpinner.getSelectedItem().toString().equals(hintMonth)) return false;

        if (dayEditText.getText().toString().equals("")) return false;
        int selectedDay = Integer.parseInt(dayEditText.getText().toString());
        if (selectedDay < 1) return false;

        String selectedMonth = monthSpinner.getSelectedItem().toString();
        if (day31group.contains(selectedMonth) && selectedDay > 31) return false;
        if (day30group.contains(selectedMonth) && selectedDay > 30) return false;
        if (day29group.contains(selectedMonth) && selectedDay > 29) return false;

        if (yearEditText.getText().toString().equals("")) return false;
        int selectedYear = Integer.parseInt(yearEditText.getText().toString());
        if (selectedYear < 1900 || selectedYear > Calendar.getInstance().get(Calendar.YEAR)) return false;

        int selectedMonthAsInt = 0;
        for (int i = 0; i < months.length - 1; i++) {
            if (selectedMonth.equals(months[i])) {
                selectedMonthAsInt = i;
            }
        }

        dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(Calendar.YEAR, selectedYear);
        dateOfBirth.set(Calendar.MONTH, selectedMonthAsInt);
        dateOfBirth.set(Calendar.DAY_OF_MONTH, selectedDay);
        dateOfBirth.set(Calendar.HOUR_OF_DAY, 0);
        dateOfBirth.set(Calendar.MINUTE, 0);
        dateOfBirth.set(Calendar.SECOND, 0);

        return true;
    }

    private void notifyWrongDateOfBirth() {
        showSnackbar(incorrectDateOfBirthWarning);
    }

    private boolean validateGender(int genderIndex) {
        return genderIndex == 0 || genderIndex == 1;
    }

    private void notifyNoGenderChosen() {
        showSnackbar(incorrectGenderWarning);
    }

    private boolean validateSecretAnswer() {
        return !(secretAnswerEditText.getText().toString().equals("") ||
                secretQuestionSpinner.getSelectedItem().toString().equals(hintSecretQuestion));
    }

    private void notifyNoSecretAnswer() {
        showSnackbar(incorrectSecretAnswerWarning);
    }

    private void showSnackbar(String message) {
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void showActivityProcessHint() {
        String message = statisticRestoreIntent ? restoreProcessHint : registrationProcessHint;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), null)
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

    private void setFonts() {
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        nameEditText.setTypeface(robotoTypeface);
        dateOfBirthHintTextView.setTypeface(robotoTypeface);
        dayEditText.setTypeface(robotoTypeface);
        yearEditText.setTypeface(robotoTypeface);
        genderHintTextView.setTypeface(robotoTypeface);
        maleRadioButton.setTypeface(robotoTypeface);
        femaleRadioButton.setTypeface(robotoTypeface);
        secretAnswerEditText.setTypeface(robotoTypeface);
    }
}




























