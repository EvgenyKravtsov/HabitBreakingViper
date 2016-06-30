package evgenykravtsov.habitbreaking.view;

public interface MainPagerView {

    void notifyNoInternetConnection();

    void notifyStatisticSaved();

    void notifyStatisticCleared();

    void notifyUserDeleted();

    void showProgress();

    void hideProgress();
}
