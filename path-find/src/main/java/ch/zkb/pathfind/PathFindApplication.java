package ch.zkb.pathfind;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.List;

@SpringBootApplication
@Slf4j
public class PathFindApplication implements CommandLineRunner {

    @Autowired
    private FindOutPathsService findOutPathsService;


    public static void main(String[] args) {
        SpringApplication.run(PathFindApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        findOutPathsService.printPaths();
    }
}
