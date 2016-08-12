package com.pubnub.chatterbox.entity;


import com.pubnub.chatterbox.service.client.ChatServiceClient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@ToString
@Builder
public class Room {

    private String name;
    private String title;
    private String roomID;
    private boolean active;
    private ChatServiceClient chatServiceClient;


    public Room(){

    }


}
