package evgenykravtsov.habitbreaking.domain.model;

public class ConsumptionDetailsDataEntity {

    private final double resin;
    private final double nicotine;
    private final double co;

    ////

    public ConsumptionDetailsDataEntity(double resin, double nicotine, double co) {
        this.resin = resin;
        this.nicotine = nicotine;
        this.co = co;
    }

    ////

    public double getResin() {
        return resin;
    }

    public double getNicotine() {
        return nicotine;
    }

    public double getCo() {
        return co;
    }
}
