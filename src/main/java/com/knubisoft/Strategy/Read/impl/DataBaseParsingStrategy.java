package com.knubisoft.Strategy.Read.impl;

import com.knubisoft.ORM;
import com.knubisoft.Strategy.ConnectionReadWriteSource;
import com.knubisoft.Strategy.Read.ParsingStrategy;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataBaseParsingStrategy implements ParsingStrategy<ConnectionReadWriteSource> {

    @Override
    public ORM.Table parseToTable(ConnectionReadWriteSource content) {
        ResultSet rs = content.getContent();
        Map<Integer, Map<String, String>> result = buildTable(rs);
        return new ORM.Table(result);
    }

    @SneakyThrows
    private Map<Integer, Map<String, String>> buildTable(ResultSet rs) {
        ResultSetMetaData metadata = rs.getMetaData();

        Map<Integer, Map<String, String>> result = new LinkedHashMap<>();
        int rowId = 0;
        while (rs.next()) {
            Map<String, String> row = new LinkedHashMap<>();
            for (int index = 1; index < metadata.getColumnCount(); index++) {
                row.put(metadata.getColumnName(index), rs.getString(index));
            }
            result.put(rowId, row);
            rowId++;
        }
        return result;
    }
}
