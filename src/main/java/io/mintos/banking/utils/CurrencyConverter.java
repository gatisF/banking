package io.mintos.banking.utils;

import com.google.gson.Gson;
import io.mintos.banking.domain.ExchangeRate;
import io.mintos.banking.dto.ExchangeDto;
import io.mintos.banking.repos.ExchangeRateRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


@Component
public class CurrencyConverter {

    @Value("${exchange.api.url}")
    private String exchangeRateApiUrl;

    @Value("${exchange.key}")
    private String exchangeKey;

    private final ExchangeRateRepository exchangeRateRepository;

    public CurrencyConverter(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = Objects.requireNonNull(exchangeRateRepository);
    }

    public BigDecimal convert(String currencyFrom, String currencyTo, BigDecimal amount) {
        try {
            Gson gson = new Gson();
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request request = new Request.Builder()
                    .url(exchangeRateApiUrl.concat("?to=").concat(currencyTo).concat("&from=")
                            .concat(currencyFrom).concat("&amount=").concat(amount.toString()))
                    .addHeader("apikey", exchangeKey)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            ExchangeDto exchangeDto = gson.fromJson(response.body().string(), ExchangeDto.class);
            return exchangeDto.getResult();
        } catch (IOException e) {
            //Set default rate if service not available
            ExchangeRate exchangeRate = exchangeRateRepository.getExchangeRateBySource(currencyFrom.concat(currencyTo));
            return amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
