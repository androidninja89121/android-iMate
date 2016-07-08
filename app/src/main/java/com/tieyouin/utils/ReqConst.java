package com.tieyouin.utils;

/**
 * Created by Morning on 11/25/2015.
 */
public class ReqConst {

    public static final String SERVER_URL = "http://api.tieyouin.com/";

    public static final String REQ_REGISTER = "api/Account/RegisterAsync";
    public static final String REQ_FB_LOGIN = "api/account/ObtainLocalAccessToken";
    public static final String REQ_TOKEN = "token"; // login & refresh token
    public static final String REQ_UPLOAD_IMAGE = "api/image/Upload";

    /*
    *   Request keyword for register, login
     */
    public static final String USERNAME = "UserName";
    public static final String USERNAME2 = "userName";
    public static final String PASSWORD = "Password";
    public static final String CONFIRM_PASSWORD = "ConfirmPassword";
    public static final String EMAIL = "Email";
    public static final String PHONE_NUMBER = "PhoneNumber";
    public static final String ADDRESS = "address";
    public static final String BIRTHDAY = "DateOfBirth";
    public static final String SEX = "GenderId";
    public static final String USER_LAT = "Latitude";
    public static final String USER_LON = "Longitude";
    public static final String USER_LANGID = "LanguageId";
    public static final String USER_MACIP = "UserMacIp";
    public static final String DEVICE_ID = "DeviceId";
    public static final String Client_ID = "ClientId";
    public static final String USER_PROVIDER = "provider";
    public static final String USER_EXTERNAL_ACCESS_TOKEN = "ExternalAccessToken";

    public static final String ID = "id";
    public static final String USER_ID = "userId";
    public static final String CLIENT_ID = "client_id";
    public static final String AS_CLIENTT_ID = "as:client_id";
    public static final String NAME = "name";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESSH_TOKEN = "refresh_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String ISSUED = ".issued";
    public static final String EXPIRES = ".expires";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT = "password";
    public static final String DEF_CLIENT_ID = "ngAuthApp";

    public static final String IS_PROFILE_PIC = "IsProfilePic";

    public static final String AUTHORIZATION = "Authorization";
}
