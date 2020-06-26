package com.kimzing.hook.domain.dto.github;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * .
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:51
 */
@Data
@NoArgsConstructor
public class Commits {
    /**
     * message : 测试钩子
     * timestamp : 2020-01-03T16:34:24+08:00
     * url : https://github.com/KimZing/blog-post/commit/83f457acd5a4961df477190f5438a865510c
     * committer : {"name":"zhiqiangjin","email":"zhiqiangjin@pateo.com.cn"}
     * modified : ["about/index.md"]
     */

    private String message;
    private String timestamp;
    private String url;
    private Committer committer;
    private List<String> modified;
}
