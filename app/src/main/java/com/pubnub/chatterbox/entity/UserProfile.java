package com.pubnub.chatterbox.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfile implements Serializable {

    private static Gson gson = new GsonBuilder().create();


    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String imageURL;
    private String location;
    private String id;


    public static UserProfile create(String jsonMessage, String timeToken) throws Exception {
        return gson.fromJson(jsonMessage, UserProfile.class);
    }

    public static String toJSON(UserProfile p) {

        return gson.toJson(p);
    }

}
