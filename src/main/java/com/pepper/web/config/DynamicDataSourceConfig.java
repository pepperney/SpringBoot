package com.pepper.web.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.web.datasource.DataSourceType;
import com.pepper.web.datasource.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan(basePackages="com.pepper.web.dao", sqlSessionFactoryRef="sessionFactory")
public class DynamicDataSourceConfig {

    @Autowired
    Environment environment;

    @Bean(name = "writeDataSource")
    public DataSource writeDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        //TODO
        return dataSource;
    }

    @Bean(name = "readDataSource")
    public DataSource readDataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        //TODO
        return dataSource;
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource createDynamicDataSource(@Qualifier("writeDataSource")DataSource writeDataSource,
                                                     @Qualifier("readDataSource")DataSource readDataSource){
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(DataSourceType.WRITE.getValue());
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.WRITE.getValue(),writeDataSource);
        targetDataSources.put(DataSourceType.READ.getValue(),readDataSource);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dataSource)throws Exception{
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setMapperLocations(resolver.getResources(environment.getProperty("mybatis.mapperLocations")));    //*Mapper.xml位置
        return sessionFactoryBean.getObject();
    }
}
