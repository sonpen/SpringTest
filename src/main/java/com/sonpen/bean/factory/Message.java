package com.sonpen.bean.factory;

/**
 * Created by 1109806 on 2019-07-12.
 */
public class Message {

    String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String text) {
        return new Message(text);
    }
}
