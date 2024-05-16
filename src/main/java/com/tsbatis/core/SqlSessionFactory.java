package com.tsbatis.core;

import java.util.Map;

/**
 * @author 尹强强
 * @version 1.0
 */
public class SqlSessionFactory {

    /**
     * 事务管理器借口，实现灵活切换
     */
    private TransactionManager transactionManager;

    /**
     * mapper
     */
    private Map<String, MappedStatement> mappedStatements;

    public SqlSessionFactory() {
    }

    public SqlSessionFactory(TransactionManager transactionManager, Map<String, MappedStatement> mappedStatements) {
        this.transactionManager = transactionManager;
        this.mappedStatements = mappedStatements;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Map<String, MappedStatement> getMappedStatement() {
        return mappedStatements;
    }

    public void setMappedStatement(Map<String, MappedStatement> mappedStatement) {
        this.mappedStatements = mappedStatement;
    }
    public SqlSession openSession(){
        transactionManager.openConnection();
        SqlSession sqlSession = new SqlSession(this);
        return sqlSession;
    }
}
