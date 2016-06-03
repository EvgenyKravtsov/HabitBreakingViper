package evgenykravtsov.habitbreaking.data;

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
}
