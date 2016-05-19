package evgenykravtsov.habitbreaking.data;

import android.content.Context;
import android.content.SharedPreferences;

import evgenykravtsov.habitbreaking.domain.os.AppController;

public class SharedPreferencesStorage implements ApplicationDataStorage {

    private static final String APP_DATA = "habit_breaking_storage";

    private static SharedPreferencesStorage instance;

    private SharedPreferences sharedPreferences;

    //// CONSTRUCTORS

    private SharedPreferencesStorage() {
        sharedPreferences = AppController.getAppContext()
                .getSharedPreferences(APP_DATA, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesStorage getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesStorage();
        }
        return instance;
    }

    //// APPLICATION DATA STORAGE INTERFACE

    @Override
    public void saveConsumptionDate(long date) {
        saveLong(ApplicationDataStorage.KEY_CONSUMPTION_DATE, date);
    }

    @Override
    public long loadConsumptionDate() {
        return loadLong(ApplicationDataStorage.KEY_CONSUMPTION_DATE,
                ApplicationDataStorage.CONSUMPTION_DATE_DEFAULT_VALUE);
    }

    //// PRIVATE METHODS

    private void saveLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private long loadLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
}
