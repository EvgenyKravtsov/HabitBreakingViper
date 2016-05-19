package evgenykravtsov.habitbreaking.data;

public class DataModuleFactory {

    public static ApplicationDataStorage provideApplicationDataStorage() {
        return SharedPreferencesStorage.getInstance();
    }
}
