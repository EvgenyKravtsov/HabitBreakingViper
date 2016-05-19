package evgenykravtsov.habitbreaking.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import evgenykravtsov.habitbreaking.R;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.presenter.MainViewPresenter;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainViewPresenter presenter;

    //// BUTTERKNIFE BINDS

    @BindView(R.id.main_activity_timer_value_text_view)
    TextView timerValueTextView;

    @OnClick(R.id.main_activity_consume_button)
    public void onClickConsumeButton() {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Consume Button Clicked!");

        presenter.countConsumption();
    }

    //// ACTIVITY LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Main Activity Started");

        presenter = new MainViewPresenter();
        presenter.bind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Main Activity Stopped");

        presenter.unbind();
        presenter = null;
    }

    //// MAIN VIEW INTERFACE

    @Override
    public void setTimerValue(String value) {
        timerValueTextView.setText(value);
    }
}
