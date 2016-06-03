package evgenykravtsov.habitbreaking.interactor;

import java.util.List;

import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;

public class GetStatisticForPeriodInteractor {

    // Module dependencies
    private StatisticDataStorage statisticDataStorage;

    //// CONSTRUCTORS

    public GetStatisticForPeriodInteractor() {
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    //// PUBLIC METHODS

    public List<StatisticDataEntity> interact(int numberOfDays) {
        return statisticDataStorage.getStatisticForPeriod(numberOfDays);
    }
}
