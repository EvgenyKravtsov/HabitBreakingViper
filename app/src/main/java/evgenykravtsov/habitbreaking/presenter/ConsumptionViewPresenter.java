package evgenykravtsov.habitbreaking.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.domain.event.ConsumptionTimeDifferenceEvent;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.interactor.ConsumptionInteractor;
import evgenykravtsov.habitbreaking.interactor.DeleteStatisticEntryInteractor;
import evgenykravtsov.habitbreaking.interactor.GetStatisticForPeriodInteractor;
import evgenykravtsov.habitbreaking.interactor.SendStatisticDataInteractor;
import evgenykravtsov.habitbreaking.interactor.event.StatisticEntryDeletedEvent;
import evgenykravtsov.habitbreaking.presenter.event.RedrawStatisticView;
import evgenykravtsov.habitbreaking.view.ConsumptionView;

public class ConsumptionViewPresenter {

    private ConsumptionView view;
    private ApplicationDataStorage applicationDataStorage;

    ////

    public ConsumptionViewPresenter(ConsumptionView view) {
        this.view = view;
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        EventBus.getDefault().register(this);
        establishInitialViewState();
    }

    ////

    public void unbindView() {
        view = null;
        EventBus.getDefault().unregister(this);
    }

    public void countConsumption() {
        new ConsumptionInteractor().interact();
    }

    public List<StatisticDataEntity> getStatisticToDisplay(int numberOfDays) {
        return new GetStatisticForPeriodInteractor().interact(numberOfDays);
    }

    public void sendStatistic() {
        SendStatisticDataInteractor interactor = new SendStatisticDataInteractor();
        interactor.interact();
    }

    public void deleteDayFromStatistic(int index) {
        DeleteStatisticEntryInteractor interactor = new DeleteStatisticEntryInteractor();
        interactor.execute(index);
    }

    ////

    private String formatSecondsForTimer(long rawSeconds) {
        int hours = (int) rawSeconds / 3600;
        int minutes = (int) (rawSeconds - hours * 3600) / 60;
        int seconds = (int) rawSeconds - hours * 3600 - minutes * 60;

        return String.format(Locale.ROOT, "%s:%s:%s",
                hours >= 10 ? hours : "0" + hours,
                minutes >= 10 ? minutes : "0" + minutes,
                seconds >= 10 ? seconds : "0" + seconds);
    }

    private void establishInitialViewState() {
        view.setTimerValue("00:00:00");
        view.changeConsumptionButtonState(applicationDataStorage.loadConsumptionLockStatus());
    }

    ////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConsumptionTimeDifferenceEvent event) {
        view.setTimerValue(formatSecondsForTimer(event.getSeconds()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RedrawStatisticView event) {
        view.redrawStatisticView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StatisticEntryDeletedEvent event) {
        view.redrawStatisticView();
    }
}
