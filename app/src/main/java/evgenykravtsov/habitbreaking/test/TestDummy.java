package evgenykravtsov.habitbreaking.test;

import java.util.List;

import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;

public class TestDummy implements StatisticDataStorage {

    @Override
    public void registerConsumption() {

    }

    @Override
    public float getAverageConsumption() {
        return 20;
    }

    @Override
    public void displayStatisticStorageContent() {

    }

    @Override
    public List<StatisticDataEntity> getUnsentStatisticData() {
        return null;
    }

    @Override
    public void confirmStatisticDataSync(long date) {

    }

    @Override
    public void clearStatisticStorage() {

    }

    @Override
    public void saveStatistic(List<StatisticDataEntity> entities) {

    }

    @Override
    public List<StatisticDataEntity> getStatisticForPeriod(int numberOfDays) {
        return null;
    }

    @Override
    public int getStatisticDaysCount() {
        return 0;
    }
}
