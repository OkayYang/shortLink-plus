package cn.ywenrou.shortlink.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class ShortLinkAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAgentApplication.class, args);
    }
}
