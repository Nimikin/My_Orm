package com.knubisoft;

import com.knubisoft.Strategy.ConnectionReadWriteSource;
import com.knubisoft.Strategy.DataReadWriteSource;
import com.knubisoft.Strategy.FileReadWriteSource;
import com.knubisoft.Strategy.Read.impl.CSVParsingStrategy;
import com.knubisoft.Strategy.Read.impl.DataBaseParsingStrategy;
import com.knubisoft.Strategy.Read.impl.JSONParsingStrategy;
import com.knubisoft.Strategy.Read.impl.XMLParsingStrategy;
import com.knubisoft.Strategy.Write.Impl.CSVWritingStrategy;
import com.knubisoft.Strategy.Write.Impl.JSONWritingStrategy;
import com.knubisoft.Strategy.Write.Impl.XMLWritingStrategy;
import com.knubisoft.Strategy.Write.WritingStrategy;
import com.knubisoft.Strategy.Read.ParsingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class ORM implements ORMInterface {

    @Override
    @SneakyThrows
    public <T> List<T> readAll(DataReadWriteSource<?> inputSource, Class<T> cls) {
        Table table = convertToTable(inputSource);
        return convertTableToListOfClasses(table, cls);
    }

    public <T> void writeAll(DataReadWriteSource inputSource, List<T> objects) {
        WritingStrategy writingStrategy = null;
        if (inputSource instanceof FileReadWriteSource){
            File file = ((FileReadWriteSource) inputSource).getSource();
            writingStrategy = getWritingStrategyForFile(file.getName());
            writingStrategy.writeTo(inputSource, objects);
        } else if (inputSource instanceof ConnectionReadWriteSource) {
            //writingStrategy = DBWriter();
        }
    }

    private WritingStrategy getWritingStrategyForFile(String fileName) {
        WritingStrategy writingStrategy;
        if (fileName.endsWith(".json")) {
            writingStrategy = new JSONWritingStrategy();
        } else if (fileName.endsWith(".xml")) {
            writingStrategy = new XMLWritingStrategy();
        } else if (fileName.endsWith(".csv")) {
            writingStrategy = new CSVWritingStrategy();
        }else{
            throw new UnsupportedOperationException("Unknown file format '" + fileName.split("\\.")[1] + "'");
        }
        return writingStrategy;
    }

    private <T> List<T> convertTableToListOfClasses(Table table, Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (int index = 0; index < table.size(); index++) {
            Map<String, String> row = table.getTableRowByIndex(index);
            T instance = reflectTableRowToClass(row, cls);
            result.add(instance);
        }
        return result;
    }

    @SneakyThrows
    private <T> T reflectTableRowToClass(Map<String, String> row, Class<T> cls) {
        T instance = cls.getDeclaredConstructor().newInstance();
        for (Field each : cls.getDeclaredFields()) {
            each.setAccessible(true);
            String value = row.get(each.getName());
            if (value != null) {
                each.set(instance, transformValueToFieldType(each, value));
            }
        }
        return instance;
    }

    private static Object transformValueToFieldType(Field field, String value) {
        Map<Class<?>, Function<String, Object>> typeToFunction = new LinkedHashMap<>();
        typeToFunction.put(String.class, s -> s);
        typeToFunction.put(int.class, Integer::parseInt);
        typeToFunction.put(Integer.class, Integer::parseInt);
        typeToFunction.put(float.class, Float::parseFloat);
        typeToFunction.put(Float.class, Float::parseFloat);
        typeToFunction.put(double.class, Double::parseDouble);
        typeToFunction.put(Double.class, Double::parseDouble);
        typeToFunction.put(LocalDate.class, LocalDate::parse);
        typeToFunction.put(LocalDateTime.class, LocalDate::parse);
        typeToFunction.put(long.class, Long::parseLong);
        typeToFunction.put(Long.class, Long::parseLong);
        typeToFunction.put(BigInteger.class, BigInteger::new);

        return typeToFunction.getOrDefault(field.getType(), type -> {
            throw new UnsupportedOperationException("Type is not supported by parser " + type);
        }).apply(value);
    }

    private Table convertToTable(DataReadWriteSource dataInputSource) {
        if (dataInputSource instanceof ConnectionReadWriteSource) {
            ConnectionReadWriteSource databaseSource = (ConnectionReadWriteSource) dataInputSource;
            return new DataBaseParsingStrategy().parseToTable(databaseSource);
        } else if (dataInputSource instanceof FileReadWriteSource) {
            FileReadWriteSource fileSource = (FileReadWriteSource) dataInputSource;
            return getStringParsingStrategy(fileSource).parseToTable(fileSource);
        } else {
            throw new UnsupportedOperationException("Unknown DataInputSource " + dataInputSource);
        }
    }

    private ParsingStrategy<FileReadWriteSource> getStringParsingStrategy(FileReadWriteSource inputSource) {
        String content = inputSource.getContent();
        char firstChar = content.charAt(0);
        switch (firstChar) {
            case '{':
            case '[':
                return new JSONParsingStrategy();
            case '<':
                return new XMLParsingStrategy();
            default:
                return new CSVParsingStrategy();
        }
    }

    @RequiredArgsConstructor
    public static class Table {
        private final Map<Integer, Map<String, String>> table;

        int size() {
            return table.size();
        }

        Map<String, String> getTableRowByIndex(int row) {
            Map<String, String> result = table.get(row);
            return result == null ? null : new LinkedHashMap<>(result);
        }
    }
}