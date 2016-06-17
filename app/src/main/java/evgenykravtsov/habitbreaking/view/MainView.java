package evgenykravtsov.habitbreaking.view;

public interface MainView {

    void setTimerValue(String value);

    void changeConsumptionButtonState(boolean locked);

    void notifyNoInternetConnection();

    void notifyStatisticSaved();

    void notifyStatisticCleared();

    void notifyUserDeleted();

    void showProgress();

    void hideProgress();
}
