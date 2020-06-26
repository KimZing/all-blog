package com.kimzing.hook.service;

import com.kimzing.hook.domain.dto.github.GitHubHookDTO;

/**
 * GitHub服务.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:57
 */
public interface GitHubService {
    /**
     * 钩子信息
     * @param gitHubHookDTO
     */
    void hook(GitHubHookDTO gitHubHookDTO);
}
