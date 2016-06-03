package evgenykravtsov.habitbreaking.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.presenter.MainViewPresenter;
import evgenykravtsov.habitbreaking.view.MainView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements MainView {

    // TODO Fix chart viewport

    private static final int NUMBER_OF_DAYS_FOR_STATISTIC = 7;

    private static String emailSwap;

    private MainViewPresenter presenter;
    private ProgressDialog progressDialog;

    //// BUTTERKNIFE BINDS

    @BindString(R.string.consume_button_tip)
    String consumeButtonTip;
    @BindString(R.string.too_early_to_smoke_warning)
    String tooEarlyToSmokeWarning;
    @BindString(R.string.no_internet_connection_warning)
    String noInternetConnectionWarning;
    @BindString(R.string.statistic_restore_success_message)
    String statisticRestoreSuccessMessage;
    @BindString(R.string.no_data_for_email_warning)
    String noDataForEmailWarning;
    @BindString(R.string.downloading_process_warning)
    String downloadingProcessWarning;
    @BindString(R.string.synchronization_message)
    String synchronizationMessage;
    @BindString(R.string.register_button_label)
    String registerButtonLabel;
    @BindString(R.string.restore_button_label)
    String restoreButtonLabel;
    @BindString(R.string.incorrect_email_warning)
    String incorrectEmailWarning;

    @BindDrawable(R.drawable.consume_button_background)
    Drawable consumeButtonDrawable;
    @BindDrawable(R.drawable.consume_button_locked_background)
    Drawable consumeButtonLockedDrawable;

    @BindView(R.id.main_activity_container_view)
    RelativeLayout containerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_activity_timer_value_text_view)
    TextView timerValueTextView;
    @BindView(R.id.main_activity_consume_button)
    LinearLayout consumeButton;
    @BindView(R.id.main_activity_statistic_chart_view)
    LineChartView statisticChartView;

    @OnClick(R.id.main_activity_consume_button)
    public void onClickConsumeButton(View view) {
        LinearLayout consumeButton = (LinearLayout) view;

        String message;
        if (consumeButton.getBackground() == consumeButtonDrawable) {
            message = consumeButtonTip;
        } else {
            message = tooEarlyToSmokeWarning;
        }

        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show();
    }

    @OnLongClick(R.id.main_activity_consume_button)
    public boolean onLongClickConsumeButton() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);
                presenter.countConsumption();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
        return true;
    }

    //// ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prepareToolbar();
        setFonts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new MainViewPresenter();
        presenter.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbind();
        presenter = null;
    }

    //// MENU LIFECYCLE

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.main_activity_sync_button:
                notifyRegistration();
                break;
            case R.id.main_activity_modes_button:
                navigateToModesActivity();
                break;
        }
        return true;
    }

    //// MAIN VIEW INTERFACE

    @Override
    public void setTimerValue(String value) {
        timerValueTextView.setText(value);
    }

    @Override
    public void changeConsumptionButtonState(boolean locked) {
        if (locked) {
            consumeButton.setLongClickable(false);
            consumeButton.setBackgroundDrawable(consumeButtonLockedDrawable);
        } else {
            consumeButton.setLongClickable(true);
            consumeButton.setBackgroundDrawable(consumeButtonDrawable);
        }
    }

    @Override
    public void notifyNoInternetConnection() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(noInternetConnectionWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
    }

    @Override
    public void notifyStatisticSaved() {
        presenter.saveUserName(emailSwap);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticRestoreSuccessMessage)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    @Override
    public void notifyNoStatisticForEmail() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(noDataForEmailWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        hideProgress();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(downloadingProcessWarning);
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

    private void navigateToModesActivity() {
        Intent startModesActivity = new Intent(this, ModesActivity.class);
        startActivity(startModesActivity);
    }

    private void navigateToRegistrationActivity() {
        Intent startRegistrationActivity = new Intent(this, RegistrationActivity.class);
        startActivity(startRegistrationActivity);
    }

    private void notifyRegistration() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(synchronizationMessage)
                .setPositiveButton(registerButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToRegistrationActivity();
                    }
                })
                .setNegativeButton(restoreButtonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSignInDialog();
                    }
                })
                .create();
        dialog.show();
    }

    private void showStatistic(List<StatisticDataEntity> statisticData) {
        if (statisticData == null || statisticData.size() <= 0) {
            statisticChartView.setVisibility(View.INVISIBLE);
            return;
        }

        statisticChartView.setVisibility(View.VISIBLE);
        Collections.reverse(statisticData);

        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();

        if (statisticData.size() < 7) {
            values.add(new PointValue(0, 0));
            axisValues.add(new AxisValue(0, "".toCharArray()));
        }

        for (int i = 0; i < statisticData.size(); i++) {
            values.add(new PointValue(i + 1, statisticData.get(i).getCount()));
            axisValues.add(new AxisValue(i + 1, formatDateForStatisticChart(statisticData.get(i).getDate()).toCharArray()));
        }

        prepareChartView(values, axisValues);
    }

    private void prepareChartView(List<PointValue> values, List<AxisValue> axisValues) {
        int colorContrast = getResources().getColor(R.color.colorContrast);
        int colorDark = getResources().getColor(R.color.colorPrimaryDark);
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");

        Line line = new Line(values).setColor(colorDark);
        line.setHasLabels(true);
        line.setStrokeWidth(2);
        line.setPointColor(colorContrast);
        line.setPointRadius(8);
        line.setFilled(true);
        line.setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        Axis xAxis = new Axis();
        xAxis.setValues(axisValues);
        xAxis.setTextColor(colorDark);
        xAxis.setTypeface(robotoTypeface);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(xAxis);
        data.setValueLabelTextSize(15);
        data.setBaseValue(0);
        data.setValueLabelBackgroundEnabled(false);
        data.setValueLabelsTextColor(Color.WHITE);
        data.setValueLabelTypeface(robotoTypeface);

        statisticChartView.setLineChartData(data);
        statisticChartView.setScrollEnabled(false);
        statisticChartView.setZoomEnabled(false);
    }

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
    }

    private String formatDateForStatisticChart(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date * 1000);

        int monthValue = calendar.get(Calendar.MONTH) + 1;
        int dayValue = calendar.get(Calendar.DAY_OF_MONTH);
        String day;
        String month;

        if (dayValue < 10) {
            day = "0" + dayValue;
        } else {
            day = Integer.toString(dayValue);
        }

        if (monthValue < 10) {
            month = "0" + monthValue;
        } else {
            month = Integer.toString(monthValue);
        }

        return day + "." + month;
    }

    private void setFonts() {
        Typeface robotoTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        timerValueTextView.setTypeface(robotoTypeface);
    }

    private void showSignInDialog() {
        @SuppressLint("InflateParams") View signInView = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog_sign_in, null);
        final EditText emailEditText = (EditText) signInView
                .findViewById(R.id.dialog_sign_in_email_edit_text);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(signInView)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailEditText.getText().toString();

                        if (validateEmail(email)) {
                            emailSwap = email;
                            presenter.restoreStatisticData(email);
                        } else {
                            Snackbar.make(containerView, incorrectEmailWarning,
                                    Snackbar.LENGTH_LONG).show();
                        }

                    }
                })
                .create();
        dialog.show();
    }

    private boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
