package iex;


import org.junit.ClassRule;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;

@ContextConfiguration(initializers = {AbstractContainerizedMongoIT.Initializer.class})
public class AbstractContainerizedMongoIT {

    @ClassRule
    public static GenericContainer mongodb = new GenericContainer("mongo:latest").withExposedPorts(27017);

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.mongodb.host=" + mongodb.getContainerIpAddress(),
                    "spring.data.mongodb.port=" + mongodb.getMappedPort(27017),
                    "spring.data.mongodb.database=iexcatalog"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
