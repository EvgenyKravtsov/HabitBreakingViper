package evgenykravtsov.habitbreaking.presenter;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import evgenykravtsov.habitbreaking.domain.event.ConsumptionTimeDifferenceEvent;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.interactor.ConsumptionInteractor;
import evgenykravtsov.habitbreaking.view.MainView;

public class MainViewPresenter {

    private MainView view;

    //// PUBLIC METHODS

    public void bind(MainView view) {
        this.view = view;
        EventBus.getDefault().register(this);

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Main View Presenter Binded");

        establishInitialViewState();
    }

    public void unbind() {
        view = null;
        EventBus.getDefault().unregister(this);

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Main View Presenter Unbinded");
    }

    public void countConsumption() {
        new ConsumptionInteractor().interact();
    }

    //// PRIVATE METHODS

    private void establishInitialViewState() {
        view.setTimerValue("00:00:00");
    }

    private String formatSecondsForTimer(long rawSeconds) {
        int hours = (int) rawSeconds / 3600;
        int minutes = (int) (rawSeconds - hours * 3600) / 60;
        int seconds = (int) rawSeconds - hours * 3600 - minutes * 60;

        return String.format(Locale.ROOT, "%s:%s:%s",
                hours >= 10 ? hours : "0" + hours,
                minutes >= 10 ? minutes : "0" + minutes,
                seconds >= 10 ? seconds : "0" + seconds);
    }

    //// EVENTS

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConsumptionTimeDifferenceEvent(ConsumptionTimeDifferenceEvent event) {
        view.setTimerValue(formatSecondsForTimer(event.getSeconds()));
    }
}
