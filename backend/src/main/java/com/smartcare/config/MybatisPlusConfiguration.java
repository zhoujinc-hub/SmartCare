package com.smartcare.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.smartcare.web.admin.mapper")
public class MybatisPlusConfiguration {
}
