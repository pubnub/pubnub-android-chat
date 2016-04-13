package com.pubnub.chatterbox.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ChatterBoxUserProfile implements Serializable {


    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String imageURL;
    private String location;
    private String id;

}
