package conversion.currency.luka.currencyconversion;

import com.google.gson.annotations.SerializedName;

public class Currency {

    @SerializedName("currency_code")
    private String currencyCode;
    @SerializedName("selling_rate")
    private float sellingRate;
    @SerializedName("buying_rate")
    private float buyingRate;
    @SerializedName("unit_value")
    private int unitValue;

    public int getUnitValue() {
        return unitValue;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public float getSellingRate() {
        return sellingRate;
    }

    public float getBuyingRate() {
        return buyingRate;
    }
}