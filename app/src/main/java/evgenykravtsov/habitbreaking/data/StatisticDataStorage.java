package evgenykravtsov.habitbreaking.data;

import java.util.List;

import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;

public interface StatisticDataStorage {

    int MAX_RECORDS = 30;

    ////

    void registerConsumption();

    float getAverageConsumption();

    void displayStatisticStorageContent();

    List<StatisticDataEntity> getUnsentStatisticData();

    void confirmStatisticDataSync(long date);

    void clearStatisticStorage();

    void saveStatistic(List<StatisticDataEntity> entities);

    List<StatisticDataEntity> getStatisticForPeriod(int numberOfDays);

    int getStatisticDaysCount();
}
