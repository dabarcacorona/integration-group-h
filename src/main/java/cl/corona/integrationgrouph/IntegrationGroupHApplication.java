package cl.corona.integrationgrouph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntegrationGroupHApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationGroupHApplication.class, args);
	}

}
