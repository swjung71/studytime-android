package kr.co.digitalanchor.utils;

/**
 * 인텐트 전달시 사용되는 데이터 집합.
 * <pre><i>
 * Created by rule at 'Mac, AndroidStudio'
 * Copyright: (C) 2012-2016 Rinasoft.
 * </i></pre>
 *
 * @author rule
 * @since 2016. 10. 11.
 */
public class Intents {
    public static final String ACTION_USAGE_SWITCH = "kr.co.digitalanchor.studytime.action.USAGE_SWITCH";
    public static final String EXTRA_SWITCH = "kr.co.digitalanchor.studytime.extra.SWITCH";

    public static final String ACTION_USAGE_CHANGED = "kr.co.digitalanchor.studytime.action.USAGE_CHANGED";
    public static final String EXTRA_PACKAGE = "kr.co.digitalanchor.studytime.extra.PACKAGE";

    public static final String ACTION_ALERT_ALARM = "kr.co.digitalanchor.studytime.action.ALERT_ALARM";
    public static final String ACTION_ATTACH_ALARM = "kr.co.digitalanchor.studytime.action.ATTACH_ALARM";
    public static final String ACTION_DETACH_ALARM = "kr.co.digitalanchor.studytime.action.DETACH_ALARM";
    public static final String EXTRA_ALARM_ID = "kr.co.digitalanchor.studytime.extra.ALARM_ID";
    public static final String EXTRA_ALARM_MODE = "kr.co.digitalanchor.studytime.extra.ALARM_MODE";
    public static final String EXTRA_REQUEST_CODE = "kr.co.digitalanchor.studytime.extra.REQUEST_CODE";

    public static final String ACTION_BLUETOOTH_ON = "kr.co.digitalanchor.studytime.action.BLUETOOTH_ON";
    public static final String ACTION_BLUETOOTH_OFF = "kr.co.digitalanchor.studytime.action.BLUETOOTH_OFF";

    public static final String ACTION_LOCATION_PROVIDERS = "kr.co.digitalanchor.studytime.action.LOCATION_PROVIDERS";

    public static final String ACTION_REMOTE_POLICY_LOAD = "kr.co.digitalanchor.studytime.action.REMOTE_POLICY_LOAD";

    public static final String ACTION_LOCAL_UNLOCK_FINISH = "kr.co.digitalanchor.studytime.action.LOCAL_UNLOCK_FINISH";

    public static final String ACTION_MIDNIGHT = "kr.co.digitalanchor.studytime.action.MIDNIGHT";

    public static final String ACTION_LOG = "kr.co.digitalanchor.studytime.action.LOG";
    public static final String ACTION_CHECK_10PM = "kr.co.digitalanchor.studytime.action.CHECK_10PM";

    public static final String ACTION_POLICY_RESUME = "kr.co.digitalanchor.studytime.action.POLICY_RESUME";
    public static final String ACTION_POLICY_PAUSE = "kr.co.digitalanchor.studytime.action.POLICY_PAUSE";

    public static final String ACTION_NETWORK_CHANGED = "kr.co.digitalanchor.studytime.action.NETWORK_CHANGED";

    public static final String ACTION_REMOTE_RELEASE_TRY = "kr.co.digitalanchor.studytime.action.REMOTE_RELEASE_TRY";

    public static final int CODE_CHECK_PRIVATE = 1000;
    public static final int CODE_REMOTE_POLICY_FAIL = 1001;
    public static final int CODE_LOG = 1002;
    public static final int CODE_CHECK_PRIVATE_LOCAL = 1003;
    public static final int CODE_CHECK_PRIVATE_REMOTE = 1004;
    public static final int CODE_CHECK_PRIVATE_BEACON = 1005;
    public static final int CODE_REMOTE_RELEASE_FAIL = 1006;
    public static final int CODE_CHECK_10PM = 1007;
    public static final int CODE_REMOTE_POLICY_RETRY = 1008;
    public static final int CODE_REMOTE_SELECT_CLICKED = 1009;
    public static final int CODE_LOCAL_UNLOCK_FINISH = 1010;
    public static final int CODE_MIDNIGHT = 1011;


    /**
     * 이 값 부터는 사용 금지.
     */
    @SuppressWarnings("unused")
    public static final int CODE_LOCK_ALARM = 70000;
}
