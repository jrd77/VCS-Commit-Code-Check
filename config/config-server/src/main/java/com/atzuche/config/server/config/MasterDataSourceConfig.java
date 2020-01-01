package com.atzuche.config.server.config;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/31 2:53 下午
 **/
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@MapperScan(basePackages={"com.atzuche.config.server.mapper.master"}, sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterDataSourceConfig implements TransactionManagementConfigurer {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Value("${master.datasource.driverClassName}")
    private String driverClassName;
    @Value("${master.datasource.url}")
    private String url;
    @Value("${master.datasource.username}")
    private String username;
    @Value("${master.datasource.password}")
    private String password;
    @Value("${master.mybatis.mapper-locations}")
    private String[] mapperLocations;
    @Value("${master.datasource.validation-query}")
    private String validationQuery;
    @Value("${master.datasource.max-wait}")
    private Integer maxWait;
    @Value("${master.datasource.max-idle}")
    private Integer maxIdle;
    @Value("${master.datasource.min-idle}")
    private Integer minIdle;
    @Value("${master.datasource.max-active}")
    private Integer maxActive;
    @Value("${master.datasource.initial-size}")
    private Integer initialSize;
    @Value("${master.datasource.test-on-borrow}")
    private Boolean testOnBorrow;
    @Value("${master.datasource.test-while-idle}")
    private Boolean testWhileIdle;
    @Value("${master.datasource.remove-abandoned}")
    private Boolean removeAbandoned;
    @Value("${master.datasource.remove-abandoned-timeout}")
    private Integer removeAbandonedTimeout;
    @Value("${master.datasource.time-between-eviction-runs-millis}")
    private Integer timeBetweenEvictionRunsMillis;

    @Autowired
    @Qualifier("masterDatasource")
    private DataSource masterDatasource;

    @Primary
    @Bean(name="masterDatasource")
    public DataSource masterDatasource() {
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

    @Primary
    @Bean(name="masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDatasource") DataSource dataSource) throws Exception {
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

    @Override
    @Bean(name="masterDataSourceTransactionManager")
    @Primary
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(masterDatasource);
    }

}

