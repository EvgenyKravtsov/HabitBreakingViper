package evgenykravtsov.habitbreaking.view;

public interface ConsumptionView {

    void setTimerValue(String value);

    void changeConsumptionButtonState(boolean locked);

    void redrawStatisticView();
}
