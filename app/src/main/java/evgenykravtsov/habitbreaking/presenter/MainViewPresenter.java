package evgenykravtsov.habitbreaking.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.Event.StatisticSavedEvent;
import evgenykravtsov.habitbreaking.domain.event.ConsumptionTimeDifferenceEvent;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.interactor.ConsumptionInteractor;
import evgenykravtsov.habitbreaking.interactor.DownloadStatisticInteractor;
import evgenykravtsov.habitbreaking.interactor.GetStatisticForPeriodInteractor;
import evgenykravtsov.habitbreaking.interactor.event.ConsumptionLockEvent;
import evgenykravtsov.habitbreaking.network.event.DownloadDataEvent;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;
import evgenykravtsov.habitbreaking.network.event.NoStatisticForEmailEvent;
import evgenykravtsov.habitbreaking.view.MainView;

public class MainViewPresenter {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;

    private MainView view;

    //// CONSTRUCTORS

    public MainViewPresenter() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
    }

    //// PUBLIC METHODS

    public void bind(MainView view) {
        this.view = view;
        EventBus.getDefault().register(this);
        establishInitialViewState();
    }

    public void unbind() {
        view = null;
        EventBus.getDefault().unregister(this);
    }

    public void countConsumption() {
        new ConsumptionInteractor().interact();
    }

    public void restoreStatisticData(String email) {
        new DownloadStatisticInteractor().interact(email);
    }

    public List<StatisticDataEntity> getStatisticToDisplay(int numberOfDays) {
        return new GetStatisticForPeriodInteractor().interact(numberOfDays);
    }

    public void saveUserName(String userName) {
        applicationDataStorage.saveUserName(userName);
    }

    //// PRIVATE METHODS

    private void establishInitialViewState() {
        view.setTimerValue("00:00:00");
        view.changeConsumptionButtonState(applicationDataStorage.loadConsumptionLockStatus());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConsumptionLockEvent(ConsumptionLockEvent event) {
        view.changeConsumptionButtonState(event.isLocked());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoInternetConnectionEvent(NoInternetConnectionEvent event) {
        view.notifyNoInternetConnection();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStatisticSavedEvent(StatisticSavedEvent event) {
        view.notifyStatisticSaved();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadDataEvent(DownloadDataEvent event) {
        switch (event.getStatisCode()) {
            case 0:
                view.showProgress();
                break;
            case 1:
                view.hideProgress();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoStatisticForEmailEvent(NoStatisticForEmailEvent event) {
        view.notifyNoStatisticForEmail();
    }
}
