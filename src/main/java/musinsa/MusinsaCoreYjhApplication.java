package musinsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MusinsaCoreYjhApplication {

  public static void main(String[] args) {
    SpringApplication.run(MusinsaCoreYjhApplication.class, args);
  }
}

