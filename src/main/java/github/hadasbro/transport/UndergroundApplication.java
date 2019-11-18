package github.hadasbro.transport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages={"github.hadasbro.transport"})
@EnableScheduling
public class UndergroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(UndergroundApplication.class, args);
	}

}
