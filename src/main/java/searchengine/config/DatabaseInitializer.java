package searchengine.config;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @PostConstruct
    public void init() {
        System.out.println("Database initialized via Liquibase for PostgreSQL");
    }
}
