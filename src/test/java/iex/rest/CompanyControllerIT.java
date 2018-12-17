package iex.rest;

import iex.AbstractContainerizedMongoIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class CompanyControllerIT extends AbstractContainerizedMongoIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldGet2PricesAfter3SecWittPollingInterval2Sec() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ResponseEntity<List<CompanyPricesResponse>> response = restTemplate.exchange(
                        "/catalog/company/price?symbols=aapl&symbols=fb&timeFrame=3&chronoUnit=SECONDS",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CompanyPricesResponse>>() {
                        });

                assertThat(response.getStatusCodeValue()).isEqualTo(200);
                List<CompanyPricesResponse> companyPricesResponse = response.getBody();

                log.info("Company prices response:  {}", companyPricesResponse);

                assertThat(companyPricesResponse).hasSize(1);

                CompanyPricesResponse companyPrice = companyPricesResponse.get(0);

                assertThat(companyPrice.getSymbol()).isEqualTo("AAPL");
                assertThat(companyPrice.getCompanyName()).isNotBlank();
                assertThat(companyPrice.getLogoUrl()).isNotBlank();

                assertThat(companyPrice.getPrices()).hasSize(2);

                latch.countDown();
            }
        }, 3000);

        latch.await();
    }


}