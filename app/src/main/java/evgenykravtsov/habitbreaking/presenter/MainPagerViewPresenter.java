package evgenykravtsov.habitbreaking.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.Event.StatisticSavedEvent;
import evgenykravtsov.habitbreaking.interactor.DeleteStatisticInteractor;
import evgenykravtsov.habitbreaking.interactor.DeleteUserInteractor;
import evgenykravtsov.habitbreaking.interactor.event.StatisticClearedEvent;
import evgenykravtsov.habitbreaking.network.event.DownloadDataEvent;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;
import evgenykravtsov.habitbreaking.network.event.UserDeletedEvent;
import evgenykravtsov.habitbreaking.presenter.event.RedrawStatisticView;
import evgenykravtsov.habitbreaking.view.MainPagerView;

public class MainPagerViewPresenter {

    // Dependencies
    private ApplicationDataStorage applicationDataStorage;

    private MainPagerView view;

    ////

    public MainPagerViewPresenter(MainPagerView view) {
        this.view = view;
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        EventBus.getDefault().register(this);
    }

    ////

    public void unbindView() {
        view = null;
        EventBus.getDefault().unregister(this);
    }

    public void deleteStatistic() {
        new DeleteStatisticInteractor().interact();
    }

    public void deleteUser() {
        new DeleteUserInteractor().interact();
    }

    public boolean isUserRegistered() {
        return applicationDataStorage.loadRegistrationDate() !=
                ApplicationDataStorage.DEFAULT_REGISTRATION_DATE_VALUE &&
                !applicationDataStorage.loadUserName().equals(ApplicationDataStorage.DEFAULT_USER_NAME_VALUE);
    }

    public String getUserName() {
        return applicationDataStorage.loadUserName();
    }

    ////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NoInternetConnectionEvent event) {
        view.notifyNoInternetConnection();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StatisticSavedEvent event) {
        view.notifyStatisticSaved();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownloadDataEvent event) {
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
    public void onEvent(StatisticClearedEvent event) {
        view.notifyStatisticCleared();
        EventBus.getDefault().post(new RedrawStatisticView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UserDeletedEvent event) {
        view.notifyUserDeleted();
    }
}
