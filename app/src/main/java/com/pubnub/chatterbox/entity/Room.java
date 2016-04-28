package com.pubnub.chatterbox.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Room {

    private String name;
    private String title;
    private String roomID;
    private boolean active;
}
