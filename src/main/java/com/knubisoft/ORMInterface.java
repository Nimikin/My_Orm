package com.knubisoft;

import com.knubisoft.Strategy.DataReadWriteSource;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;

public interface ORMInterface {

    @SneakyThrows
    <T> List<T> readAll(DataReadWriteSource<?> source, Class<T> cls);
    @SneakyThrows
    default <T> void writeAll(DataReadWriteSource content, List<T> objects) throws IOException { };
}
