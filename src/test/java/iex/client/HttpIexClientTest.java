package iex.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class HttpIexClientTest {

    private final RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final RestTemplate restTemplate = mock(RestTemplate.class);

    private HttpIexClient httpIexClient;

    @Before
    public void setUp() {
        adjustRestTemplateMock();
        httpIexClient = new HttpIexClient(
                restTemplateBuilder,
                Collections.singleton("aapl"),
                objectMapper);
    }


    @Test
    public void shouldGetCompaniesDataSuccessfully() throws IOException {

        String url = "https://api.iextrading.com/1.0/stock/market/batch?symbols=aapl&types=company,logo,price";

        ResponseEntity<String> okResponse = ResponseEntity.ok("Dummy response");

        given(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .willReturn(okResponse);

        IexCompanyResponse expected = new IexCompanyResponse(
                "aapl",
                "Apple",
                "logo url",
                160.01
        );

        given(objectMapper.readValue(eq(okResponse.getBody()), any(TypeReference.class)))
                .willReturn(Collections.singletonMap("aapl", expected));

        Collection<IexCompanyResponse> actual = httpIexClient.getCompaniesData();
        assertThat(actual).usingFieldByFieldElementComparator().containsExactly(expected);
    }

    @Test
    public void shouldReturnEmptyCompaniesDataOnErrorResponse()  {

        ResponseEntity<String> notFoundResponse = ResponseEntity.notFound().build();

        given(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .willReturn(notFoundResponse);

        Collection<IexCompanyResponse> actual = httpIexClient.getCompaniesData();
        assertThat(actual).isEmpty();

        verifyZeroInteractions(objectMapper);
    }

    private void adjustRestTemplateMock() {
        given(restTemplateBuilder.setConnectTimeout(any(Duration.class)))
                .willReturn(restTemplateBuilder);
        given(restTemplateBuilder.setReadTimeout(any(Duration.class)))
                .willReturn(restTemplateBuilder);
        given(restTemplateBuilder.errorHandler(any(IexClientResponseErrorHandler.class)))
                .willReturn(restTemplateBuilder);
        given(restTemplateBuilder.build()).willReturn(restTemplate);
    }


}