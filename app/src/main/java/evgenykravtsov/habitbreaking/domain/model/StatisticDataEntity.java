package evgenykravtsov.habitbreaking.domain.model;

public class StatisticDataEntity {

    private final long date;
    private final int count;

    //// CONSTRUCTORS

    public StatisticDataEntity(long date, int count) {
        this.date = date;
        this.count = count;
    }

    //// PUBLIC METHODS

    public long getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    //// OBJECT INTERFACE


    @Override
    public String toString() {
        return "Statistic data entity: date - " + date + " count - " + count;
    }
}
