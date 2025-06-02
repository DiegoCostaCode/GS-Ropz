package br.fiap.ropz.ropz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RopzApplication {

	public static void main(String[] args) {
		SpringApplication.run(RopzApplication.class, args);
	}

}
