package com.example.translateconnector.utils;

/**
 * Created by tontn on 3/24/18.
 */

public class Constants {
    //Key LocalSharedPreference
    public static final String KEY_INTRO_SHOWED = "key_intro_showed";
    public static final String IS_USER_LOGGED_IN = "is_user_logged_in";
    public static final String KEY_USER_INFO_DATA = "key_user_info_data";
    public static final String KEY_USER_INFO_CACHED = "KEY_USER_INFO_CACHED";
    public static final String KEY_ORDER_CACHED = "KEY_ORDER_CACHED";
    public static final String KEY_ACCESS_TOKEN = "key_access_token";
    public static final String KEY_FIREBASE_USER = "key_firebase_user";
    public static final String KEY_FIREBASE_USER_INFO = "KEY_FIREBASE_USER_INFO";
    public static final String KEY_CHATTING = "key_chatting";
    public static final String KEY_NEAR_BY_SETTING = "KEY_NEAR_BY_SETTING";

    public static final String INTRODUCING_PREFERENCES = "INTRODUCING_PREFERENCES";

    //keys to transfer data between screens
    public static final String KEY_INTRO_DATA = "key_intro_data";

    public static final int PHONE_NUMBER_MAX_LENGTH = 50;
    public static final String PHONE_PATTERN = "[0-9]+";

    public static final String FIREBASE_DEFAULT_PASS = "111111";

    public static final String IMAGE_CAMERA_AUTHORITY = "com.imoktranslator" + ".fileprovider";

    public static int PASSWORD_MIN_LENGTH = 6;

    //Address
    public static final int ADDRESS_TYPE_FILTER = 1;
    public static final int ADDRESS_TYPE_MAP = 0;

    //Sort
    public static final String SORT_DESC = "desc";
    public static final String SORT_ASC = "asc";
    public static final int MAX_CERTIFICATE_IMAGES = 20;

    //Gender
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int ANY = 3;

    //default Lat, lng at HaNoi, VN
    public static final double DEFAULT_LAT = 20.9737724;
    public static final double DEFAULT_LNG = 105.8210913;

    public static final String SEARCH_CONTACTS = "search_contacts";
    public static final String SEARCH_USER_SUGGESTIONS = "search_user_suggestions";

    public static final String ROOM_KEY = "ROOM_KEY";
    public static final String USER_ROOM_KEY = "USER_ROOM_KEY";
    public static final String USER_KEY = "USER_KEY";
    public static final String IS_ROOM_REMOVED = "IS_ROOM_REMOVED";
    public static final String POST_KEY = "POST_KEY";
    public static final String POST_ID_KEY = "POST_ID_KEY";
    public static final String FILE_MODELS_KEY = "FILE_MODELS_KEY";
    public static final String SELECTED_POSITION_KEY = "SELECTED_POSITION_KEY";
    public static final String USER_ID = "USER_ID";
    public static final String IS_FRIEND = "IS_FRIEND";
    public static final String SEARCH_FRIEND = "SEARCH_FRIEND";
    public static final String SETTING_LANGUAGE = "SETTING_LANGUAGE";

    //post mode
    public static final String PUBLIC_MODE = "public";
    public static final String FRIENDS_MODE = "friends";
    public static final String PRIVATE_MODE = "private";

    public static final String QR_CODE_SEPARATE = ",";

    //seconds
    public static final int MAX_VIDEO_DURATION = 30;
    //milliseconds
    public static final int MAX_AUDIO_DURATION = 60000;

    public static final String MESS_1054 = "đã rời khỏi nhóm";
    public static final String MESS_1055 = "đã bị mời khỏi nhóm";
    public static final String MESS_1056 = "đã tạo nhóm chat";
    public static final String MESS_1059 = "đã được thêm vào nhóm chat";
    public static final String MESS_1071 = "đã đổi tên nhóm chat thành";

    //Account Status
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_LOCKED = 2;
}
