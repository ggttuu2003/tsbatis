package com.tsbatis.core;

import java.lang.reflect.Method;
import java.sql.*;

/**
 * @author 尹强强
 * @version 1.0
 */
public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void insert(String sqlId, Object object){
        int count = 0;
        try {
            MappedStatement mappedStatement = sqlSessionFactory.getMappedStatement().get(sqlId);
            String resultType = mappedStatement.getResultType();
            String godbatisSql = mappedStatement.getSql();
            Connection connection = sqlSessionFactory.getTransactionManager().getConnection();
            String sql = godbatisSql.replaceAll("#\\{[0-9a-zA-z_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int fromIndex = 0;
            int index = 1;
            while (true){
                int jingIndex = godbatisSql.indexOf("#", fromIndex);
                if (jingIndex < 0){
                    break;
                }
                int youkuohaoIndex = godbatisSql.indexOf("}", fromIndex);
                String propertyName = godbatisSql.substring(jingIndex + 2, youkuohaoIndex).trim();
                fromIndex = youkuohaoIndex + 1;
                String methodName = "get"+propertyName.toUpperCase().charAt(0)+propertyName.substring(1);
                Method method = object.getClass().getDeclaredMethod(methodName);
                String propertyValue = (String) method.invoke(object);
                preparedStatement.setString(index,propertyValue);
                index++;
            }

            count = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object selectOne(String sqlId,Object object){
        Object result = null;
        try {
            MappedStatement mappedStatement = sqlSessionFactory.getMappedStatement().get(sqlId);
            String resultType = mappedStatement.getResultType();
            String godbatisSql = mappedStatement.getSql();
            Connection connection = sqlSessionFactory.getTransactionManager().getConnection();
            String sql = godbatisSql.replaceAll("#\\{[0-9a-zA-z_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int fromIndex = 0;
            int index = 1;
            while (true){
                int jingIndex = godbatisSql.indexOf("#",fromIndex);
                if (jingIndex < 0){
                    break;
                }
                int youkuohaoIndex = godbatisSql.indexOf("}",fromIndex);
                fromIndex = youkuohaoIndex + 1;
                preparedStatement.setString(index,object.toString());
                index++;
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Class<?> clazz = Class.forName(resultType);
                result = clazz.newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String propertyName = metaData.getColumnName(i + 1);
                    String setMethod = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                    Method method = clazz.getDeclaredMethod(setMethod,String.class);
                    method.invoke(result,resultSet.getString(propertyName));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void commit(){
        sqlSessionFactory.getTransactionManager().commit();
    }

    public void rollback(){
        sqlSessionFactory.getTransactionManager().rollback();
    }

    public void close(){
        sqlSessionFactory.getTransactionManager().close();
    }
}
