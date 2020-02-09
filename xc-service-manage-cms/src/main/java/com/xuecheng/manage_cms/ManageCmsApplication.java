package com.xuecheng.manage_cms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms") // 扫描实体类
@ComponentScan("com.xuecheng.api.cms") //扫描接口
@ComponentScan("com.xuecheng.api.config") // 扫描Swagger2
@ComponentScan(basePackages = "com.xuecheng.manage_cms")//扫描本项目下所有的类
public class ManageCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }

}
