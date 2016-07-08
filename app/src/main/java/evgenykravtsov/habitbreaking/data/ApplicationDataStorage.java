package evgenykravtsov.habitbreaking.data;

import evgenykravtsov.habitbreaking.domain.model.ConsumptionDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.CostDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.Mode;

public interface ApplicationDataStorage {

    double HEALTH_MODE_DECREASE_RATIO = 0.95;

    String KEY_CONSUMPTION_DATE = "key_consumption_date";
    long CONSUMPTION_DATE_DEFAULT_VALUE = 0;
    String KEY_ACCUMULATION_DATE = "key_accumulation_date";
    long ACCUMULATION_DATE_DEFAULT_VALUE = 0;
    String KEY_ACCUMULATION_REGISTER = "key_accumulation_register";
    int DEFAULT_ACCUMULATION_REGISTER_VALUE = 0;
    String KEY_MODE = "key_mode";
    Mode DEFAULT_MODE_VALUE = Mode.FREE;
    String KEY_CONSUMPTION_UNLOCK_DATE = "key_consumption_unlock_date";
    long DEFAULT_CONSUMPTION_UNLOCK_DATE_VALUE = 0;
    String KEY_CONSUMPTION_LOCK_STATUS = "key_consumption_lock_status";
    boolean DEFAULT_CONSUMPTION_LOCK_STATUS_VALUE = false;
    String KEY_USER_NAME = "key_user_name";
    String DEFAULT_USER_NAME_VALUE = "";
    String KEY_REGISTRATION_DATE = "key_registration_date";
    long DEFAULT_REGISTRATION_DATE_VALUE = 0;
    String KEY_RESIN = "key_resin";
    double DEFAULT_RESIN_VALUE = 0.0;
    String KEY_NICOTINE = "key_nicotine";
    double DEFAULT_NICOTINE_VALUE = 0.0;
    String KEY_CO = "key_co";
    double DEFAULT_CO_VALUE = 0.0;
    String KEY_CONSUMPTION_DETAILS_INITIAL_CALCULATING_STATUS =
            "key_consumption_details_calculating_status";
    boolean DEFAULT_CONSUMPTION_DETAILS_INITIAL_CALCULATING_STATUS_VALUE = false;
    String KEY_RESIN_SUMMARY = "key_resin_summary";
    double DEFAULT_RESIN_SUMMARY_VALUE = 0.0;
    String KEY_NICOTINE_SUMMARY = "key_nicotine_summary";
    double DEFAULT_NICOTINE_SUMMARY_VALUE = 0.0;
    String KEY_CO_SUMMARY = "key_co_summary";
    double DEFAULT_CO_SUMMARY_VALUE = 0.0;
    String KEY_COST = "key_cost";
    double DEFAULT_COST_VALUE = 0.0;
    String KEY_CURRENCY_TYPE = "key_currency_type";
    String DEFAULT_CURRENCY_TYPE_VALUE = "";
    String KEY_COST_SUMMARY = "key_cost_summary";
    double DEFAULT_COST_SUMMARY_VALUE = 0.0;

    ////

    void saveConsumptionDate(long date);

    long loadConsumptionDate();

    void saveAccumulationDate(long date);

    long loadAccumulationDate();

    void saveAccumulationRegister(int value);

    int loadAccumulationRegister();

    void saveMode(Mode mode);

    Mode loadMode();

    void saveConsumptionUnlockDate(long date);

    long loadConsumptionUnlockDate();

    void saveConsumptionLockStatus(boolean status);

    boolean loadConsumptionLockStatus();

    void saveUserName(String userName);

    String loadUserName();

    void saveRegistrationDate(long registrationDate);

    long loadRegistrationDate();

    void saveConsumptionDetailsData(ConsumptionDetailsDataEntity data);

    ConsumptionDetailsDataEntity loadConsumptionDetailsData();

    void saveConsumptionDetailsInitailCalculatingStatus(boolean status);

    boolean loadConsumptionDetailsInitialCalculatingStatus();

    void saveConsumptionDetailsSummary(ConsumptionDetailsDataEntity data);

    ConsumptionDetailsDataEntity loadConsumptionDetailsSummary();

    void saveCostDetailsData(CostDetailsDataEntity data);

    CostDetailsDataEntity loadCostDetailsData();
}
