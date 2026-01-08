package ch.zkb.advent.tresor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ShellComponent
@Slf4j
public class TresorService {

    @Value("classpath:tresor.txt")
    private Resource resourceFile;

    @ShellMethod
    public void crack() throws IOException {
        List<Movement> movements = readTresor(resourceFile.getFile());
        log.info("Found {} movement in file", movements);

        AtomicInteger pointer = new AtomicInteger(50);
        long count = movements.stream()
                .map(movement -> createTresorMove(movement, pointer))
                .peek(tresorMove -> log.info("Created Tresor move {}", tresorMove))
                .filter(tresorMove -> tresorMove.currentPointer == 0)
                .count();

        log.info("Found {} movements with 0 in file", count);

        AtomicInteger pointer2 = new AtomicInteger(50);
        long count2 = movements.stream()
                .map(movement -> createTresorMove(movement, pointer2))
                .peek(tresorMove -> log.info("Created Tresor move {}", tresorMove))
                .mapToInt(TresorMove::zeroCrossedCount)
                .sum();

        log.info("Found {} zero cosses during clicks", count2);
    }

    public static TresorMove createTresorMove(Movement movement, AtomicInteger pointer) {
        int oldPointer = pointer.get();
        int movementClicksDirected;
        switch (movement.direction) {
            case L -> movementClicksDirected = -movement.clicks;
            case R -> movementClicksDirected = movement.clicks;
            default -> throw new IllegalStateException("Unexpected value: " + movement.direction);
        }

        pointer.set(((oldPointer + movementClicksDirected) % 100 + 100) % 100);

        return new TresorMove(movement, pointer.get(), ((Math.abs(oldPointer + movementClicksDirected)) / 100)) ;
    }

    private List<Movement> readTresor(File file) {
        ArrayList<Movement> movements = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                movements.add(new Movement(Direction.valueOf(line.substring(0, 1)), Integer.parseInt(line.substring(1))));
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", file, e);
        }

        return movements;
    }

    public record Movement(Direction direction, int clicks) {
    }

    public record TresorMove(Movement movement, int currentPointer, int zeroCrossedCount) {
    }

    public enum Direction {L, R}
}
