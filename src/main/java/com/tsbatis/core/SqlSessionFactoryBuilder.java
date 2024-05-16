package com.tsbatis.core;

import com.tsbatis.constant.Const;
import com.tsbatis.utils.Resouces;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 尹强强
 * @version 1.0
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactoryBuilder(){}

    public SqlSessionFactory build(InputStream inputStream){
        SqlSessionFactory factory = null;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            String xpath = "/configuration/environments";
            Element environmentsElt = (Element) document.selectSingleNode(xpath);
            String defaultId = environmentsElt.attributeValue("default");
            xpath = "/configuration/environments/environment['@id="+defaultId+"']";
            Element environmentElt = (Element) document.selectSingleNode(xpath);
            Element transactionManagerElt = environmentElt.element("transactionManager");
            Element dataSourceElt = environmentElt.element("dataSource");
            List<String> sqlMapperPaths = new ArrayList<>();
            List<Element> mapperElts = document.selectNodes("//mapper");
            mapperElts.forEach(mapperElt ->{
                String sqlMapperPath = mapperElt.attributeValue("resource");
                sqlMapperPaths.add(sqlMapperPath);
            });
            DataSource dataSource = getDataSource(dataSourceElt);
            TransactionManager transactionManager = getTransaction(transactionManagerElt,dataSource);
            Map<String,MappedStatement> mappedStatements = getMappedStatements(sqlMapperPaths);
            factory = new SqlSessionFactory(transactionManager, mappedStatements);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return factory;
    }

    private Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperPaths) {
        Map<String, MappedStatement> map = new HashMap<>();
        sqlMapperPaths.forEach(sqlMapperPath ->{
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(Resouces.getResourceAsReader(sqlMapperPath));
                Element mapperEtl = (Element) document.selectSingleNode("/mapper");
                String namespace = mapperEtl.attributeValue("namespace");
                List<Element> sqlEtls = mapperEtl.elements();
                sqlEtls.forEach(sqlEtl->{
                    String id = sqlEtl.attributeValue("id");
                    String sqlId = namespace + "."+id;
                    String resultType = sqlEtl.attributeValue("resultType");
                    String sql = sqlEtl.getTextTrim();
                    MappedStatement mappedStatement = new MappedStatement();
                    mappedStatement.setSql(sql);
                    mappedStatement.setResultType(resultType);
                    map.put(sqlId,mappedStatement);
                });
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    private TransactionManager getTransaction(Element transactionManagerElt, DataSource dataSource) {
        String type = transactionManagerElt.attributeValue("type").trim().toUpperCase();
        if (Const.JDBC_TRASACTIONMANAGER.equals(type)){
            return new JDBCTransactionManager(dataSource,false);
        }else if (Const.MANAGED_TRASACTIONMANAGER.equals(type)){
            return new ManagedTransactionManager();
        }else {
            throw new RuntimeException("请正确配置事务管理器");
        }
    }

    private DataSource getDataSource(Element dataSourceElt) {
        String type = dataSourceElt.attributeValue("type").trim().toUpperCase();
        Map<String ,String> map = new HashMap<>();
        List<Element> propertyElts = dataSourceElt.elements();
        propertyElts.forEach(propertyElt ->{
            String name = propertyElt.attributeValue("name");
            String value = propertyElt.attributeValue("value");
            map.put(name,value);
        });
        switch (type){
            case Const.UN_POOLED_DATASOURCE:
                return new UnPooledDataSource(map.get("driver"),map.get("url"),map.get("username"),map.get("password"));
            case Const.POOLED_DATASOURCE:
                return new PoolDataSource();
            case Const.JNDI_DATASOURCE:
                return new JNDIDataSource();
            default:
                System.out.println("数据源配置出错");
                break;
        }
        throw new RuntimeException();
    }
}
