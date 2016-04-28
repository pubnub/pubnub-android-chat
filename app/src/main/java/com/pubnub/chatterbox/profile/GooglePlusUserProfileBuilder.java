package com.pubnub.chatterbox.profile;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.pubnub.chatterbox.entity.UserProfile;

public class GooglePlusUserProfileBuilder extends UserProfileBuilder {

    private GoogleApiClient googleApiClient;

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public UserProfile build() {

        UserProfile newUserProfile = new UserProfile();
        String email = Plus.AccountApi.getAccountName(googleApiClient);
        newUserProfile.setEmail(email);


        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);

            String userName = currentPerson.getName().getGivenName().toLowerCase()
                    + currentPerson.getName().getFamilyName().toLowerCase();

            newUserProfile.setUserName(userName);
            newUserProfile.setFirstName(currentPerson.getName().getGivenName());
            newUserProfile.setLastName(currentPerson.getName().getFamilyName());
            newUserProfile.setImageURL(currentPerson.getImage().getUrl());
        }


        return newUserProfile;
    }
}
