package com.pubnub.chatterbox.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ChatterBoxPresenceMessage {

    private static Gson gson = new GsonBuilder().create();

    private String actionType;
    private String timeToken;
    private int occupancyCount;
    private String uuid;


    public static ChatterBoxPresenceMessage create(String json) {
        return gson.fromJson(json, ChatterBoxPresenceMessage.class);
    }

}
