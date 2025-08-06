package cn.ywenrou.shortlink.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.ywenrou.shortlink.system.dao.mapper")
public class ShortLinkSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkSystemApplication.class, args);
    }
}
