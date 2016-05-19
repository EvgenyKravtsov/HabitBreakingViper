package evgenykravtsov.habitbreaking.data;

public interface ApplicationDataStorage {

    String KEY_CONSUMPTION_DATE = "key_consumption_date";
    long CONSUMPTION_DATE_DEFAULT_VALUE = 0;

    ////

    void saveConsumptionDate(long date);

    long loadConsumptionDate();
}
