package evgenykravtsov.habitbreaking.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements MainView {

    // TODO Delete MainActivity before release

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_OF_DAYS_FOR_STATISTIC = 30;

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
    @BindString(R.string.downloading_process_warning)
    String downloadingProcessWarning;
    @BindString(R.string.synchronization_message)
    String synchronizationMessage;
    @BindString(R.string.register_button_label)
    String registerButtonLabel;
    @BindString(R.string.restore_button_label)
    String restoreButtonLabel;
    @BindString(R.string.incorrect_name_warning)
    String incorrectEmailWarning;
    @BindString(R.string.statistic_cleared_warning)
    String statisticClearedWarning;
    @BindString(R.string.user_deleted_warning)
    String userDeletedWarning;

    @BindDrawable(R.drawable.consume_button_background)
    Drawable consumeButtonDrawable;
    @BindDrawable(R.drawable.consume_button_locked_background)
    Drawable consumeButtonLockedDrawable;

    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout drawerLayout;
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
    @BindView(R.id.main_activity_modes_button)
    Button modesButton;
    @BindView(R.id.main_activity_registraion_button)
    Button registrationButton;
    @BindView(R.id.main_activity_delete_statistic_button)
    Button deleteStatisticButton;
    @BindView(R.id.main_activity_restore_statistic_button)
    Button restoreStatisticButton;
    @BindView(R.id.main_activity_delete_account_button)
    Button deleteAccountButton;
    @BindView(R.id.main_activity_user_name_text_view)
    TextView userNameTextView;

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
        presenter.sendStatistic();
        return true;
    }

    @OnClick(R.id.main_activity_modes_button)
    public void onClickModesButton() {
        navigateToModesActivity();
    }

    @OnClick(R.id.main_activity_registraion_button)
    public void onRegistrationButtonClicked() {
        navigateToRegistrationActivity();
    }

    @OnClick(R.id.main_activity_delete_statistic_button)
    public void onDeleteStatisticButtonClicked() {
        presenter.deleteStatistic();
    }

    @OnClick(R.id.main_activity_restore_statistic_button)
    public void onRestoreStatisticButtonClicked() {
        navigateToRegistrationActivityForStatisticRestore();
    }

    @OnClick(R.id.main_activity_delete_account_button)
    public void onDeleteAccountButtonClicked() {
        presenter.deleteUser();
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
        prepareDrawerMenu();
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
            case R.id.main_activity_menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticRestoreSuccessMessage)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    @Override
    public void notifyStatisticCleared() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(statisticClearedWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    @Override
    public void notifyUserDeleted() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(userDeletedWarning)
                .setPositiveButton(getString(android.R.string.ok), null)
                .create();
        dialog.show();
        prepareDrawerMenu();
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

    private void navigateToRegistrationActivityForStatisticRestore() {
        Intent startRegistrationActivity = new Intent(this, RegistrationActivity.class);
        startRegistrationActivity.putExtra(RegistrationActivity.KEY_STATISTIC_RESTORE_INTENT, true);
        startActivity(startRegistrationActivity);
    }

    private void showStatistic(List<StatisticDataEntity> statisticData) {
//        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd.MM\nEEE");
//
//        Collections.reverse(statisticData);
//
//        DataPoint[] dataPoints = new DataPoint[statisticData.size()];
//        for (int i = 0; i < statisticData.size(); i++) {
//            dataPoints[i] = new DataPoint(i, statisticData.get(i).getCount());
//        }
//
//        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPoints);
//        statisticChartView.addSeries(lineGraphSeries);
//
//        statisticChartView.getGridLabelRenderer().setNumHorizontalLabels(7);
//
//        statisticChartView.getViewport().setXAxisBoundsManual(true);
//        statisticChartView.getViewport().setMinX(0);
//        statisticChartView.getViewport().setMaxX(7);
//
//        statisticChartView.getViewport().setScrollable(true);
//
//        statisticChartView.getGridLabelRenderer().setVerticalLabelsVisible(false);
//        statisticChartView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);















        // TODO Under consideration
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

        // TODO Under consideration

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
        statisticChartView.setScrollEnabled(true);
        statisticChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        statisticChartView.setZoomEnabled(false);
    }

    private void prepareToolbar() {
        setSupportActionBar(toolbar);
    }

    private void prepareDrawerMenu() {
        if (presenter.isUserRegistered()) {
            userNameTextView.setVisibility(View.GONE);
            userNameTextView.setText(presenter.getUserName());
            registrationButton.setVisibility(View.GONE);
            deleteAccountButton.setVisibility(View.VISIBLE);
        } else {
            userNameTextView.setVisibility(View.GONE);
            registrationButton.setVisibility(View.VISIBLE);
            deleteAccountButton.setVisibility(View.GONE);
        }
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
        modesButton.setTypeface(robotoTypeface);
        registrationButton.setTypeface(robotoTypeface);
        deleteStatisticButton.setTypeface(robotoTypeface);
        restoreStatisticButton.setTypeface(robotoTypeface);
        deleteAccountButton.setTypeface(robotoTypeface);
    }
}
