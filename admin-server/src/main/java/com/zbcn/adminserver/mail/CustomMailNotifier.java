package com.zbcn.adminserver.mail;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  @title CustomMailNotifier
 *  @Description 自定义通知
 *  若是采用SpringBoot Admin自带的邮件通知，那么不能按照业务进行分组通知，需要我们关闭自带的邮件通知，手动进行通知。
 *  @author zbcn8
 *  @Date 2020/1/10 16:52
 */
@Component
public class CustomMailNotifier extends AbstractStatusChangeNotifier {

	private Logger log = LoggerFactory.getLogger(CustomMailNotifier.class);

	//自定义邮件发送类
	@Resource
	private SendEmailUtils sendEmailUtils;

	//自定义邮件模板
	private final static String email_template="updatePsw.ftl";

	private static Map<String,String> instance_name=new HashMap<>();

	@Value("${spring.mail.to}")
	private String toMail;

	@Value("${spring.mail.cc}")
	private String[] ccMail;

	static {
		instance_name.put("DOWN","服务心跳异常通知");
		instance_name.put("OFFLINE","服务下线报警通知");
		instance_name.put("UP","服务恢复通知");
		instance_name.put("UNKNOWN","服务未知异常");
	}

	public CustomMailNotifier(InstanceRepository repository) {
		super(repository);
	}

	@Override
	protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
		return Mono.fromRunnable(() -> {

			if (event instanceof InstanceStatusChangedEvent) {
				String serviceUrl = instance.getRegistration().getServiceUrl();
				log.info("【邮件通知】-【Instance {} ({}) is {},The IP is {}】", instance.getRegistration().getName(), event.getInstance(),
						((InstanceStatusChangedEvent) event).getStatusInfo().getStatus(), serviceUrl);
				//获取服务地址
				String status = ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus();
				Map<String, Object> model = new HashMap<>();
				model.put("ipAddress", instance.getRegistration().getServiceUrl());
				model.put("instanceName", instance.getRegistration().getName());
				model.put("instanceId", instance.getId());
				model.put("startup", null);
				//邮件接收者，可根据instanceName灵活配置
				switch (status) {
					// 健康检查没通过
					case "DOWN":
						log.error(instance.getRegistration().getServiceUrl() + "服务心跳异常。");
						model.put("status", "服务心跳异常");
						Map<String, Object> details = instance.getStatusInfo().getDetails();
						//遍历Map，查找down掉的服务
						Map<String ,String> errorMap=new HashMap<>();
						StringBuffer sb = new StringBuffer();
						for (Map.Entry<String, Object> entry : details.entrySet()) {
							try {
								LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) entry.getValue();
								//服务状态
								String serviceStatus = (String) value.get("status");
								//如果不是成功状态
								if (!"UP".equalsIgnoreCase(serviceStatus)) {
									//异常细节
									LinkedHashMap<String, Object> exceptionDetails = (LinkedHashMap<String, Object>) value.get("details");
									String error = (String) exceptionDetails.get("error");
									sb.append("节点：").append(entry.getKey()).append("<br>");
									sb.append("状态：").append(serviceStatus).append("<br>");
									sb.append(" 异常原因： ").append(error).append("<br>");
								}
							} catch (Exception e) {
								//异常时，不应该抛出，而是继续打印异常
								log.error("【获取-服务心跳异常邮件信息异常】", e);
							}
						}
						//节点详细状态
						model.put("details", sb.toString());
						try {
							//发送短信
							sendEmailUtils.sendMail(model, instance_name.get("DOWN"),email_template, toMail, ccMail);
						} catch (Exception e) {
							log.error("【邮件发送超时...】", e);
						}
						break;
					// 服务离线
					case "OFFLINE":
						log.error(instance.getRegistration().getServiceUrl() + " 发送 服务离线 的通知！");
						try {
							model.put("status", "服务下线");
							model.put("message", ((InstanceStatusChangedEvent) event).getStatusInfo().getDetails().get("message"));
							sendEmailUtils.sendMail(model, instance_name.get("OFFLINE"),email_template, toMail, ccMail);
						} catch (Exception e) {
							log.error("【邮件发送超时...】", e);
						}
						break;
					//服务上线
					case "UP":
						log.info(instance.getRegistration().getServiceUrl() + "服务恢复");
						//启动时间
						String startup = instance.getRegistration().getMetadata().get("startup");
						model.put("status", "服务恢复");
						model.put("startup", startup);
						try {
							sendEmailUtils.sendMail(model, instance_name.get("UP"),email_template, toMail, ccMail);
						} catch (Exception e) {
							log.error("【邮件发送超时...】", e);
						}
						break;
					// 服务未知异常
					case "UNKNOWN":
						log.error(instance.getRegistration().getServiceUrl() + "发送 服务未知异常 的通知！");
						break;
					default:
						break;
				}

			} else {
				log.info("Instance {} ({}) {}", instance.getRegistration().getName(), event.getInstance(),
						event.getType());
			}
		});
	}
}
