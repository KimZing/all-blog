package com.kimzing.hook.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 命令配置.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 18:30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "command")
public class CommandProperties {

    private List<String> hexo;

}
