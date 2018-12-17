package iex.repository;

import iex.AbstractContainerizedMongoIT;
import iex.model.Company;
import iex.model.CompanyPriceHistory;
import iex.model.CompanyPriceHistoryItem;
import org.apache.commons.compress.utils.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import(CompanyRepository.class)
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class CompanyRepositoryTest extends AbstractContainerizedMongoIT {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void shouldSaveCompanies() {

        Company twitter = new Company("TW", "Twitter", "twitter logo url");
        Company amazon = new Company("AM", "Amazon", "amazon logo url");

        companyRepository.saveCompanies(Arrays.asList(twitter, amazon));

        List<Company> actual = companyRepository.findCompaniesByIds(Sets.newHashSet("TW", "AM"));

        assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(twitter, amazon);

    }

    @Test
    public void shouldAddCompanyPriceHistoryItems() {

        CompanyPriceHistoryItem twitterPrice = new CompanyPriceHistoryItem("TW", Instant.now(), 165.48);
        CompanyPriceHistoryItem amazonPrice = new CompanyPriceHistoryItem("AM", Instant.now(), 155.32);

        companyRepository.addCompanyPriceHistoryItems(Arrays.asList(twitterPrice, amazonPrice));

        List<CompanyPriceHistoryItem> actual = companyRepository.findPricesByCompanyIds(Sets.newHashSet("TW", "AM"));

        assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(twitterPrice, amazonPrice);
    }


    @Test
    public void shouldFindAppleCompaniesPriceHistoryFor90Minutes() {

        Company apple = new Company("AAPL", "Apple", "apple logo url");
        Company fb = new Company("FB", "Facebook", "fb logo url");

        Instant now = Instant.now();

        CompanyPriceHistoryItem currentApplePrice = new CompanyPriceHistoryItem("AAPL", now, 165.48);
        CompanyPriceHistoryItem applePriceMinusHour = new CompanyPriceHistoryItem(
                "AAPL", now.minus(1, ChronoUnit.HOURS), 166.48);
        CompanyPriceHistoryItem applePriceMinus2Hours = new CompanyPriceHistoryItem(
                "AAPL", now.minus(2, ChronoUnit.HOURS), 167.48);
        CompanyPriceHistoryItem fbPrice = new CompanyPriceHistoryItem("FB", now, 155.32);

        companyRepository.saveCompanies(Arrays.asList(apple, fb));
        companyRepository.addCompanyPriceHistoryItems(Arrays.asList(currentApplePrice, applePriceMinusHour, applePriceMinus2Hours, fbPrice));

        List<CompanyPriceHistory> actual = companyRepository.findCompaniesPriceHistory(Collections.singleton("AAPL"),
                Optional.of(Duration.ofMinutes(90)));

        List<CompanyPriceHistory> expected = Collections.singletonList(
                new CompanyPriceHistory(apple, Arrays.asList(
                        new CompanyPriceHistoryItem(currentApplePrice.getCompanyId(), currentApplePrice.getTimestamp(), currentApplePrice.getPrice()),
                        new CompanyPriceHistoryItem(applePriceMinusHour.getCompanyId(), applePriceMinusHour.getTimestamp(), applePriceMinusHour.getPrice())
                ))
        );

        assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(expected);
    }


}