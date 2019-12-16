package hu.galambo.gobelin;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GobelinmakerApplication implements CommandLineRunner {

	@Value("${app.version}")
	private String appVersion;

	
	public static void main(String[] args) {
		SpringApplication.run(GobelinmakerApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
	}
	
	@PreDestroy
	public void onExit() {
	}


}
