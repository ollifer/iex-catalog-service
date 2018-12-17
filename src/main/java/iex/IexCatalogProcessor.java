package iex;

import iex.client.IexClient;
import iex.client.IexCompanyResponse;
import iex.model.Company;
import iex.model.CompanyPriceHistoryItem;
import iex.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IexCatalogProcessor {

    private final IexClient iexClient;
    private final CompanyRepository companyRepository;

    @Autowired
    public IexCatalogProcessor(IexClient iexClient,
                               CompanyRepository companyRepository) {
        this.iexClient = iexClient;
        this.companyRepository = companyRepository;
    }

    @Scheduled(fixedDelayString = "${app.iex.polling.interval.seconds}000")
    public void processIexCompanyData() {

        Collection<IexCompanyResponse> companiesData = iexClient.getCompaniesData();

        if (!companiesData.isEmpty()) {
            List<Company> companies = companiesData.stream().map(iexCompanyResponse ->
                    new Company(
                            iexCompanyResponse.getSymbol().toUpperCase(),
                            iexCompanyResponse.getCompanyName(),
                            iexCompanyResponse.getLogoUrl()
                    )).collect(Collectors.toList());
            companyRepository.saveCompanies(companies);

            Instant now = Instant.now();
            List<CompanyPriceHistoryItem> priceHistoryItems = companiesData.stream().map(iexCompanyResponse ->
                    new CompanyPriceHistoryItem(
                            iexCompanyResponse.getSymbol().toUpperCase(),
                            now,
                            iexCompanyResponse.getPrice()
                    )).collect(Collectors.toList());
            companyRepository.addCompanyPriceHistoryItems(priceHistoryItems);
        }
    }
}
