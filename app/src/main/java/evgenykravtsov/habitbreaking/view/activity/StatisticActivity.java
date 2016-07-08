package evgenykravtsov.habitbreaking.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.presenter.StatisticViewPresenter;
import evgenykravtsov.habitbreaking.view.StatisticView;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class StatisticActivity extends AppCompatActivity implements StatisticView {

    private StatisticViewPresenter presenter;

    ////

    @BindView(R.id.statistic_activity_chart_view)
    LineChartView statisticChartView;

    ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showStatistic(presenter
                .getStatisticToDisplay(StatisticDataStorage.MAX_RECORDS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindPresenter();
    }

    ////

    @Override
    public void redrawStatisticView() {
        showStatistic(presenter
                .getStatisticToDisplay(StatisticDataStorage.MAX_RECORDS));
    }


    ////

    private void bindPresenter() {
        presenter = new StatisticViewPresenter(this);
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

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd.MM");
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
        xAxis.setHasLines(true);

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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Удалить статистику за этот день?")
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
}
