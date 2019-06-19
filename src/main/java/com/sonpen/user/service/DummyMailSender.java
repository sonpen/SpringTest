package com.sonpen.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by 1109806 on 2019-06-18.
 */
public class DummyMailSender implements MailSender{
    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        System.out.println("======> send");
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
        System.out.println("======> send");
    }
}
