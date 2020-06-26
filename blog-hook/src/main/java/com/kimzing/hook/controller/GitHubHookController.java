package com.kimzing.hook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimzing.hook.domain.dto.github.GitHubHookDTO;
import com.kimzing.hook.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * GitHub钩子触发控制.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 14:58
 */
@Slf4j
@RestController
@RequestMapping("/github")
public class GitHubHookController {

    @Resource
    GitHubService gitHubService;

    @PostMapping
    public void hookByGitHub(@RequestBody GitHubHookDTO gitHubHookDTO) {
        ObjectMapper mapper = new ObjectMapper();
        log.info("github hook info: [{}]", gitHubHookDTO);
        gitHubService.hook(gitHubHookDTO);
    }

}
