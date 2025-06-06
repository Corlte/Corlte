package com.lzyp.common.mob;

/**
 * Created by cxf on 2018/9/21.
 */

public class LoginData {

    private String mType;
    private String mOpenID;
    private String mAccessToken;
    private String mNickName;
    private String mAvatar;

    public LoginData() {

    }

    public LoginData(String type, String openID,String accessToken, String nickName, String avatar) {
        mType = type;
        mOpenID = openID;
        mAccessToken = accessToken;
        mNickName = nickName;
        mAvatar = avatar;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getOpenID() {
        return mOpenID;
    }

    public void setOpenID(String openID) {
        mOpenID = openID;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }


    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
