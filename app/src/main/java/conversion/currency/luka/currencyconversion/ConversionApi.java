package conversion.currency.luka.currencyconversion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConversionApi {
    @GET("api/v1/rates/daily/")
    Call<List<Currency>> getCurrencies();

}