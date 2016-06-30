package evgenykravtsov.habitbreaking.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.interactor.DeleteStatisticEntryInteractor;
import evgenykravtsov.habitbreaking.interactor.GetStatisticForPeriodInteractor;
import evgenykravtsov.habitbreaking.interactor.event.StatisticEntryDeletedEvent;
import evgenykravtsov.habitbreaking.view.StatisticView;

public class StatisticViewPresenter {

    private StatisticView view;

    ////

    public StatisticViewPresenter(StatisticView view) {
        this.view = view;
        EventBus.getDefault().register(this);
    }

    ////

    public void unbindView() {
        view = null;
        EventBus.getDefault().unregister(this);
    }

    public List<StatisticDataEntity> getStatisticToDisplay(int numberOfDays) {
        return new GetStatisticForPeriodInteractor().interact(numberOfDays);
    }

    public void deleteDayFromStatistic(int index) {
        DeleteStatisticEntryInteractor interactor = new DeleteStatisticEntryInteractor();
        interactor.execute(index);
    }

    ////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StatisticEntryDeletedEvent event) {
        view.redrawStatisticView();
    }
}
