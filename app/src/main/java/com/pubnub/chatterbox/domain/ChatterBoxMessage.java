package com.pubnub.chatterbox.domain;

import android.graphics.drawable.Drawable;

import org.json.JSONObject;

import java.util.Date;

public class ChatterBoxMessage {

    public static final String DEVICETAG = "deviceTag";

    public static final String FROM = "from";
    public static final String SENTON = "sentOn";
    public static final String EMOTICON = "emoticon";
    public static final String MESSAGECONTENT = "messageContent";

    public static final String MSGID = "id";
    public static final String TYPE = "type";

    public static final String MESSAGE_TYPE_CHATTMESSAGE = "chattbmessage";
    public static final String SENDERUUID = "senderuuid";


    private String type;
    private String ID;
    private String messageContent;
    private String from;
    private Date sentOn;
    private String deviceTag;
    private String emoticon;
    private Drawable avatarImage;
    private String senderUUID;


    private ChatterBoxMessage() {

    }

    public static ChatterBoxMessage create() {

        ChatterBoxMessage c = new ChatterBoxMessage();
        c.setType(MESSAGE_TYPE_CHATTMESSAGE);
        return c;
    }

    public static ChatterBoxMessage create(JSONObject obj, String timeToken) throws Exception {

        ChatterBoxMessage message = new ChatterBoxMessage();
        String deviceTag = obj.getString(DEVICETAG);
        String type = obj.getString(TYPE);
        String from = obj.getString(FROM);
        String emoticon = obj.getString(EMOTICON);
        String messageContent = obj.getString(MESSAGECONTENT);


        message.setDeviceTag(deviceTag);
        message.setType(type);
        message.setFrom(from);
        //TODO convert the timeToken? or add the date to the content?
        message.setSentOn(new Date());
        message.setEmoticon(emoticon);
        message.setMessageContent(messageContent);

        return message;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Date getSentOn() {
        return sentOn;
    }

    public void setSentOn(Date sentOn) {
        this.sentOn = sentOn;
    }

    public String getDeviceTag() {
        return deviceTag;
    }

    public void setDeviceTag(String deviceTag) {
        this.deviceTag = deviceTag;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(String emoticon) {
        this.emoticon = emoticon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public void setSenderUUID(String senderUUID) {
        this.senderUUID = senderUUID;
    }

    public Drawable getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(Drawable avatarImage) {
        this.avatarImage = avatarImage;
    }

}
