package com.knubisoft.Strategy.Read.impl;

import com.knubisoft.ORM;
import com.knubisoft.Strategy.FileReadWriteSource;
import com.knubisoft.Strategy.Read.ParsingStrategy;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVParsingStrategy implements ParsingStrategy<FileReadWriteSource> {
    public static final String DELIMITER = ",";
    public static final String COMMENT = "--";

    public ORM.Table parseToTable(FileReadWriteSource content) {
        List<String> lines = Arrays.asList(content.getContent().split(System.lineSeparator()));
        Map<Integer, String> mapping = buildMapping(lines.get(0));
        Map<Integer, Map<String, String>> result = buildTable(lines.subList(1, lines.size()), mapping);
        return new ORM.Table(result);
    }

    private Map<Integer, Map<String, String>> buildTable(List<String> lines, Map<Integer, String> mapping) {
        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            result.put(index, biuldRow(mapping, line));
        }
        return result;
    }

    private Map<String, String> biuldRow(Map<Integer, String> mapping, String line) {
        Map<String, String> nameTovalueMap = new LinkedHashMap<>();
        String[] rowItems = splitLine(line);
        for (int rowIndex = 0; rowIndex < rowItems.length; rowIndex++) {
            String value = rowItems[rowIndex];
            nameTovalueMap.put(mapping.get(rowIndex), value);
        }
        return nameTovalueMap;
    }

    private Map<Integer, String> buildMapping(String firstLine) {
        Map<Integer, String> map = new LinkedHashMap<>();
        String[] array = splitLine(firstLine);
        for (int index = 0; index < array.length; index++) {
            String value = array[index];
            if (value.contains(COMMENT)) {
                value = value.split(COMMENT)[0];
            }
            map.put(index, value.trim());
        }
        return map;
    }

    private static String[] splitLine(String line) {
        return line.split(DELIMITER);
    }
}
