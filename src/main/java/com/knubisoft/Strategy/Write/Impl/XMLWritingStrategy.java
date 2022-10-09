package com.knubisoft.Strategy.Write.Impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.knubisoft.Strategy.DataReadWriteSource;
import com.knubisoft.Strategy.FileReadWriteSource;
import com.knubisoft.Strategy.Write.WritingStrategy;
import lombok.SneakyThrows;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class XMLWritingStrategy implements WritingStrategy {

    @Override
    @SneakyThrows
    public <T> void writeTo(DataReadWriteSource<?> src, List<T> objects) {
        File file = ((FileReadWriteSource) src).getSource();
        XmlMapper mapper = ((XmlMapper) new XmlMapper().findAndRegisterModules());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.writerWithDefaultPrettyPrinter().withRootName("root").writeValue(file, objects);
    }
}
