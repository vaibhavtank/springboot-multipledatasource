package com.vaibhav.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(
        entityManagerFactoryRef = "studentEntityMgrFactory",
        transactionManagerRef = "studentTransactionMgr",
        basePackages = {
                "com.vaibhav.demo.student.repository"
        })
@EnableTransactionManagement
public class DB2Configuration {

    @Autowired
    Environment env;

    @Bean(name = "datasource2")
    @ConfigurationProperties(prefix = "spring.db2.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "studentEntityMgrFactory")
    public LocalContainerEntityManagerFactoryBean studentEntityMgrFactory(
            final EntityManagerFactoryBuilder builder,
            @Qualifier("datasource2") final DataSource dataSource) {
        final Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.vaibhav.demo.student.entity")
                .persistenceUnit("student")
                .build();
    }

    @Bean(name = "studentTransactionMgr")
    public PlatformTransactionManager studentTransactionMgr(
            @Qualifier("studentEntityMgrFactory") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

