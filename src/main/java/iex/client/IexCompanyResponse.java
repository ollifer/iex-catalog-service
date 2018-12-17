package iex.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@ToString
public class IexCompanyResponse {

    private String symbol;
    private String companyName;
    private String logoUrl;
    private Double price;

    @JsonProperty("company")
    private void unpackNestedCompany(Map<String, Object> brand) {
        this.symbol = (String) brand.get("symbol");
        this.companyName = (String) brand.get("companyName");
    }

    @JsonProperty("logo")
    private void unpackNestedLogo(Map<String, Object> logo) {
        this.logoUrl = (String) logo.get("url");
    }
}
