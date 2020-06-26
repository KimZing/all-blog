package com.kimzing.hook.service.impl;

import com.kimzing.hook.domain.dto.github.GitHubHookDTO;
import com.kimzing.hook.domain.event.GithubHookEvent;
import com.kimzing.hook.service.GitHubService;
import com.kimzing.hook.service.HexoOperationService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * GitHub钩子信息处理.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:58
 */
@Service
public class GitHubServiceImpl implements GitHubService {

    @Resource
    ApplicationContext applicationContext;

    @Resource
    HexoOperationService hexoOperationService;

    @Override
    public void hook(GitHubHookDTO gitHubHookDTO) {
        // 更新博客
        hexoOperationService.update();

        GithubHookEvent event = createHookEvent(gitHubHookDTO);
        // 发送邮件
        applicationContext.publishEvent(event);
    }

    private GithubHookEvent createHookEvent(GitHubHookDTO gitHubHookDTO) {
        GithubHookEvent.GithubHookEventBuilder builder = GithubHookEvent.builder()
                .repositoryUrl(gitHubHookDTO.getRepository().getHtml_url())
                .commitDiffUrl(gitHubHookDTO.getCompare())
                .pusherName(gitHubHookDTO.getPusher().getName())
                .pusherEmail(gitHubHookDTO.getPusher().getEmail());

        if (gitHubHookDTO.getCommits() == null || gitHubHookDTO.getCommits().size() == 0) {
            return builder.build();
        }

        return builder.commiterName(gitHubHookDTO.getCommits().get(0).getCommitter().getName())
                .commiterEmail(gitHubHookDTO.getCommits().get(0).getCommitter().getEmail())
                .commitMessage(gitHubHookDTO.getCommits().get(0).getMessage())
                .commitUrl(gitHubHookDTO.getCommits().get(0).getUrl())
                .modifiedFiles(gitHubHookDTO.getCommits().get(0).getModified())
                .build();
    }
}
