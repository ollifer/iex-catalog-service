package iex.rest;

import iex.model.CompanyPriceHistory;
import iex.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("catalog/company")
@Slf4j
public class CompanyController {
    private final ChronoUnit defaultChronoUnit;
    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(CompanyRepository companyRepository,
                             @Value("${app.defaultChronoUnit}") ChronoUnit defaultChronoUnit) {
        this.companyRepository = companyRepository;
        this.defaultChronoUnit = defaultChronoUnit;
    }

    @GetMapping("price")
    public List<CompanyPricesResponse> getCompaniesPrices(@RequestParam Set<String> symbols,
                                                          @RequestParam(required = false) Long timeFrame,
                                                          @RequestParam(required = false) ChronoUnit chronoUnit) {

        log.debug("Got get company prices request: {} {} {}", symbols, timeFrame, chronoUnit);

        Optional<Duration> timeFrameOpt =
                Optional.ofNullable(timeFrame)
                        .map(aLong -> Duration.of(aLong, Optional.ofNullable(chronoUnit).orElse(defaultChronoUnit)));

        List<CompanyPriceHistory> companiesPriceHistory = companyRepository.findCompaniesPriceHistory(symbols, timeFrameOpt);

        return companiesPriceHistory.stream().map(CompanyPricesResponse::fromCompanyPriceHistory).collect(Collectors.toList());
    }

}
