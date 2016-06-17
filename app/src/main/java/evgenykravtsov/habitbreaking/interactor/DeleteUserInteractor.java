package evgenykravtsov.habitbreaking.interactor;

import org.greenrobot.eventbus.EventBus;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.NetworkModuleFactory;
import evgenykravtsov.habitbreaking.network.ServerConnection;
import evgenykravtsov.habitbreaking.network.event.NoInternetConnectionEvent;

public class DeleteUserInteractor {

    // Module dependencies
    private ServerConnection serverConnection;
    private ApplicationDataStorage applicationDataStorage;

    ////

    public DeleteUserInteractor() {
        serverConnection = NetworkModuleFactory.provideServerConnection();
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
    }

    ////

    public void interact() {
        if (!AppController.isInternetAvailable()) {
            EventBus.getDefault().post(new NoInternetConnectionEvent());
            return;
        }

        serverConnection.sendDeleteUserQuery(applicationDataStorage.loadRegistrationDate());
    }
}
