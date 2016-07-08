package evgenykravtsov.habitbreaking.domain.model;

public class CostDetailsDataEntity {

    private final double value;
    private final String currencyType;

    ////

    public CostDetailsDataEntity(double value, String currencyType) {
        this.value = value;
        this.currencyType = currencyType;
    }

    ////

    public double getValue() {
        return value;
    }

    public String getCurrencyType() {
        return currencyType;
    }
}
