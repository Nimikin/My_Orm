package com.knubisoft.Strategy.Write.Impl;

import com.knubisoft.Strategy.DataReadWriteSource;
import com.knubisoft.Strategy.FileReadWriteSource;
import com.knubisoft.Strategy.Write.WritingStrategy;
import lombok.SneakyThrows;
import org.csveed.api.CsvClientImpl;
import org.csveed.bean.conversion.AbstractConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class CSVWritingStrategy implements WritingStrategy {

    @Override
    @SneakyThrows
    public <T> void writeTo(DataReadWriteSource<?> src, List<T> objects) {
        File file = ((FileReadWriteSource) src).getSource();
        try(Writer writer = new FileWriter(file)) {
            Class<T> clazz = (Class<T>) objects.get(0).getClass();
            CsvClientImpl<T> csvWriter = new CsvClientImpl<>(writer,clazz);
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.getType().equals(LocalDate.class))
                    .forEach(field -> {
                        LocalDateConverter converter = new LocalDateConverter(LocalDate.class);
                        csvWriter.setConverter(file.getName(), converter);
                    });
            csvWriter.writeBeans(objects);
        }
    }

    static class LocalDateConverter extends AbstractConverter<LocalDate>{
        private String format = "yyyy-MM-dd";

        public void setFormatOrDefault(String format){
            if (format != null && !format.isEmpty()){
                this.format = format;
            }
        }

        public LocalDateConverter(Class<LocalDate> clazz){
            super(clazz);
        }

        @Override
        public LocalDate fromString(String text) {
            return LocalDate.parse(text);
        }

        @Override
        public String toString(LocalDate value) {
            return value.format(DateTimeFormatter.ofPattern(format));
        }
    }
}
