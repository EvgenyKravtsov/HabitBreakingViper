package evgenykravtsov.habitbreaking.interactor.event;

public class ConsumptionLockEvent {

    private final boolean locked;

    //// CONSTRUCTORS

    public ConsumptionLockEvent(boolean locked) {
        this.locked = locked;
    }

    //// PUBLIC METHODS

    public boolean isLocked() {
        return locked;
    }
}
