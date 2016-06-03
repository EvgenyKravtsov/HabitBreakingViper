package evgenykravtsov.habitbreaking.interactor;

import android.util.Log;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.domain.model.Mode;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.domain.Utils;

public class ChangeModeInteractor {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;

    //// CONSTRUCTORS

    public ChangeModeInteractor() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
    }

    //// PUBLIC METHODS

    public void interact(Mode newMode) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Change Mode Interaction");

        applicationDataStorage.saveMode(newMode);
        applicationDataStorage.saveConsumptionDate(
                ApplicationDataStorage.CONSUMPTION_DATE_DEFAULT_VALUE);

        if (newMode == Mode.FREE) {
            applicationDataStorage.saveAccumulationDate(
                    ApplicationDataStorage.ACCUMULATION_DATE_DEFAULT_VALUE);
            applicationDataStorage.saveAccumulationRegister(
                    ApplicationDataStorage.DEFAULT_ACCUMULATION_REGISTER_VALUE);
            applicationDataStorage.saveConsumptionUnlockDate(
                    ApplicationDataStorage.DEFAULT_CONSUMPTION_UNLOCK_DATE_VALUE);
            applicationDataStorage.saveConsumptionLockStatus(
                    ApplicationDataStorage.DEFAULT_CONSUMPTION_LOCK_STATUS_VALUE);
        } else {
            applicationDataStorage.saveAccumulationDate(Utils.getCurrentTimeUnixSeconds());
        }
    }
}
