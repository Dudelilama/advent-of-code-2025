package ch.zkb.pathfind;

import lombok.Data;

import java.util.List;

@Data
public class PathEntry {
    private final String key;
    private final List<String> values;
}