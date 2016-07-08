package evgenykravtsov.habitbreaking.presenter;

import evgenykravtsov.habitbreaking.data.ApplicationDataStorage;
import evgenykravtsov.habitbreaking.data.DataModuleFactory;
import evgenykravtsov.habitbreaking.data.StatisticDataStorage;
import evgenykravtsov.habitbreaking.domain.model.ConsumptionDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.CostDetailsDataEntity;
import evgenykravtsov.habitbreaking.view.ConsumptionDetailsView;

public class ConsumptionDetailsViewPresenter {

    private static final String TAG = ConsumptionDetailsViewPresenter.class.getSimpleName();

    // Dependencies
    private ApplicationDataStorage applicationDataStorage;
    private StatisticDataStorage statisticDataStorage;

    private ConsumptionDetailsView view;

    ////

    public ConsumptionDetailsViewPresenter(ConsumptionDetailsView view) {
        this.view = view;
        applicationDataStorage = DataModuleFactory.provideApplicationDataStorage();
        statisticDataStorage = DataModuleFactory.provideStatisticDataStorage();
    }

    ////

    public void unbindView() {
        view = null;
    }

    public void saveConsumptionDetails(ConsumptionDetailsDataEntity data) {
        applicationDataStorage.saveConsumptionDetailsData(data);
        view.notifyConsumptionDetailsSaved();
    }

    public ConsumptionDetailsDataEntity loadConsumptionDetails() {
        return applicationDataStorage.loadConsumptionDetailsData();
    }

    public boolean isConsumptionDetailsCalculatingAllowed() {
        return !applicationDataStorage.loadConsumptionDetailsInitialCalculatingStatus();
    }

    public ConsumptionDetailsDataEntity makeConsumptionDetailsInitialCalculating() {
        int consumptionCountSummary = statisticDataStorage.getConsumptionCountSummary();

        ConsumptionDetailsDataEntity consumptionDetails =
                applicationDataStorage.loadConsumptionDetailsData();

        double resin = consumptionDetails.getResin();
        double nicotine = consumptionDetails.getNicotine();
        double co = consumptionDetails.getCo();

        double resinSummary = resin * consumptionCountSummary;
        double nicotineSummary = nicotine * consumptionCountSummary;
        double coSummary = co * consumptionCountSummary;

        ConsumptionDetailsDataEntity consumptionSummary =
                new ConsumptionDetailsDataEntity(
                        resinSummary != 0 ? resinSummary : resin,
                        nicotineSummary != 0 ? nicotineSummary : nicotine,
                        coSummary != 0 ? coSummary : co);

        applicationDataStorage.saveConsumptionDetailsSummary(consumptionSummary);
        applicationDataStorage.saveConsumptionDetailsInitailCalculatingStatus(true);

        return consumptionSummary;
    }

    public ConsumptionDetailsDataEntity loadConsumptionDetailsSummary() {
        return applicationDataStorage.loadConsumptionDetailsSummary();
    }

    public void saveCostDetails(CostDetailsDataEntity data) {
        applicationDataStorage.saveCostDetailsData(data);
    }
}
