package com.knubisoft.Strategy.Read;

import com.knubisoft.ORM;
import com.knubisoft.Strategy.DataReadWriteSource;

public interface ParsingStrategy<T extends DataReadWriteSource> {
    ORM.Table parseToTable(T content);
}