package com.zbcn.eureka_consumer.controller;

import com.zbcn.eureka_consumer.feign.HelloRemote;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 消费者controller
 *
 * @author Administrator
 * @date 2018/10/25 22:19
 */
@RestController
public class ConsumerController  {

    @Resource
    private HelloRemote helloRemote;

    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        return helloRemote.hello(name);
    }
}
