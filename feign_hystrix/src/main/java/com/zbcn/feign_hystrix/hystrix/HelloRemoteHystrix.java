package com.zbcn.feign_hystrix.hystrix;


import com.zbcn.feign_hystrix.feign.HelloRemote;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 对远程服务调用的熔断操作
 *
 * @author Administrator
 * @date 2018/10/26 11:47
 */
@Component
public class HelloRemoteHystrix implements HelloRemote {
    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return "producer 响应失败。发送参数为："+name;
    }
}
