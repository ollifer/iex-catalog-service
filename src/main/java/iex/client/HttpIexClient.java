package iex.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

//todo: possibly split into different uri to fetch companies and logos first
// in order to ease batch calls

@Component
@Slf4j
public class HttpIexClient implements IexClient {

    private static final String IEX_BATCH_URL_TEMPLATE =
            "https://api.iextrading.com/1.0/stock/market/batch?symbols=%s&types=company,logo,price";
    private String iexBatchUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public HttpIexClient(RestTemplateBuilder builder,
                         @Value("${app.iex.polling.symbols}") Set<String> symbols,
                         ObjectMapper objectMapper) {

        iexBatchUrl = String.format(IEX_BATCH_URL_TEMPLATE, String.join(",", symbols));
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(2000))
                .errorHandler(new IexClientResponseErrorHandler())
                .build();
        this.objectMapper = objectMapper;
    }

    //todo: revise exception handling
    @Override
    public Collection<IexCompanyResponse> getCompaniesData() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(iexBatchUrl, HttpMethod.GET,
                request,
                String.class);
        String responseBody = response.getBody();

        try {
            if (isErrorResponse(response.getStatusCode())) {
                return Collections.emptyList();
            } else {
                Map<String, IexCompanyResponse> iexCompanyResponse = objectMapper.readValue(responseBody,
                        new TypeReference<Map<String, IexCompanyResponse>>() {
                        });
                log.debug("Got iex response: {}", iexCompanyResponse);
                return iexCompanyResponse.values();
            }
        } catch (IOException e) {
            log.error("Failed to read response: ", e);
            return Collections.emptyList();
        }
    }

    private boolean isErrorResponse(HttpStatus status) {
        return status.series() == CLIENT_ERROR
                || status.series() == SERVER_ERROR;
    }

}
