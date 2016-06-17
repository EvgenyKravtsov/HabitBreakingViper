package evgenykravtsov.habitbreaking.view;

public interface RegistrationView {

    void notifyNoInternetConnection();

    void notifyRegistrationSuccess();

    void notifyConnectionError();

    void notifyNoStatisticForUser();

    void notifyStatisticRestored();

    void showProgress();

    void hideProgress();
}
