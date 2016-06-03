package evgenykravtsov.habitbreaking.view;

public interface RegistrationView {

    void notifyNoInternetConnection();

    void notifyRegistrationSuccess();

    void notifyDuplicateUserName();

    void notifyConnectionError();

    void showProgress();

    void hideProgress();
}
