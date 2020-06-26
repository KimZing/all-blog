package com.kimzing.hook.domain.dto.github;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Github钩子信息.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:49
 */
@Data
@NoArgsConstructor
public class GitHubHookDTO {


    /**
     * ref : refs/heads/master
     * before : df557c3bc8189e2f845e52b886259e63194d194b
     * after : 83f457acd5a4961dc0a0f477190f5438a865510c
     * repository : {"name":"blog-post","owner":{"name":"KimZing","email":"kimzing@163.com"},"html_url":"https://github.com/KimZing/blog-post"}
     * pusher : {"name":"KimZing","email":"kimzing@163.com"}
     * sender : {"login":"KimZing"}
     * compare : https://github.com/KimZing/blog-post/compare/df557c3bc818...83f457acd5a4
     * commits : [{"message":"测试钩子","timestamp":"2020-01-03T16:34:24+08:00","url":"https://github.com/KimZing/blog-post/commit/83f457acd5a4961df477190f5438a865510c","committer":{"name":"zhiqiangjin","email":"zhiqiangjin@pateo.com.cn"},"modified":["about/index.md"]}]
     */

    private String ref;
    private String before;
    private String after;
    private Repository repository;
    private Pusher pusher;
    private Sender sender;
    private String compare;
    private List<Commits> commits;
}
