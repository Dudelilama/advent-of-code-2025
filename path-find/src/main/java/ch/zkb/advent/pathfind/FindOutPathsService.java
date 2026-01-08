package ch.zkb.advent.pathfind;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@ShellComponent
@Slf4j
public class FindOutPathsService {

    @Value("classpath:paths.txt")
    private Resource resourceFile;

    private final HashMap<String, Long> cache = new HashMap();

    @ShellMethod(key = "11-print-pahts")
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
        log.info("found count: {}", findOut(stackYou, pathsMap, new HashSet<>(), "out").size());


        // srv - dac 0 fft - out 0
        // srv - fft - dac 0 out
        Stack<String> stackDac = new Stack<>();
        stackDac.push("dac");
        log.info("found count: {}", findOut(stackDac, pathsMap, new HashSet<>(), "out").size());

//        Path1
        var segment11 = countSegment("svr", pathsMap, "dac");
        var segment12 = countSegment("dac", pathsMap, "fft");
        var segment13 = countSegment("fft", pathsMap, "out");

        Long pathCount1 = segment11.longValue() * segment12.longValue() * segment13.longValue();
        log.info("found path1 count: {}", pathCount1);


        //Path2
        var segment21 = countSegment("svr", pathsMap, "fft");
        var segment22 = countSegment("fft", pathsMap, "dac");
        var segment23 = countSegment("dac", pathsMap, "out");

        Long pathCount2 = segment21.longValue() * segment22.longValue() * segment23.longValue();
        log.info("found path2 count: {}", pathCount2);

        Long total = pathCount1 + pathCount2;

        log.info("found count: {}", total);

    }

    private AtomicLong countSegment(String startSegment, HashMap<String, List<String>> pathsMap, String endSegment) {
        Stack<String> startStack = new Stack<>();
        startStack.push(startSegment);
        var count = countOut(startStack, pathsMap, endSegment);
        cache.clear();
        log.info("from {} to {} count: {}", startSegment, endSegment, count.get());
        return count;
    }

    private AtomicLong countOut(Stack<String> stack, HashMap<String, List<String>> pathsMap, String pathEnd) {
        final var pathsCount = new AtomicLong(0);
        List<String> nextSteps = pathsMap.get(stack.getLast());

        if (nextSteps == null) {
            return pathsCount;
        }

        nextSteps.forEach(nextStep -> {
            if (stack.contains(nextStep)) {
                log.warn("Found loop with next step {} in Path: {}", nextStep, stack);
                return;
            }
            stack.push(nextStep);
            var cachedCount = cache.get(nextStep);
            if (cachedCount == null) {
                log.debug("Next step: {}", stack);
                if (nextStep.equals(pathEnd)) {
                    List<String> outPath = stack.stream().toList();
                    log.debug("{} Found out Path: {}", (int) pathsCount.incrementAndGet(), outPath);
                } else {
                    log.debug("{} Recursion out: {}", (int) pathsCount.addAndGet(countOut(stack, pathsMap, pathEnd).get()), stack);
                }
            } else {
                log.debug("{} Cached out: {}", (int) pathsCount.addAndGet(cachedCount), stack);
            }
            stack.pop();
        });
        cache.putIfAbsent(stack.getLast(), pathsCount.get());
        log.debug("Cached {},  {}", stack.getLast(), pathsCount.get());

        return pathsCount;
    }

    private Set<List<String>> findOut(Stack<String> stack, HashMap<String, List<String>> pathsMap, @NotNull final Set<List<String>> paths, String pathEnd) {
        List<String> nextSteps = pathsMap.get(stack.getLast());
        if (nextSteps == null || nextSteps.isEmpty()) {
            return paths;
        }

        nextSteps.forEach(nextStep -> {
            if (stack.contains(nextStep)) {
                log.warn("Found loop with next step {} in Path: {}", nextStep, stack);
                return;
            }
            stack.push(nextStep);
            log.debug("Next step: {}", stack);
            if (nextStep.equals(pathEnd)) {
                List<String> outPath = stack.stream().toList();
                if (paths.add(outPath)) {
                    log.info("{} Found out Path: {}", paths.size(), outPath);
                    log.debug("Out Paths: {}", paths);
                } else {
                    throw new IllegalStateException("Found duplicate out Path: " + outPath);
                }
            } else {
                findOut(stack, pathsMap, paths, pathEnd);
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
