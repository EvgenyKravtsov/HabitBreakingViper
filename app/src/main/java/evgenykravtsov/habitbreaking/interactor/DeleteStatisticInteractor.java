package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.ConsumptionDetailsDataEntity;
import evgenykravtsov.habitbreaking.interactor.event.StatisticClearedEvent;

public class DeleteStatisticInteractor {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    ////

    public DeleteStatisticInteractor() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    ////

    public void interact() {
        statisticDataStorage.clearStatisticStorage();
        ConsumptionDetailsDataEntity consumptionDetailsSummary =
                new ConsumptionDetailsDataEntity(0, 0, 0);
        applicationDataStorage.saveConsumptionDetailsSummary(consumptionDetailsSummary);
        EventBus.getDefault().post(new StatisticClearedEvent());
    }
}
