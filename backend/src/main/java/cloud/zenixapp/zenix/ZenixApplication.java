package cloud.zenixapp.zenix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZenixApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenixApplication.class, args);
	}

}
