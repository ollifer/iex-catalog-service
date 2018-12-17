package iex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class CompanyPriceHistory {

    private Company company;
    private List<CompanyPriceHistoryItem> priceHistory;
}
