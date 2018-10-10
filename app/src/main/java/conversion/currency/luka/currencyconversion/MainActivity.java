package conversion.currency.luka.currencyconversion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ConversionApi conversionApi;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button buttonSubmit;
    private List<Currency> currencyList;
    private TextView buyingRateResult;
    private TextView sellingRateResult;
    private Button buttonSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRetrofit();
        sendRequest();

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buyingRateResult = findViewById(R.id.textResultBuying);
        sellingRateResult = findViewById(R.id.textResultSelling);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });

        buttonSwitch = findViewById(R.id.buttonSwitch);

        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCurrency();
            }
        });
    }

    private void calculate() {
        Currency from = currencyList.get(spinnerFrom.getSelectedItemPosition());
        Currency to = currencyList.get(spinnerTo.getSelectedItemPosition());

        float buyFrom = from.getBuyingRate();
        float buyTo = to.getBuyingRate();

        float sellFrom = from.getSellingRate();
        float sellTo = to.getSellingRate();

        EditText amount = findViewById(R.id.amount);
        if (TextUtils.isEmpty(amount.getText().toString()) || !TextUtils.isDigitsOnly(amount.getText().toString()))
            amount.setError("Please type the valid amount you want to convert");
        else {
            int amountInt = Integer.parseInt(amount.getText().toString());
            float resultFrom = ((buyFrom / from.getUnitValue()) / buyTo) * to.getUnitValue();
            float resultTo = ((sellFrom / from.getUnitValue()) / sellTo) * to.getUnitValue();

            buyingRateResult.setText(amountInt +  " " + from.getCurrencyCode() + " = " + resultFrom * amountInt + " " + to.getCurrencyCode());
            sellingRateResult.setText(amountInt +  " " + from.getCurrencyCode() + " = " + resultTo * amountInt + " " + to.getCurrencyCode());
        }
    }

    private void switchCurrency() {
        int from = spinnerFrom.getSelectedItemPosition();
        int to = spinnerTo.getSelectedItemPosition();

        spinnerFrom.setSelection(to);
        spinnerTo.setSelection(from);

        if (!TextUtils.isEmpty(buyingRateResult.getText().toString())) calculate();
    }

    private void sendRequest() {
        conversionApi.getCurrencies().enqueue(new Callback<List<Currency>>() {
            @Override
            public void onResponse(Call<List<Currency>> call, Response<List<Currency>> response) {
                if (response.body() != null)
                    initSpinner(response.body());
            }

            @Override
            public void onFailure(Call<List<Currency>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://hnbex.eu/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        conversionApi = retrofit.create(ConversionApi.class);
    }

    private void initSpinner(List<Currency> list) {
        currencyList = list;
        spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        List<String> currencyCodes = new ArrayList<>();
        for (Currency item : list) {
            currencyCodes.add(item.getCurrencyCode());
        }
        setSpinnerAdapter(spinnerFrom, currencyCodes);
        setSpinnerAdapter(spinnerTo, currencyCodes);

    }

    private void setSpinnerAdapter(Spinner spinner, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}