package iex.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "company")
public class Company {
    @Id
    private String companyId;
    private String companyName;
    private String logoUrl;
}
