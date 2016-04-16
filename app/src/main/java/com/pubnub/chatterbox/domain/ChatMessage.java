package com.pubnub.chatterbox.domain;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@ToString
public class ChatMessage {
    private static Gson gson = new GsonBuilder().create();

    private String type = "chattmessage";
    private String ID;
    private String messageContent;
    private String from;
    private Date sentOn;
    private String deviceTag;
    private String emoticon;
    private String senderUUID;


    public static ChatMessage create() {
        ChatMessage c = new ChatMessage();
        return c;
    }

    public static ChatMessage create(String jsonMessage, String timeToken) throws Exception {
        ChatMessage message = gson.fromJson(jsonMessage, ChatMessage.class);
        return message;
    }

    public static String toJSON(ChatMessage m) {
        return gson.toJson(m);
    }


}
