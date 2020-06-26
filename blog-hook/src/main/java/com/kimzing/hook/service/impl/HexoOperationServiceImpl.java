package com.kimzing.hook.service.impl;

import com.kimzing.hook.config.CommandProperties;
import com.kimzing.hook.service.HexoOperationService;
import com.kimzing.hook.utils.shell.ShellUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 博客构建操作.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:54
 */
@Service
public class HexoOperationServiceImpl implements HexoOperationService {

    @Resource
    CommandProperties commandProperties;

    @Override
    public void update() {
        String command = commandProperties.getHexo().stream().collect(Collectors.joining(" && "));
        ShellUtil.exec(command);
    }
}
