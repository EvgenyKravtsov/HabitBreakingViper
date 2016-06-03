package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.NetworkModuleFactory;
import evgenykravtsov.habitbreaking.network.ServerConnection;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;

public class SendRegistrationDataInteractor {

    // Module dependencies
    private ServerConnection serverConnection;

    //// CONSTRUCTORS

    public SendRegistrationDataInteractor() {
        serverConnection = NetworkModuleFactory.provideServerConnection();
    }

    //// PUBLIC METHODS

    public void interact(RegistrationDataEntity entity) {
        if (!AppController.isInternetAvailable()) {
            EventBus.getDefault().post(new NoInternetConnectionEvent());
            return;
        }

        serverConnection.sendRegistrationData(entity);
    }
}
