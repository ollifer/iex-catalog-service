package iex.rest;

import iex.AbstractContainerizedMongoIT;
import iex.model.Company;
import iex.model.CompanyPriceHistoryItem;
import iex.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class CompanyControllerIT extends AbstractContainerizedMongoIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Before
    public void setUp() {
        prepareDummyData();
    }

    @Test
    public void shouldGetCompanyPricesOkResponse() {

        ResponseEntity<List<CompanyPricesResponse>> response = restTemplate.exchange(
                "/catalog/company/price?symbols=aapl&symbols=fb&timeFrame=3&chronoUnit=SECONDS",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CompanyPricesResponse>>() {
                });

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        List<CompanyPricesResponse> companyPricesResponse = response.getBody();
        log.info("Company prices response:  {}", companyPricesResponse);

        assertThat(companyPricesResponse).usingRecursiveFieldByFieldElementComparator()
                .containsExactly(new CompanyPricesResponse("AAPL",
                        "Apple",
                        "logo url",
                        Collections.singletonList(165.48)));
    }

    private void prepareDummyData() {
        Company apple = new Company("AAPL", "Apple", "logo url");
        companyRepository.saveCompanies(Collections.singletonList(apple));
        CompanyPriceHistoryItem applePrice = new CompanyPriceHistoryItem("AAPL", Instant.now(), 165.48);
        companyRepository.addCompanyPriceHistoryItems(Collections.singletonList(applePrice));
    }

}