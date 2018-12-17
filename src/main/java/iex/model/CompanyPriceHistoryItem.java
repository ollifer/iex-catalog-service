package iex.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@ToString
@Document(collection = "price_history_item")
public class CompanyPriceHistoryItem {
    private String companyId;
    private Instant timestamp;
    private Double price;

    public CompanyPriceHistoryItem(String companyId, Instant timestamp, Double price) {
        this.companyId = companyId;
        this.timestamp = timestamp;
        this.price = price;
    }
}
