package com.kimzing.hook.service;

import com.kimzing.hook.domain.dto.mail.MailDTO;

/**
 * 邮件服务.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 15:02
 */
public interface MailService {

    void sendMail(MailDTO mailDTO);

}
