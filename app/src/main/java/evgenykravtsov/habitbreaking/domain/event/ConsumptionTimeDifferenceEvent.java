package evgenykravtsov.habitbreaking.domain.event;

public class ConsumptionTimeDifferenceEvent {

    private final long seconds;

    //// CONSTRUCTORS

    public ConsumptionTimeDifferenceEvent(long seconds) {
        this.seconds = seconds;
    }

    //// PUBLIC METHODS

    public long getSeconds() {
        return seconds;
    }
}
