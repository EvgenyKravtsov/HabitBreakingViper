package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.interactor.event.StatisticEntryDeletedEvent;

public class DeleteStatisticEntryInteractor {

    // Dependencies
    private StatisticDataStorage statisticDataStorage;

    ////

    public DeleteStatisticEntryInteractor() {
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    ////

    public void execute(int index) {
        statisticDataStorage.deleteStatisticEntryByIndex(index);
        EventBus.getDefault().post(new StatisticEntryDeletedEvent());
    }

    public void executeForLastWeek(int index) {
        if (index == 0) return;

        int statisticDaysCount = statisticDataStorage.getStatisticDaysCount();
        if (statisticDaysCount > 8) index = statisticDaysCount - 7 + index;
        else if (statisticDaysCount < 7) index -= 1;

        statisticDataStorage.deleteStatisticEntryByIndex(index);
        EventBus.getDefault().post(new StatisticEntryDeletedEvent());
    }
}
