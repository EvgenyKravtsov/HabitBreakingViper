package evgenykravtsov.habitbreaking.data;

import android.content.Context;
import android.content.SharedPreferences;

import evgenykravtsov.habitbreaking.domain.model.ConsumptionDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.CostDetailsDataEntity;
import evgenykravtsov.habitbreaking.domain.model.Mode;
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

    @Override
    public void saveAccumulationDate(long date) {
        saveLong(ApplicationDataStorage.KEY_ACCUMULATION_DATE, date);
    }

    @Override
    public long loadAccumulationDate() {
        return loadLong(ApplicationDataStorage.KEY_ACCUMULATION_DATE,
                ApplicationDataStorage.ACCUMULATION_DATE_DEFAULT_VALUE);
    }

    @Override
    public void saveAccumulationRegister(int value) {
        saveInt(ApplicationDataStorage.KEY_ACCUMULATION_REGISTER, value);
    }

    @Override
    public int loadAccumulationRegister() {
        return loadInt(ApplicationDataStorage.KEY_ACCUMULATION_REGISTER,
                ApplicationDataStorage.DEFAULT_ACCUMULATION_REGISTER_VALUE);
    }

    @Override
    public void saveMode(Mode mode) {
        int modeAsInt = modeToInt(mode);
        saveInt(ApplicationDataStorage.KEY_MODE, modeAsInt);
    }

    @Override
    public Mode loadMode() {
        return intToMode(loadInt(ApplicationDataStorage.KEY_MODE,
                modeToInt(ApplicationDataStorage.DEFAULT_MODE_VALUE)));
    }

    @Override
    public void saveConsumptionUnlockDate(long date) {
        saveLong(ApplicationDataStorage.KEY_CONSUMPTION_UNLOCK_DATE, date);
    }

    @Override
    public long loadConsumptionUnlockDate() {
        return loadLong(ApplicationDataStorage.KEY_CONSUMPTION_UNLOCK_DATE,
                ApplicationDataStorage.DEFAULT_CONSUMPTION_UNLOCK_DATE_VALUE);
    }

    @Override
    public void saveConsumptionLockStatus(boolean status) {
        saveBoolean(ApplicationDataStorage.KEY_CONSUMPTION_LOCK_STATUS, status);
    }

    @Override
    public boolean loadConsumptionLockStatus() {
        return loadBoolean(ApplicationDataStorage.KEY_CONSUMPTION_LOCK_STATUS,
                ApplicationDataStorage.DEFAULT_CONSUMPTION_LOCK_STATUS_VALUE);
    }

    @Override
    public void saveUserName(String userName) {
        saveString(ApplicationDataStorage.KEY_USER_NAME, userName);
    }

    @Override
    public String loadUserName() {
        return loadString(ApplicationDataStorage.KEY_USER_NAME,
                ApplicationDataStorage.DEFAULT_USER_NAME_VALUE);
    }

    @Override
    public void saveRegistrationDate(long registrationDate) {
        saveLong(ApplicationDataStorage.KEY_REGISTRATION_DATE, registrationDate);
    }

    @Override
    public long loadRegistrationDate() {
        return  loadLong(ApplicationDataStorage.KEY_REGISTRATION_DATE,
                ApplicationDataStorage.DEFAULT_REGISTRATION_DATE_VALUE);
    }

    @Override
    public void saveConsumptionDetailsData(ConsumptionDetailsDataEntity data) {
        saveDouble(ApplicationDataStorage.KEY_RESIN, data.getResin());
        saveDouble(ApplicationDataStorage.KEY_NICOTINE, data.getNicotine());
        saveDouble(ApplicationDataStorage.KEY_CO, data.getCo());
    }

    @Override
    public ConsumptionDetailsDataEntity loadConsumptionDetailsData() {
        return new ConsumptionDetailsDataEntity(
                loadDouble(ApplicationDataStorage.KEY_RESIN,
                        ApplicationDataStorage.DEFAULT_RESIN_VALUE),
                loadDouble(ApplicationDataStorage.KEY_NICOTINE,
                        ApplicationDataStorage.DEFAULT_NICOTINE_VALUE),
                loadDouble(ApplicationDataStorage.KEY_CO,
                        ApplicationDataStorage.DEFAULT_CO_VALUE));
    }

    @Override
    public void saveConsumptionDetailsInitailCalculatingStatus(boolean status) {
        saveBoolean(
                ApplicationDataStorage.KEY_CONSUMPTION_DETAILS_INITIAL_CALCULATING_STATUS, status);
    }

    @Override
    public boolean loadConsumptionDetailsInitialCalculatingStatus() {
        return loadBoolean(
                ApplicationDataStorage.KEY_CONSUMPTION_DETAILS_INITIAL_CALCULATING_STATUS,
                ApplicationDataStorage.DEFAULT_CONSUMPTION_DETAILS_INITIAL_CALCULATING_STATUS_VALUE);
    }

    @Override
    public void saveConsumptionDetailsSummary(ConsumptionDetailsDataEntity data) {
        saveDouble(ApplicationDataStorage.KEY_RESIN_SUMMARY, data.getResin());
        saveDouble(ApplicationDataStorage.KEY_NICOTINE_SUMMARY, data.getNicotine());
        saveDouble(ApplicationDataStorage.KEY_CO_SUMMARY, data.getCo());
    }

    @Override
    public ConsumptionDetailsDataEntity loadConsumptionDetailsSummary() {
        return new ConsumptionDetailsDataEntity(
                loadDouble(ApplicationDataStorage.KEY_RESIN_SUMMARY,
                        ApplicationDataStorage.DEFAULT_RESIN_SUMMARY_VALUE),
                loadDouble(ApplicationDataStorage.KEY_NICOTINE_SUMMARY,
                        ApplicationDataStorage.DEFAULT_NICOTINE_SUMMARY_VALUE),
                loadDouble(ApplicationDataStorage.KEY_CO_SUMMARY,
                        ApplicationDataStorage.DEFAULT_CO_SUMMARY_VALUE));
    }

    @Override
    public void saveCostDetailsData(CostDetailsDataEntity data) {
        saveDouble(KEY_COST, data.getValue());
        saveString(KEY_CURRENCY_TYPE, data.getCurrencyType());
    }

    @Override
    public CostDetailsDataEntity loadCostDetailsData() {
        double value = loadDouble(KEY_COST, DEFAULT_COST_VALUE);
        String currencyType = loadString(KEY_CURRENCY_TYPE, DEFAULT_CURRENCY_TYPE_VALUE);
        return new CostDetailsDataEntity(value, currencyType);
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

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int loadInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    private void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String loadString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    private void saveDouble(String key, double value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, (float) value);
        editor.apply();
    }

    private double loadDouble(String key, double defaultValue) {
        return sharedPreferences.getFloat(key, (float) defaultValue);
    }

    private int modeToInt(Mode mode) {
        switch (mode) {
            case FREE:
                return 0;
            case CONTROL:
                return 1;
            case HEALTH:
                return 2;
        }

        return 0;
    }

    private Mode intToMode(int modeAsInt) {
        switch (modeAsInt) {
            case 0:
                return Mode.FREE;
            case 1:
                return Mode.CONTROL;
            case 2:
                return Mode.HEALTH;
        }

        return Mode.FREE;
    }
}
