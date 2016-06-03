package evgenykravtsov.habitbreaking.network.event;

public class DownloadDataEvent {

    private final int statisCode;
    // 0 - started, 1 - finished

    //// CONSTRUCTORS

    public DownloadDataEvent(int statisCode) {
        this.statisCode = statisCode;
    }

    //// PUBLIC MERTHODS

    public int getStatisCode() {
        return statisCode;
    }
}
