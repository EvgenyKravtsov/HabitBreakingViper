package evgenykravtsov.habitbreaking.domain.model;

public class ConsumptionDetailsDataEntity {

    private final double resin;
    private final double nicotine;

    ////

    public ConsumptionDetailsDataEntity(double resin, double nicotine) {
        this.resin = resin;
        this.nicotine = nicotine;
    }

    ////

    public double getResin() {
        return resin;
    }

    public double getNicotine() {
        return nicotine;
    }
}
