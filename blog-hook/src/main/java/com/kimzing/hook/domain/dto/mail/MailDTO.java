package com.kimzing.hook.domain.dto.mail;

import lombok.Data;

/**
 * 邮件信息实体.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 14:53
 */
@Data
public class MailDTO {
    /**
     * 发送者
     */
    private String sender;
    /**
     * 接收者
     */
    private String receiver;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String content;
}
