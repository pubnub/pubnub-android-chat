package com.pubnub.chatterbox.domain;

import android.graphics.drawable.Drawable;

import org.json.JSONObject;

import java.util.Date;

public class ChatterBoxMessage {

    public static final String DEVICETAG = "deviceTag";
    public static final String TYPE = "type";
    public static final String FROM = "from";
    public static final String SENTON = "sentOn";
    public static final String EMOTICON = "emoticon";
    public static final String MESSAGECONTENT = "messageContent";
    public static final String MESSAGEID = "msgId";
    public static final String SENDERUUID = "uuid"; //the uuid of the sender

    //Message types
    public static final String MTYPE_PRIVATE_CHAT_REQUEST = "pcr";
    public static final String CHATTMESSAGE = "chattmessage";

    private String type;
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
        c.setType(CHATTMESSAGE);
        return c;
    }

    public static ChatterBoxMessage create(JSONObject obj, String timeToken) throws Exception {
        ChatterBoxMessage message = new ChatterBoxMessage();
        String deviceTag = obj.getString(DEVICETAG);
        String type = obj.getString(TYPE);
        String from = obj.getString(FROM);
        String emoticon = obj.getString(EMOTICON);
        String messageContent = obj.getString(MESSAGECONTENT);
        String uuid = obj.getString(SENDERUUID);

        message.setDeviceTag(deviceTag);
        message.setType(type);
        message.setFrom(from);
        //TODO convert the timeToken? or add the date to the content?
        message.setSentOn(new Date());
        message.setSenderUUID(uuid);
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
