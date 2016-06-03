package evgenykravtsov.habitbreaking.presenter;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.Mode;
import evgenykravtsov.habitbreaking.interactor.ChangeModeInteractor;
import evgenykravtsov.habitbreaking.view.ModesView;

public class ModesViewPresenter {

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    private ModesView view;

    //// CONSTRUCTORS

    public ModesViewPresenter() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    //// PUBLIC METHODS

    public void bind(ModesView view) {
        this.view = view;
        establishInitialViewState();
    }

    public void unbind() {
        this.view = null;
    }

    public void changeMode(Mode mode) {
        if (mode != Mode.FREE) {
            int statisticDaysCount = statisticDataStorage.getStatisticDaysCount();
            if (statisticDaysCount < StatisticDataStorage.MAX_RECORDS) {
                view.notifyNotEnoughStatistic(StatisticDataStorage.MAX_RECORDS - statisticDaysCount);
                return;
            }
        }

        ChangeModeInteractor interactor = new ChangeModeInteractor();
        interactor.interact(mode);
        activateCurrentModeButton(mode);
    }

    //// PRIVATE METHODS

    private void establishInitialViewState() {
        activateCurrentModeButton(applicationDataStorage.loadMode());
    }

    private void activateCurrentModeButton(Mode mode) {
        switch (mode) {
            case FREE:
                view.changeFreeModeState(true);
                view.changeControlModeState(false);
                view.changeHealthModeState(false);
                break;
            case CONTROL:
                view.changeFreeModeState(false);
                view.changeControlModeState(true);
                view.changeHealthModeState(false);
                break;
            case HEALTH:
                view.changeFreeModeState(false);
                view.changeControlModeState(false);
                view.changeHealthModeState(true);
                break;
        }
    }
}
