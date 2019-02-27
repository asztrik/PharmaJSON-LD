package pharma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	/******************************************************/
	//
	// TODO add this: https://docs.spring.io/spring-data/neo4j/docs/5.1.3.RELEASE/reference/html/ 
	//
	/******************************************************/
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
