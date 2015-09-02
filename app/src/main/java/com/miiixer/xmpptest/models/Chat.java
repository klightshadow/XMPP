package com.miiixer.xmpptest.models;

/**
 * Created by lightshadow on 15/1/21.
 */
public class Chat {

    private String name, content;
    private int from; //0 = self, 1 = other
    public Chat(String name, String content, int from) {
        this.name = name;
        this.content = content;
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getFrom() {
        return from;
    }
}
