package com.casino.CasinoProject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlayerService {

	public static void main(String[] args) {
		SpringApplication.run(PlayerService.class, args);
	}

}
