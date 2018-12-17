package iex.repository;

import iex.model.Company;
import iex.model.CompanyPriceHistory;
import iex.model.CompanyPriceHistoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CompanyRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CompanyRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void addCompanyPriceHistoryItems(List<CompanyPriceHistoryItem> priceItems) {
        this.mongoTemplate.insertAll(priceItems);
    }

    public void saveCompanies(List<Company> companies) {
        companies.forEach(mongoTemplate::save);
    }

    //todo: add default timeFrame to avoid OOM
    public List<CompanyPriceHistory> findCompaniesPriceHistory(Set<String> companyIds, Optional<Duration> timeFrame) {

        Set<String> upperCaseIDs = companyIds.stream().map(String::toUpperCase).collect(Collectors.toSet());
        Query companyQuery = new Query(Criteria.where("_id").in(upperCaseIDs));
        List<Company> companies = mongoTemplate.find(companyQuery, Company.class);

        Criteria pricesCriteria = Criteria.where("companyId").in(upperCaseIDs);
        if (timeFrame.isPresent()) {
            Instant fromTimestamp = Instant.now().minus(timeFrame.get());
            pricesCriteria = pricesCriteria.and("timestamp").gte(fromTimestamp);
        }

        Query pricesQuery = new Query(pricesCriteria).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        List<CompanyPriceHistoryItem> priceHistoryItems = mongoTemplate.find(pricesQuery, CompanyPriceHistoryItem.class);

        Map<String, List<CompanyPriceHistoryItem>> priceHistoryByCompanyId =
                priceHistoryItems.stream().collect(Collectors.groupingBy(CompanyPriceHistoryItem::getCompanyId));

        return companies.stream()
                .map(company -> new CompanyPriceHistory(company, priceHistoryByCompanyId.get(company.getCompanyId())))
                .collect(Collectors.toList());
    }

    //test purpose methods
    List<Company> findCompaniesByIds(Set<String> companyIds) {
        Query companyQuery = new Query(Criteria.where("_id").in(companyIds));
        return mongoTemplate.find(companyQuery, Company.class);
    }

    List<CompanyPriceHistoryItem> findPricesByCompanyIds(Set<String> companyIds) {
        Query pricesQuery = new Query(Criteria.where("companyId").in(companyIds));
        return mongoTemplate.find(pricesQuery, CompanyPriceHistoryItem.class);
    }
}
