package evgenykravtsov.habitbreaking.presenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.interactor.SendRegistrationDataInteractor;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;
import evgenykravtsov.habitbreaking.network.event.RegistrationResultEvent;
import evgenykravtsov.habitbreaking.view.RegistrationView;

public class RegistrationViewPresenter {

    private RegistrationView view;

    //// PUBLIC METHODS

    public void bind(RegistrationView view) {
        this.view = view;
        EventBus.getDefault().register(this);
    }

    public void unbind() {
        view = null;
        EventBus.getDefault().unregister(this);
    }

    public void processRegistrationData(RegistrationDataEntity entity) {
        if (entity != null) {
            new SendRegistrationDataInteractor().interact(entity);
            view.showProgress();
        }
    }

    //// EVENTS

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoInternetConnectionEvent(NoInternetConnectionEvent event) {
        view.notifyNoInternetConnection();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegistrationResultEvent(RegistrationResultEvent event) {
        view.hideProgress();

        switch (event.getResultCode()) {
            case 0:
                view.notifyRegistrationSuccess();
                break;
            case 1:
                view.notifyDuplicateUserName();
                break;
            case 2:
                view.notifyConnectionError();
                break;
        }
    }
}
