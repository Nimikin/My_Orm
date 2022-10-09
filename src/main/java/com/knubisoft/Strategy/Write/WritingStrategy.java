package com.knubisoft.Strategy.Write;

import com.knubisoft.Strategy.DataReadWriteSource;

import java.io.File;
import java.util.List;

public interface WritingStrategy {
    <T> void writeTo(DataReadWriteSource<?> src, List<T> objects);
}
