package com.atzuche.config.server.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 2:50 下午
 **/
@Configuration
@MapperScan(basePackages="com.atzuche.config.server.mapper.second", sqlSessionFactoryRef = "secondSqlSessionFactory")
public class SecondDataSourceConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${second.datasource.driverClassName}")
    private String driverClassName;
    @Value("${second.datasource.url}")
    private String url;
    @Value("${second.datasource.username}")
    private String username;
    @Value("${second.datasource.password}")
    private String password;
    @Value("${second.mybatis.mapper-locations}")
    private String[] mapperLocations;
    @Value("${second.datasource.validation-query}")
    private String validationQuery;
    @Value("${second.datasource.max-wait}")
    private Integer maxWait;
    @Value("${second.datasource.max-idle}")
    private Integer maxIdle;
    @Value("${second.datasource.min-idle}")
    private Integer minIdle;
    @Value("${second.datasource.max-active}")
    private Integer maxActive;
    @Value("${second.datasource.initial-size}")
    private Integer initialSize;
    @Value("${second.datasource.test-on-borrow}")
    private Boolean testOnBorrow;
    @Value("${second.datasource.test-while-idle}")
    private Boolean testWhileIdle;
    @Value("${second.datasource.remove-abandoned}")
    private Boolean removeAbandoned;
    @Value("${second.datasource.remove-abandoned-timeout}")
    private Integer removeAbandonedTimeout;
    @Value("${second.datasource.time-between-eviction-runs-millis}")
    private Integer timeBetweenEvictionRunsMillis;

    @Bean(name="secondDatasource")
    public DataSource secondDatasource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setMaxWait(maxWait);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setInitialSize(initialSize);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setRemoveAbandoned(removeAbandoned);
        dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        return dataSource;
    }

    @Bean(name="secondSqlSessionFactory")
    public SqlSessionFactory secondSqlSessionFactory(@Named("secondDatasource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setMapperLocations(resolveMapperLocations());
        return sqlSessionFactory.getObject();
    }

    private Resource[] resolveMapperLocations() {
        List<Resource> resources = new ArrayList<Resource>();
        if (this.mapperLocations != null) {
            for (String mapperLocation : this.mapperLocations) {
                Resource[] mappers;
                try {
                    mappers = new PathMatchingResourcePatternResolver().getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    logger.error("mapperLocations has error.", e);
                }
            }
        }
        Resource[] mapperResources = new Resource[resources.size()];
        mapperResources = resources.toArray(mapperResources);
        return mapperResources;
    }

}

