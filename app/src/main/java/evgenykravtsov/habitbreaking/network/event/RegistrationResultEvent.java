package evgenykravtsov.habitbreaking.network.event;

public class RegistrationResultEvent {

    private final int resultCode;
    // 0 - success, 1 - connection error

    //// CONSTRUCTORS

    public RegistrationResultEvent(int resultCode) {
        this.resultCode = resultCode;
    }

    ////

    public int getResultCode() {
        return resultCode;
    }
}
