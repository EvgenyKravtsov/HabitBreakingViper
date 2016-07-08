package evgenykravtsov.habitbreaking.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.presenter.ConsumptionViewPresenter;
import evgenykravtsov.habitbreaking.view.ConsumptionView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class ConsumptionFragment extends Fragment implements ConsumptionView {

    @BindString(R.string.consume_button_tip)
    String consumeButtonTip;
    @BindString(R.string.too_early_to_smoke_warning)
    String tooEarlyToSmokeWarning;
    @BindString(R.string.delete_statistic_entry_warning)
    String deleteStatisticEntryWarning;

    @BindDrawable(R.drawable.consume_button_background)
    Drawable consumeButtonDrawable;
    @BindDrawable(R.drawable.consume_button_locked_background)
    Drawable consumeButtonLockedDrawable;

    @BindView(R.id.consumption_fragment_container_view)
    RelativeLayout containerView;
    @BindView(R.id.consumption_fragment_timer_value_text_view)
    TextView timerValueTextView;
    @BindView(R.id.consumption_fragment_consume_button)
    LinearLayout consumeButton;
    @BindView(R.id.consumption_fragment_statistic_chart_view)
    LineChartView statisticChartView;

    @OnClick(R.id.consumption_fragment_consume_button)
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

    @OnLongClick(R.id.consumption_fragment_consume_button)
    public boolean onLongClickConsumeButton() {
        Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE) ;
        vibe.vibrate(50);
        presenter.countConsumption();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
        presenter.sendStatistic();
        return true;
    }

    ////

    public static final int NUMBER_OF_DAYS_FOR_STATISTIC = 7;

    private static final String TAG = ConsumptionFragment.class.getSimpleName();

    private ConsumptionViewPresenter presenter;

    ////

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumption, container, false);
        ButterKnife.bind(this, view);
        setFonts();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO Delete test code
        Log.d("Log", "on stop");

        unbindPresenter();
    }

    ////

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
    public void redrawStatisticView() {
        showStatistic(presenter.getStatisticToDisplay(NUMBER_OF_DAYS_FOR_STATISTIC));
    }

    ////

    private void bindPresenter() {
        presenter = new ConsumptionViewPresenter(this);
    }

    private void unbindPresenter() {
        presenter.unbindView();
        presenter = null;
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

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("EE");
        for (int i = 0; i < statisticData.size(); i++) {
            String formattedDate = dateFormat.format(new Date(statisticData.get(i).getDate() * 1000));
            values.add(new PointValue(i + 1, statisticData.get(i).getCount()));
            axisValues.add(new AxisValue(i + 1, formattedDate.toCharArray()));
        }

        prepareChartView(values, axisValues);
    }

    private void prepareChartView(List<PointValue> values, List<AxisValue> axisValues) {
        int colorContrast = getResources().getColor(R.color.colorContrast);
        int colorDark = getResources().getColor(R.color.colorPrimaryDark);
        Typeface robotoTypeface = Typeface.createFromAsset(
                getActivity().getAssets(),
                "Roboto-Bold.ttf");

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
        statisticChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                checkUserStatisticEntryDelete(pointIndex);
            }

            @Override
            public void onValueDeselected() {

            }
        });
        statisticChartView.setScrollEnabled(true);
        statisticChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        statisticChartView.setZoomEnabled(false);
    }

    private void checkUserStatisticEntryDelete(final int dayIndex) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(deleteStatisticEntryWarning)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.deleteDayFromStatistic(dayIndex);
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
        dialog.show();
    }

    private void setFonts() {
        Typeface robotoTypeface =
                Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        timerValueTextView.setTypeface(robotoTypeface);
    }
}




























