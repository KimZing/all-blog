package com.kimzing.hook.listener;

import com.kimzing.hook.domain.dto.mail.MailDTO;
import com.kimzing.hook.domain.event.GithubHookEvent;
import com.kimzing.hook.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 邮件监听.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:19
 */
@Component
public class MailListener {

    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    MailService mailService;

    @Async
    @EventListener
    public void sendMail(GithubHookEvent event) {
        MailDTO mailDTO = new MailDTO();
        mailDTO.setSender(sender);
        mailDTO.setReceiver(event.getPusherEmail());
        mailDTO.setSubject("部署成功:" + event.getCommitMessage() + "  by " + event.getPusherName());
        mailDTO.setContent(event.toString());
        mailService.sendMail(mailDTO);
    }

}
