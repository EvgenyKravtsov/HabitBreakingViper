package evgenykravtsov.habitbreaking.network;

import java.util.List;

import evgenykravtsov.habitbreaking.domain.model.RegistrationDataEntity;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;

public interface ServerConnection {

    void setServerConnectionListener(ServerConnectionListener listener);

    void removeServerConnectionListener(ServerConnectionListener listener);

    void sendStatisticData(List<StatisticDataEntity> statisticData);

    void sendRegistrationData(RegistrationDataEntity entry);

    void sendRestorationData(RegistrationDataEntity entity);

    void getStatistic(long registrationDate);

    void sendDeleteUserQuery(long registrationDate);
}
