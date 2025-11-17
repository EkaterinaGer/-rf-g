package searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import searchengine.config.DatabaseInitializer;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        DatabaseInitializer.createDatabaseIfNotExists();

        SpringApplication.run(Application.class, args);
    }
}