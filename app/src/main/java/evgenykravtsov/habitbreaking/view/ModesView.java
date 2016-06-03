package evgenykravtsov.habitbreaking.view;

public interface ModesView {

    void changeFreeModeState(boolean activated);

    void changeControlModeState(boolean activated);

    void changeHealthModeState(boolean activated);

    void notifyNotEnoughStatistic(int remainingDays);
}
