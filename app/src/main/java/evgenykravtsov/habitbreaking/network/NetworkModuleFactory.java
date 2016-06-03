package evgenykravtsov.habitbreaking.network;

public class NetworkModuleFactory {

    public static ServerConnection provideServerConnection() {
        return new SocketClient();
    }
}
