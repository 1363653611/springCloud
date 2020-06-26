package com.zbcn.adminclient.config;

import com.zbcn.adminclient.common.MyPasswordEncoder;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * 自定义授权规则，只对端点进行安全访问
	 *
	 * @param http
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
				.anyRequest().authenticated()
				.and().httpBasic()
				.and().csrf();

		//同上
//        http.authorizeRequests()
//                .antMatchers("/actuator/**").authenticated()  //该url需要认证
//                .antMatchers("/**").permitAll().and().httpBasic();
//        ;
		//另外一种方式
//		http.authorizeRequests()
//				//拦截所有endpoint，拥有ACTUATOR_ADMIN角色可访问，否则需登录
//				.requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR_ADMIN")
//				//静态文件允许访问
//				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//				//根路径允许访问
//				.antMatchers("/").permitAll()
//				//所有请求路径可以访问
//				.antMatchers("/**").permitAll()
//				.and().httpBasic();
	}
	/**
	 * 配置用户名密码的加密方式
	 *
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(new MyPasswordEncoder()).withUser("user")
				.password(new MyPasswordEncoder().encode("123456")).roles("ADMIN");
	}
}
