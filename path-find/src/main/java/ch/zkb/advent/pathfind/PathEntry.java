package ch.zkb.advent.pathfind;

import lombok.Data;

import java.util.List;

@Data
public class PathEntry {
    private final String key;
    private final List<String> values;
}