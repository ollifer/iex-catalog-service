package iex.rest;

import iex.model.CompanyPriceHistory;
import iex.model.CompanyPriceHistoryItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@ToString
public class CompanyPricesResponse {
    private String symbol;
    private String companyName;
    private String logoUrl;
    private List<Double> prices;

    public static CompanyPricesResponse fromCompanyPriceHistory(CompanyPriceHistory companyPriceHistory) {

        List<Double> prices = companyPriceHistory.getPriceHistory().stream()
                .map(CompanyPriceHistoryItem::getPrice)
                .collect(Collectors.toList());

        return new CompanyPricesResponse(
                companyPriceHistory.getCompany().getCompanyId(),
                companyPriceHistory.getCompany().getCompanyName(),
                companyPriceHistory.getCompany().getLogoUrl(),
                prices);
    }

}
