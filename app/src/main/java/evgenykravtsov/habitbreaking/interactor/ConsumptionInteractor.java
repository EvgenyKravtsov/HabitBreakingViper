package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.Utils;
import evgenykravtsov.habitbreaking.interactor.event.ConsumptionLockEvent;

public class ConsumptionInteractor {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    //// CONSTRUCTORS

    public ConsumptionInteractor() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    //// PUBLIC METHODS

    public void interact() {
        statisticDataStorage.registerConsumption();

        switch (applicationDataStorage.loadMode()) {
            case FREE:
                applicationDataStorage.saveConsumptionDate(Utils.getCurrentTimeUnixSeconds());
                break;
            case CONTROL:
                applicationDataStorage.saveConsumptionUnlockDate(calculateConsumptionUnlockDate());
                decrementAccumulationRegister();
                applicationDataStorage.saveConsumptionLockStatus(true);
                emitConsumptionLockEvent();
                break;
            case HEALTH:
                applicationDataStorage.saveConsumptionUnlockDate(calculateConsumptionUnlockDateHealth());
                decrementAccumulationRegister();
                applicationDataStorage.saveConsumptionLockStatus(true);
                emitConsumptionLockEvent();
                break;
        }

        // TODO Delete test code
        statisticDataStorage.displayStatisticStorageContent();
    }

    //// PRIVATE METHODS

    private long calculateConsumptionUnlockDate() {
        return (long) (Utils.getCurrentTimeUnixSeconds() +
                (1440 / (statisticDataStorage.getAverageConsumption() +
                        applicationDataStorage.loadAccumulationRegister()))
                        * 60);
    }

    private long calculateConsumptionUnlockDateHealth() {
        return (long) (Utils.getCurrentTimeUnixSeconds() +
                (1440 / ((statisticDataStorage.getAverageConsumption() *
                        ApplicationDataStorage.HEALTH_MODE_DECREASE_RATIO) +
                        applicationDataStorage.loadAccumulationRegister()))
                        * 60);
    }

    private void decrementAccumulationRegister() {
        int register = applicationDataStorage.loadAccumulationRegister();
        int decrementedRegister = register - 1;
        if (decrementedRegister < 0) decrementedRegister = 0;
        applicationDataStorage.saveAccumulationRegister(decrementedRegister);
    }

    private void emitConsumptionLockEvent() {
        ConsumptionLockEvent event = new ConsumptionLockEvent(true);
        EventBus.getDefault().post(event);
    }
}
