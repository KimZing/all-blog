package com.kimzing.hook.service.impl;

import com.kimzing.hook.domain.dto.mail.MailDTO;
import com.kimzing.hook.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 邮件服务实现.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 16:22
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Resource
    JavaMailSender mailSender;

    @Override
    public void sendMail(MailDTO mailDTO) {
        log.info("发送邮件:[{}]", mailDTO);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailDTO.getSender());
        message.setTo(mailDTO.getReceiver());
        message.setSubject(mailDTO.getSubject());
        message.setText(mailDTO.getContent());
        message.setSentDate(new Date());
        this.mailSender.send(message);
    }

}
