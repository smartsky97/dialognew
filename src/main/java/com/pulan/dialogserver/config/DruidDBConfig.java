package com.pulan.dialogserver.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;


/**
 * dataSource
 */
@Configuration
public class DruidDBConfig {

    @Autowired
    private Environment env;

    @Bean(name = "oraclesDataSource")
    @Qualifier("oraclesDataSource")
    public DataSource oraclesDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.oracles.driver-class-name"))
                .url(env.getProperty("spring.datasource.oracles.url"))
                .username(env.getProperty("spring.datasource.oracles.username"))
                .password(env.getProperty("spring.datasource.oracles.password"))
                .build();
    }

    @Bean(name = "mysqlDataSource")
    @Qualifier("mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.secondary.driver-class-name"))
                .url(env.getProperty("spring.datasource.secondary.url"))
                .username(env.getProperty("spring.datasource.secondary.username"))
                .password(env.getProperty("spring.datasource.secondary.password"))
                .build();
    }


    @Bean(name = "mysqlDataSource_78")
    @Qualifier("mysqlDataSource_78")
    public DataSource mysqlDataSource_78() {
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.secondary2.driver-class-name"))
                .url(env.getProperty("spring.datasource.secondary2.url"))
                .username(env.getProperty("spring.datasource.secondary2.username"))
                .password(env.getProperty("spring.datasource.secondary2.password"))
                .build();
    }

//
//
//    @Bean(name = "sqlserverDataSource")
//    @Qualifier("sqlserverDataSource")
//    public DataSource sqlserverDataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName(env.getProperty("spring.datasource.sqlserver.driver-class-name"))
//                .url(env.getProperty("spring.datasource.sqlserver.url"))
//                .username(env.getProperty("spring.datasource.sqlserver.username"))
//                .password(env.getProperty("spring.datasource.sqlserver.password"))
//                .build();
//    }


    @Bean(name = "oraclesJdbcTemplate")
    public JdbcTemplate oraclesJdbcTemplate(
            @Qualifier("oraclesDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "mysqlJdbcTemplate")
    public JdbcTemplate mysqlJdbcTemplate(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "mysqlJdbcTemplate_78")
    public JdbcTemplate mysqlJdbcTemplate_78(
            @Qualifier("mysqlDataSource_78") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

//    @Bean(name = "sqlserverJdbcTemplate")
//    public JdbcTemplate sqlserverJdbcTemplate(
//            @Qualifier("sqlserverDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
}
