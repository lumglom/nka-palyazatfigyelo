package hu.szatmarbanda.palyazat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PalyazatfigyeloApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalyazatfigyeloApplication.class, args);
    }
}