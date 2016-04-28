package com.pubnub.chatterbox.domain;

import com.google.gson.Gson;
import com.pubnub.chatterbox.ui.SessionMediator;

import javax.inject.Singleton;

import dagger.Component;



@Component(modules = {ChatterBoxModule.class})
@Singleton
public interface ChatterBox {

    Gson gson();

    SessionMediator sessionMediator();

}
