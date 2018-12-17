package iex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IexCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(IexCatalogApplication.class, args);
    }

}

