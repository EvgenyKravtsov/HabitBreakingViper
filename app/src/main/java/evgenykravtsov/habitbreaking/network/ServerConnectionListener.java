package evgenykravtsov.habitbreaking.network;

import java.util.List;

import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;

public interface ServerConnectionListener {

    void onStatisticDataReceived(List<StatisticDataEntity> entities);
}
