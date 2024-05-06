package obed.org.apirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsgoApplication.class, args);
	}

}
