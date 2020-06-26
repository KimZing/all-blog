package com.kimzing.hook.domain.event;

/**
 * .
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 15:04
 */

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * github hook事件内容.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 14:53
 */
@Data
@Builder
public class GithubHookEvent {

    private String repositoryUrl;

    private String pusherName;

    private String pusherEmail;

    private String commitUrl;

    private String commitDiffUrl;

    private String commitMessage;

    private String commiterName;

    private String commiterEmail;

    private List<String> modifiedFiles;
}