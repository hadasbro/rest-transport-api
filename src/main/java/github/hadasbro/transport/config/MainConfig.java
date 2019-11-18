package github.hadasbro.transport.config;

import de.sandkastenliga.tools.projector.core.Projector;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/*
    Config contains 2 separate DB connections (here we use only 1 and the
    same DB but this config is designed to use 2 different databases)
 */
@Configuration
@SuppressWarnings("unused")
//@ImportResource("classpath:spring-config.xml")
public class MainConfig {

    private Properties jpaProperties = new Properties();

    private Environment env;

    private JpaVendorAdapter jpaVendorAdapter;

    public MainConfig() {

        jpaProperties.put("hibernate.dialect", MySQL5Dialect.class.getName());
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.hbm2ddl.auto", "none"); // create update none

    }

    @Primary
    @Bean(name = "dataSource1")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource firstDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory(
            @Qualifier("dataSource1") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapter);
        emf.setPackagesToScan("github.hadasbro.transport");
        emf.setPersistenceUnitName("default");
        emf.setJpaProperties(jpaProperties);
        emf.setPersistenceProvider(new HibernatePersistenceProvider());
        emf.afterPropertiesSet();

        return emf.getObject();

    }

    @Primary
    @Bean(name = "entityManager")
    public EntityManager entityManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean(name = "dataSource2")
    @ConfigurationProperties(prefix="db2.datasource")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "entityManagerBb2")
    public EntityManager entityManagerBb2(
            @Qualifier("EntityManagerFactoryBb2") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean(name = "EntityManagerFactoryBb2")
    public EntityManagerFactory entityManagerFactorydb2(@Qualifier("dataSource2") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(jpaVendorAdapter);

        emf.setPackagesToScan("github.hadasbro.transport");
        emf.setPersistenceUnitName("persistenceUnitdb2");
        emf.setJpaProperties(jpaProperties);
        emf.setPersistenceProvider(new HibernatePersistenceProvider());
        emf.afterPropertiesSet();
        return emf.getObject();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Bean(name = "transactionManagerBb2")
    public PlatformTransactionManager transactionManagerBb2(
            @Qualifier("EntityManagerFactoryBb2") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    public Environment getEnv() {
        return env;
    }

    public void setJpaVendorAdapter(JpaVendorAdapter jpaVendorAdapter) {
        this.jpaVendorAdapter = jpaVendorAdapter;
    }

    @Bean
    public Projector projector(){
        return new Projector();
    }

}
