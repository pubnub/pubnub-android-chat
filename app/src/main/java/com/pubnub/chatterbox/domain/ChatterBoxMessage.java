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
public class ChatterBoxMessage {
    private static Gson gson = new GsonBuilder().create();

    private String type = "chattmessage";
    private String ID;
    private String messageContent;
    private String from;
    private Date sentOn;
    private String deviceTag;
    private String emoticon;
    private String senderUUID;


    public static ChatterBoxMessage create() {
        ChatterBoxMessage c = new ChatterBoxMessage();
        return c;
    }

    public static ChatterBoxMessage create(String jsonMessage, String timeToken) throws Exception {
        ChatterBoxMessage message = gson.fromJson(jsonMessage, ChatterBoxMessage.class);
        return message;
    }

    public static String toJSON(ChatterBoxMessage m) {
        return gson.toJson(m);
    }


}
