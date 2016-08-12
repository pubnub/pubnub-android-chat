package com.pubnub.chatterbox.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatusEvent {

    private boolean error;
    private String message;
    private String type;
}
