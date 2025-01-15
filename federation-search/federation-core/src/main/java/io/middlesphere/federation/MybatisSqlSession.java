package io.middlesphere.federation;

import io.middlesphere.federation.config.FederationConfig;
import io.middlesphere.federation.util.FastjsonObjectMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author Administrator
 */
public class MybatisSqlSession {

    private FederationConfig federationConfig;

    private  SqlSession  sqlSession;
    public MybatisSqlSession(FederationConfig federationConfig){
        Properties properties = new Properties();
        properties.setProperty("caseSensitive", String.valueOf(federationConfig.isCaseSensitive()));
        properties.put("model","inline:"+ FastjsonObjectMapper.toJSONString(federationConfig.getJdbcConfig()));
        try  {
            Connection connection = DriverManager.getConnection("jdbc:calcite:",properties);
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
             sqlSession =  sqlSessionFactory.openSession(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  <T> T getMapper(Class<T> type) {
        return sqlSession.getMapper(type);
    }
}
