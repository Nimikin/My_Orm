package com.knubisoft.Strategy.Write.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knubisoft.Strategy.DataReadWriteSource;
import com.knubisoft.Strategy.FileReadWriteSource;
import com.knubisoft.Strategy.Write.WritingStrategy;
import lombok.SneakyThrows;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class JSONWritingStrategy implements WritingStrategy {

    @Override
    @SneakyThrows
    public <T> void writeTo(DataReadWriteSource<?> src, List<T> objects) {
        File file = ((FileReadWriteSource) src).getSource();
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, objects);
    }
}
