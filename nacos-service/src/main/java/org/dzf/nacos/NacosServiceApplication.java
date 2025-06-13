package org.dzf.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Nacos服务注册与配置中心应用启动类
 * 
 * @author dzf
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosServiceApplication.class, args);
        System.out.println("\n==================================");
        System.out.println("  Nacos Service 启动成功!");
        System.out.println("  服务注册与配置中心已就绪");
        System.out.println("==================================");
    }
}