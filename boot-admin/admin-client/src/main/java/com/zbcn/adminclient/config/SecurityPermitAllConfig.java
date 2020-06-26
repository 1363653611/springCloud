package com.zbcn.adminclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
/**
 *  @title SecurityPermitAllConfig
 *  @Description 允许所有的服务可以访问
 *  @author zbcn8
 *  @Date 2020/1/9 18:11
 */
//@Configuration
public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().permitAll()
				.and().csrf().disable();
	}
}
