package com.pepper.web.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.pepper.web.helper.MybatisInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.collect.Lists;

@Configuration
@EnableTransactionManagement
@MapperScan("com.pepper.web.dao")
@PropertySource("classpath:config/config.properties")
public class DruidConfig implements EnvironmentAware, TransactionManagementConfigurer {


	@Value("${db.host}")
	private String host;

	@Value("${db.port}")
	private Integer port;

	@Value("${db.database}")
	private String database;

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;


	private Environment environment;

	@Bean(initMethod = "init", destroyMethod = "close")
	@ConditionalOnMissingBean
	public DataSource dataSource(){
		DruidDataSource druidDataSource = new DruidDataSource();
		//基本属性 驱动 url、user、password
		String url = "jdbc:mysql://HOST:PORT/DATABASE?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&failOverReadOnly=false&maxReconnects=10" ;
		url = url.replaceAll("HOST",host).replace("PORT",String.valueOf(port)).replaceAll("DATABASE",database);
		druidDataSource.setUrl(url);
		druidDataSource.setUsername(username);
		druidDataSource.setPassword(password);
		//配置初始化大小、最小、最大
		druidDataSource.setMinIdle(10);                        
		druidDataSource.setMaxActive(200);                     
		druidDataSource.setInitialSize(10);    
		 //配置获取连接等待超时的时间
		druidDataSource.setMaxWait(60000);         
		//配置一个连接在池中最小生存的时间,单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(300000);   
		//配置间隔多久才进行一次检测,检测需要关闭的空闲连接,单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(60000); 
		//默认的testWhileIdle=true,testOnBorrow=false,testOnReturn=false
		druidDataSource.setValidationQuery("SELECT 1");         
		/*
		 * 下面两行设置用于-->打开PSCache,并且指定每个连接上PSCache的大小
		 * PSCache(preparedStatement)对支持游标的数据库性能提升巨大,比如说Oracle/DB2/SQLServer,但MySQL下建议关闭
		 */
		druidDataSource.setPoolPreparedStatements(false);
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(-1);
		druidDataSource.setProxyFilters(Lists.<Filter>newArrayList(statFilter()));
		try{
			druidDataSource.setFilters("wall,mergeStat,stat");
		}catch(SQLException e){
			e.printStackTrace();
		}
		return druidDataSource;
	}

	@Bean
	public FilterRegistrationBean druidFilterRegistrationBean(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("profileEnable", "true");
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}

	@Bean
	public StatFilter statFilter(){
		StatFilter statFilter = new StatFilter();
		statFilter.setLogSlowSql(Boolean.valueOf(environment.getProperty("spring.datasource.logSlowSql", "true")));
		statFilter.setSlowSqlMillis(Long.valueOf(environment.getProperty("spring.datasource.slowSqlMillis", "3000")));
		statFilter.setMergeSql(true);
		return statFilter;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Bean
	@ConditionalOnMissingBean
	public TransactionTemplate transactionTemplate(DataSourceTransactionManager transactionManager){
		return new TransactionTemplate(transactionManager);
	}
	@Bean
	@ConditionalOnMissingBean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public Interceptor getInterceptor(){
		return new MybatisInterceptor();
	}


}