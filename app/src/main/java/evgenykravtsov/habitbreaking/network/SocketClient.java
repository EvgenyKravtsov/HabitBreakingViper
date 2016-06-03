package evgenykravtsov.habitbreaking.network;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.event.DownloadDataEvent;
import evgenykravtsov.habitbreaking.network.event.RegistrationResultEvent;

public class SocketClient implements ServerConnection {

    private static final String SERVER_ADDRESS = "195.42.183.52";
    private static final int SERVER_PORT = 8080;
    private static final int CONNECTION_TIMEOUT_INTERVAL = 5000; // Milliseconds

    // Module dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    private List<ServerConnectionListener> listeners;
    private Socket socket;

    //// CONSTRUCTORS

    public SocketClient() {
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
        listeners = new ArrayList<>();
    }

    //// SERVER CONNECTION INTERFACE

    @Override
    public void setServerConnectionListener(ServerConnectionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeServerConnectionListener(ServerConnectionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void sendStatisticData(final List<StatisticDataEntity> statisticData) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Send Statistic Data Called With - " + statisticData.get(0).toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, "Sender Thread Started And Socket Is - " + socket.isConnected());

                    PrintWriter socketOutput = new PrintWriter(socket.getOutputStream(), true);

                    for (StatisticDataEntity entity : statisticData) {
                        SocketListener socketListener = new SocketListener();

                        JSONObject message = formatStatitsticDataToJson(entity);

                        // TODO Delete test code
                        Log.d(AppController.APP_TAG, "Statistic message to send - " + message.toString());

                        socketOutput.println(message);

                        int socketListenerCounter = 0;
                        long sendTime = Calendar.getInstance().getTimeInMillis();
                        while (Calendar.getInstance().getTimeInMillis() - sendTime <
                                CONNECTION_TIMEOUT_INTERVAL) {

                            if (socketListenerCounter < 1) {
                                socketListener.threadStatus = true;
                                socketListener.queryType = ServerQueryType.WRITE_STATISTIC;
                                socketListener.confirmationDate = entity.getDate();
                                new Thread(socketListener).start();
                                socketListenerCounter++;
                            }

                            if (!socketListener.threadStatus) {
                                break;
                            }

                            TimeUnit.MILLISECONDS.sleep(100);
                        }

                        socketListener.threadStatus = false;
                    }

                    closeConnection();

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, "Socket connection closed");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void sendRegistrationData(RegistrationDataEntity entity) {

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Send Registration Data Called With - " + entity.toString());

        try {
            sendJsonMessage(ServerQueryType.WRITE_USER, formatRegistrationDataToJson(entity));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getStatistic(String email) {
        try {
            sendJsonMessage(ServerQueryType.GET_STATISTIC, generateJsonForGetStatistic(email));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //// PRIVATE METHODS

    private void openConnection() {
        try {

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "Connection Process Started");

            socket = new Socket();

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "Socket Object Created");

            socket.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT), CONNECTION_TIMEOUT_INTERVAL);

            // TODO Delete test code
            Log.d(AppController.APP_TAG, "Connection Process Finished");
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new RegistrationResultEvent(2));
        }
    }

    private void closeConnection() throws IOException {
        socket.close();
    }

    private void sendJsonMessage(final ServerQueryType queryType, final JSONObject message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, "Sender Thread Started And Socket Is - " + socket.isConnected());

                    PrintWriter socketOutput = new PrintWriter(socket.getOutputStream(), true);

                    SocketListener socketListener = new SocketListener();

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, "Statistic message to send - " + message.toString());

                    socketOutput.println(message);

                    int socketListenerCounter = 0;
                    long sendTime = Calendar.getInstance().getTimeInMillis();
                    while (Calendar.getInstance().getTimeInMillis() - sendTime <
                            CONNECTION_TIMEOUT_INTERVAL) {

                        if (socketListenerCounter < 1) {
                            socketListener.threadStatus = true;
                            socketListener.queryType = queryType;
                            if (queryType == ServerQueryType.WRITE_USER) {
                                socketListener.userName = message.getString("EMAIL");
                            }
                            new Thread(socketListener).start();
                            socketListenerCounter++;
                        }

                        if (!socketListener.threadStatus) {
                            break;
                        }

                        TimeUnit.MILLISECONDS.sleep(100);
                    }

                    socketListener.threadStatus = false;
                    closeConnection();

                    if (queryType == ServerQueryType.GET_STATISTIC) {
                        EventBus.getDefault().post(new DownloadDataEvent(1));
                    }

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, "Socket connection closed");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONObject formatStatitsticDataToJson(StatisticDataEntity entity) throws JSONException {
        String userName = applicationDataStorage.loadUserName();
        JSONObject message = new JSONObject();
        message.put("TYPE", "STATISTIC");
        message.put("EMAIL", userName);
        message.put("DATE", entity.getDate());
        message.put("COUNT", entity.getCount());
        return message;
    }

    private JSONObject formatRegistrationDataToJson(RegistrationDataEntity entity) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("TYPE", "REGISTRATION");
        message.put("EMAIL", entity.getEmail());
        message.put("GENDER", entity.getGender());
        message.put("AGE", entity.getAge());
        message.put("REGISTRATION_DATE", entity.getRegistrationDate());
        return message;
    }

    private JSONObject generateJsonForGetStatistic(String email) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("TYPE", "GET_STATISTIC");
        message.put("EMAIL", email);
        return message;
    }

    private ServerAnswerType parseServerAnswer(ServerQueryType queryType, String answer) {
        try {
            JSONObject jsonAnswer = new JSONObject(answer);
            if (jsonAnswer.getString("STORAGE").equals("WRITE_SUCCESS")) {
                switch (queryType) {
                    case WRITE_STATISTIC:
                        return ServerAnswerType.STATISTIC_WRITE_SUCCESS;
                    case WRITE_USER:
                        return ServerAnswerType.USER_WRITE_SUCCESS;
                }
            } else if (jsonAnswer.getString("STORAGE").equals("DUPLICATE_ERROR")) {
                return ServerAnswerType.USER_DUPLICATE_ERROR;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ServerAnswerType.BAD_ANSWER;
    }

    private void parseServerStatisticAnswer(String answer) {
        try {
            List<StatisticDataEntity> entities = new ArrayList<>();

            JSONObject jsonAnswer = new JSONObject(answer);
            JSONArray statisticData = jsonAnswer.getJSONArray("STATISTIC_DATA");
            for (int i = 0; i < statisticData.length(); i++) {
                JSONObject element = statisticData.getJSONObject(i);
                entities.add(new StatisticDataEntity(element.getLong("DATE"),
                        element.getInt("COUNT")));
            }

            for (ServerConnectionListener listener : listeners) {
                listener.onStatisticDataReceived(entities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //// INNER CLASSES

    class SocketListener implements Runnable {

        boolean threadStatus;
        ServerQueryType queryType;
        String userName;
        long confirmationDate;

        @Override
        public void run() {
            while (threadStatus) {
                try {
                    BufferedReader socketInput = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    String answer = socketInput.readLine();

                    if (queryType == ServerQueryType.GET_STATISTIC) {
                        parseServerStatisticAnswer(answer);
                        break;
                    }

                    // TODO Delete test code
                    Log.d(AppController.APP_TAG, answer);

                    ServerAnswerType serverAnswerType = parseServerAnswer(queryType, answer);
                    if (serverAnswerType != null) {
                        switch (serverAnswerType) {
                            case STATISTIC_WRITE_SUCCESS:
                                statisticDataStorage.confirmStatisticDataSync(confirmationDate);
                                break;
                            case USER_WRITE_SUCCESS:
                                applicationDataStorage.saveUserName(userName);
                                EventBus.getDefault().post(new RegistrationResultEvent(0));
                                break;
                            case USER_DUPLICATE_ERROR:
                                EventBus.getDefault().post(new RegistrationResultEvent(1));
                                break;
                            case BAD_ANSWER:
                                if (queryType == ServerQueryType.WRITE_USER ||
                                        queryType == ServerQueryType.GET_STATISTIC) EventBus
                                        .getDefault().post(new RegistrationResultEvent(2));
                                break;
                        }
                    }

                    threadStatus = false;

                    // TODO Handle server not responding

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
