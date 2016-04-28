package com.pubnub.chatterbox.profile;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pubnub.chatterbox.entity.UserProfile;

public abstract class UserProfileBuilderFactory {

    public static UserProfileBuilder getInstance(Object o) {
        if (o instanceof GoogleApiClient) {
            GooglePlusUserProfileBuilder gbuilder = new GooglePlusUserProfileBuilder();
            gbuilder.setGoogleApiClient((GoogleApiClient) o);
            return gbuilder;
        }else{
            return null;
        }

    }



    public abstract UserProfile build();


}
