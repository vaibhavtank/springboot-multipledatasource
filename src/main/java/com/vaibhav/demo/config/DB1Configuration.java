package com.vaibhav.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(
        entityManagerFactoryRef = "departmentMgrFactory",
        transactionManagerRef = "departmentTransactionMgr",
        basePackages = {
                "com.vaibhav.demo.department.repository"
        })
@EnableTransactionManagement
public class DB1Configuration {

    @Autowired
    Environment env;

    @Bean(name = "logdatasource")
    @ConfigurationProperties(prefix = "spring.log.datasource")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "departmentMgrFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean departmentMgrFactory(
            final EntityManagerFactoryBuilder builder,
            @Qualifier("logdatasource") final DataSource dataSource) {
        // dynamically setting up the hibernate properties for each of the datasource.
        return builder
                .dataSource(dataSource)
                .packages("com.vaibhav.demo.department.entity")
                .persistenceUnit("department")
                .build();
    }

    @Bean(name = "departmentTransactionMgr")
    @Primary
    public PlatformTransactionManager departmentTransactionMgr(
            @Qualifier("departmentMgrFactory") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
