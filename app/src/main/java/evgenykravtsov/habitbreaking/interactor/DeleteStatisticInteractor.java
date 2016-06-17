package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.interactor.event.StatisticClearedEvent;

public class DeleteStatisticInteractor {

    // Module dependencies
    private StatisticDataStorage statisticDataStorage;

    ////

    public DeleteStatisticInteractor() {
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    ////

    public void interact() {
        statisticDataStorage.clearStatisticStorage();
        EventBus.getDefault().post(new StatisticClearedEvent());
    }
}
