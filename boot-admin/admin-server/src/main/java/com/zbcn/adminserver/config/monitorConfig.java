package com.zbcn.adminserver.config;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.BasicAuthHttpHeaderProvider;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Configuration
public class monitorConfig {

	private static final Logger log = LoggerFactory.getLogger(monitorConfig.class);
	/**
	 * 自定义安全认证
	 * springboot自动装配默认实现类，由于需要对配置密码进行解码操作，故手动装配
	 *
	 * @return
	 */
	@Bean
	public BasicAuthHttpHeaderProvider basicAuthHttpHeadersProvider() {
		return new BasicAuthHttpHeaderProvider() {
			@Override
			public HttpHeaders getHeaders(Instance instance) {
				HttpHeaders headers = new HttpHeaders();
				//获取用户名，密码
				String username = instance.getRegistration().getMetadata().get("user.name");
				String password = instance.getRegistration().getMetadata().get("user.password");
				String type = instance.getRegistration().getMetadata().get("user.type");

				//若是token有值，那么使用token认知
				if ("token".equalsIgnoreCase(type)) {
					headers.set("X-Token",password);
				} else if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
					headers.set(HttpHeaders.AUTHORIZATION, encode(username, password));
				}
				return headers;
			}

			protected String encode(String username, String password) {
				String token = Base64Utils.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
				return "Basic " + token;
			}
		};
	}

	/**
	 * 自定义Http请求头
	 * 如果需要将自定义HTTP标头注入到受监控应用程序的执行器端点的请求中，您可以轻松添加HttpHeadersProvider
	 * @return
	 */
	@Bean
	public HttpHeadersProvider customHttpHeadersProvider() {
		return instance -> {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("X-CUSTOM", "My Custom Value");
			return httpHeaders;
		};

	}

	/**
	 * 自定义拦截器
	 * monitor Server向客户端发送请求时，会进入InstanceExchangeFilterFunction中，但是对于查询请求（即：actuator的端点请求），
	 * 一般的请求方式是Get。我们可以在这里加入一些审计或者安全控制。
	 *
	 * @return
	 */
	@Bean
	public InstanceExchangeFilterFunction auditLog() {
		return (instance, request, next) -> next.exchange(request).doOnSubscribe(s -> {
			if (HttpMethod.DELETE.equals(request.method()) || HttpMethod.POST.equals(request.method())) {
				log.info("{} for {} on {}", request.method(), instance.getId(), request.url());
			}
		});
	}
}
