package com.kimzing.hook.domain.dto.github;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * .
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:51
 */
@Data
@NoArgsConstructor
public class Repository {
    /**
     * name : blog-post
     * owner : {"name":"KimZing","email":"kimzing@163.com"}
     * html_url : https://github.com/KimZing/blog-post
     */

    private String name;
    private Owner owner;
    private String html_url;
}
