package com.tsbatis.core;

import java.sql.Connection;

/**
 * @author 尹强强
 * @version 1.0
 */
public interface TransactionManager {

    void commit();

    void rollback();

    void close();

    void openConnection();

    Connection getConnection();

}
