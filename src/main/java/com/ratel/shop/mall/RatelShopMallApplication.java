
package com.ratel.shop.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ratel.shop.mall.mapper")
@SpringBootApplication
public class RatelShopMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(RatelShopMallApplication.class, args);
    }
}
