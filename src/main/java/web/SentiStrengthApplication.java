package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"web"})
public class SentiStrengthApplication {
  public static void main(String[] args) {
    SpringApplication.run(SentiStrengthApplication.class, args);
  }
}
