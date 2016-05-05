package com.pubnub.chatterbox.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class PresenceMessage {

    private static Gson gson = new GsonBuilder().create();

    private String actionType;
    private Long timeToken;
    private int occupancyCount;
    private String uuid;


    public static PresenceMessage create(String json) {
        return gson.fromJson(json, PresenceMessage.class);
    }

}
