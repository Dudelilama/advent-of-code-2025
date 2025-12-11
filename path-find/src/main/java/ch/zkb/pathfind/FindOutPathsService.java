package ch.zkb.pathfind;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FindOutPathsService {

    @Value("classpath:paths.txt")
    Resource resourceFile;

    public void printPaths() throws IOException {
        List<PathEntry> paths = readPathsFromFile(resourceFile.getFile());

        log.info("Found Paths: {}", paths.size());
        // Print the parsed paths
        for (PathEntry path : paths) {
            log.info("Key: {}, Values: {}", path.getKey(), path.getValues());
        }

        HashMap<String, List<String>> pathsMap = new HashMap<>();
        paths.forEach(path -> pathsMap.put(path.getKey(), path.getValues()));

        log.info("Found unique Paths: {}", pathsMap.keySet().size());

        Stack<String> stackYou = new Stack<>();
        stackYou.push("you");
        log.info("found count: {}", findOut(stackYou, pathsMap, new HashSet<>()).size());


        Stack<String> stackSvr = new Stack<>();
        stackSvr.push("svr");
        log.info("found count: {}", findOut(stackSvr, pathsMap, new HashSet<>()));

    }

    private Set<List<String>> findOut(Stack<String> stack, HashMap<String, List<String>> pathsMap, @NotNull final Set<List<String>> paths) {
        List<String> nextSteps = pathsMap.get(stack.getLast());
        if(nextSteps == null || nextSteps.isEmpty()) {
            return paths;
        }

        nextSteps.forEach(nextStep -> {
            stack.push(nextStep);
            log.debug("Next step: {}", stack);
            if(nextStep.equals("out")) {
                log.debug("Found out Path: {}", stack);
                List<String> outPath = stack.stream().toList();
                if(paths.add(outPath)) {
                    log.info("Found out Path: {}", outPath);
                    log.debug("Out Paths: {}", paths);
                }
                else {
                    throw new IllegalStateException("Found duplicate out Path: " + outPath);
                }
            } else {
                findOut(stack, pathsMap, paths);
            }
            stack.pop();
        });
        return paths;
    }

    private List<PathEntry> readPathsFromFile(File filePath) {
        List<PathEntry> paths = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into key and values
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    List<String> values = Arrays.asList(parts[1].trim().split("\\s+"));
                    paths.add(new PathEntry(key, values));
                } else {
                    log.warn("Skipping invalid line: {}", line);
                }
            }
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
        }

        return paths;
    }
}
