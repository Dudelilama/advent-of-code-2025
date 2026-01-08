package ch.zkb.advent;

import ch.zkb.advent.pathfind.FindOutPathsService;
import ch.zkb.advent.tresor.TresorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AdventApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdventApplication.class, args);
    }
}
