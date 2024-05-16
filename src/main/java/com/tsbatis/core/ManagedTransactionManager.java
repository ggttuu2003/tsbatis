package com.tsbatis.core;

import java.sql.Connection;

/**
 * @author 尹强强
 * @version 1.0
 */
public class ManagedTransactionManager implements TransactionManager {
    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }

    @Override
    public void openConnection() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
