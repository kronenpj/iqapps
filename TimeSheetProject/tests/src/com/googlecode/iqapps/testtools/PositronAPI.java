package com.googlecode.iqapps.testtools;

/**
 * A portable interface defining positron's features.
 * 
 * @author philhsmith
 */
public interface PositronAPI {
	
	/** Constants from KeyEvent. */
	public static interface Key {
		/** KeyEvent.ACTION_DOWN             */ static final int ACTION_DOWN     = 0;
		/** KeyEvent.ACTION_UP               */ static final int ACTION_UP       = 1;
		/** KeyEvent.ACTION_MULTIPLE         */ static final int ACTION_MULTIPLE = 2;

		/** KeyEvent.META_ALT_ON             */ static final int META_ALT_ON         = 0x02;
		/** KeyEvent.META_ALT_LEFT_ON        */ static final int META_ALT_LEFT_ON    = 0x10;
		/** KeyEvent.META_ALT_RIGHT_ON       */ static final int META_ALT_RIGHT_ON   = 0x20;
		/** KeyEvent.META_SHIFT_ON           */ static final int META_SHIFT_ON       = 0x1;
		/** KeyEvent.META_SHIFT_LEFT_ON      */ static final int META_SHIFT_LEFT_ON  = 0x40;
		/** KeyEvent.META_SHIFT_RIGHT_ON     */ static final int META_SHIFT_RIGHT_ON = 0x80;
		/** KeyEvent.META_SYM_ON             */ static final int META_SYM_ON         = 0x4;

		/** KeyEvent.FLAG_WOKE_HERE          */ static final int FLAG_WOKE_HERE  = 0x1;
		  
		/** KeyEvent.KEYCODE_SOFT_LEFT       */ static final int SOFT_LEFT       = 1;
		/** KeyEvent.KEYCODE_SOFT_RIGHT      */ static final int SOFT_RIGHT      = 2;
		/** KeyEvent.KEYCODE_HOME            */ static final int HOME            = 3;
		/** KeyEvent.KEYCODE_BACK            */ static final int BACK            = 4;
		/** KeyEvent.KEYCODE_CALL            */ static final int CALL            = 5;
		/** KeyEvent.KEYCODE_ENDCALL         */ static final int ENDCALL         = 6;
		/** KeyEvent.KEYCODE_0               */ static final int KEYCODE_0       = 7;
		/** KeyEvent.KEYCODE_1               */ static final int KEYCODE_1       = 8;
		/** KeyEvent.KEYCODE_2               */ static final int KEYCODE_2       = 9;
		/** KeyEvent.KEYCODE_3               */ static final int KEYCODE_3       = 10;
		/** KeyEvent.KEYCODE_4               */ static final int KEYCODE_4       = 11;
		/** KeyEvent.KEYCODE_5               */ static final int KEYCODE_5       = 12;
		/** KeyEvent.KEYCODE_6               */ static final int KEYCODE_6       = 13;
		/** KeyEvent.KEYCODE_7               */ static final int KEYCODE_7       = 14;
		/** KeyEvent.KEYCODE_8               */ static final int KEYCODE_8       = 15;
		/** KeyEvent.KEYCODE_9               */ static final int KEYCODE_9       = 16;
		/** KeyEvent.KEYCODE_STAR            */ static final int STAR            = 17;
		/** KeyEvent.KEYCODE_POUND           */ static final int POUND           = 18;
		/** KeyEvent.KEYCODE_DPAD_UP         */ static final int DPAD_UP         = 19;
		/** KeyEvent.KEYCODE_DPAD_DOWN       */ static final int DPAD_DOWN       = 20;
		/** KeyEvent.KEYCODE_DPAD_LEFT       */ static final int DPAD_LEFT       = 21;
		/** KeyEvent.KEYCODE_DPAD_RIGHT      */ static final int DPAD_RIGHT      = 22;
		/** KeyEvent.KEYCODE_DPAD_CENTER     */ static final int DPAD_CENTER     = 23;
		/** KeyEvent.KEYCODE_VOLUME_UP       */ static final int VOLUME_UP       = 24;
		/** KeyEvent.KEYCODE_VOLUME_DOWN     */ static final int VOLUME_DOWN     = 25;
		/** KeyEvent.KEYCODE_POWER           */ static final int POWER           = 26;
		/** KeyEvent.KEYCODE_CAMERA          */ static final int CAMERA          = 27;
		/** KeyEvent.KEYCODE_CLEAR           */ static final int CLEAR           = 28;
		/** KeyEvent.KEYCODE_A               */ static final int A               = 29;
		/** KeyEvent.KEYCODE_B               */ static final int B               = 30;
		/** KeyEvent.KEYCODE_C               */ static final int C               = 31;
		/** KeyEvent.KEYCODE_D               */ static final int D               = 32;
		/** KeyEvent.KEYCODE_E               */ static final int E               = 33;
		/** KeyEvent.KEYCODE_F               */ static final int F               = 34;
		/** KeyEvent.KEYCODE_G               */ static final int G               = 35;
		/** KeyEvent.KEYCODE_H               */ static final int H               = 36;
		/** KeyEvent.KEYCODE_I               */ static final int I               = 37;
		/** KeyEvent.KEYCODE_J               */ static final int J               = 38;
		/** KeyEvent.KEYCODE_K               */ static final int K               = 39;
		/** KeyEvent.KEYCODE_L               */ static final int L               = 40;
		/** KeyEvent.KEYCODE_M               */ static final int M               = 41;
		/** KeyEvent.KEYCODE_N               */ static final int N               = 42;
		/** KeyEvent.KEYCODE_O               */ static final int O               = 43;
		/** KeyEvent.KEYCODE_P               */ static final int P               = 44;
		/** KeyEvent.KEYCODE_Q               */ static final int Q               = 45;
		/** KeyEvent.KEYCODE_R               */ static final int R               = 46;
		/** KeyEvent.KEYCODE_S               */ static final int S               = 47;
		/** KeyEvent.KEYCODE_T               */ static final int T               = 48;
		/** KeyEvent.KEYCODE_U               */ static final int U               = 49;
		/** KeyEvent.KEYCODE_V               */ static final int V               = 50;
		/** KeyEvent.KEYCODE_W               */ static final int W               = 51;
		/** KeyEvent.KEYCODE_X               */ static final int X               = 52;
		/** KeyEvent.KEYCODE_Y               */ static final int Y               = 53;
		/** KeyEvent.KEYCODE_Z               */ static final int Z               = 54;
		/** KeyEvent.KEYCODE_COMMA           */ static final int COMMA           = 55;
		/** KeyEvent.KEYCODE_PERIOD          */ static final int PERIOD          = 56;
		/** KeyEvent.KEYCODE_ALT_LEFT        */ static final int ALT_LEFT        = 57;
		/** KeyEvent.KEYCODE_ALT_RIGHT       */ static final int ALT_RIGHT       = 58;
		/** KeyEvent.KEYCODE_SHIFT_LEFT      */ static final int SHIFT_LEFT      = 59;
		/** KeyEvent.KEYCODE_SHIFT_RIGHT     */ static final int SHIFT_RIGHT     = 60;
		/** KeyEvent.KEYCODE_TAB             */ static final int TAB             = 61;
		/** KeyEvent.KEYCODE_SPACE           */ static final int SPACE           = 62;
		/** KeyEvent.KEYCODE_SYM             */ static final int SYM             = 63;
		/** KeyEvent.KEYCODE_EXPLORER        */ static final int EXPLORER        = 64;
		/** KeyEvent.KEYCODE_ENVELOPE        */ static final int ENVELOPE        = 65;
		/** KeyEvent.KEYCODE_ENTER           */ static final int ENTER           = 66;
		/** KeyEvent.KEYCODE_DEL             */ static final int DEL             = 67;
		/** KeyEvent.KEYCODE_GRAVE           */ static final int GRAVE           = 68;
		/** KeyEvent.KEYCODE_MINUS           */ static final int MINUS           = 69;
		/** KeyEvent.KEYCODE_EQUALS          */ static final int EQUALS          = 70;
		/** KeyEvent.KEYCODE_LEFT_BRACKET    */ static final int LEFT_BRACKET    = 71;
		/** KeyEvent.KEYCODE_RIGHT_BRACKET   */ static final int RIGHT_BRACKET   = 72;
		/** KeyEvent.KEYCODE_BACKSLASH       */ static final int BACKSLASH       = 73;
		/** KeyEvent.KEYCODE_SEMICOLON       */ static final int SEMICOLON       = 74;
		/** KeyEvent.KEYCODE_APOSTROPHE      */ static final int APOSTROPHE      = 75;
		/** KeyEvent.KEYCODE_SLASH           */ static final int SLASH           = 76;
		/** KeyEvent.KEYCODE_AT              */ static final int AT              = 77;
		/** KeyEvent.KEYCODE_NUM             */ static final int NUM             = 78;
		/** KeyEvent.KEYCODE_HEADSETHOOK     */ static final int HEADSETHOOK     = 79;
		/** KeyEvent.KEYCODE_FOCUS (*Camera* focus)  */ static final int FOCUS   = 80;
		/** KeyEvent.KEYCODE_PLUS            */ static final int PLUS            = 81;
		/** KeyEvent.KEYCODE_MENU            */ static final int MENU            = 82;
		/** KeyEvent.KEYCODE_NOTIFICATION    */ static final int NOTIFICATION    = 83;
		/** KeyEvent.KEYCODE_SEARCH          */ static final int SEARCH          = 84;

		/** KeyEvent.KEYCODE_DPAD_CENTER     */ static final int CENTER          = DPAD_CENTER;
		/** KeyEvent.KEYCODE_DPAD_UP         */ static final int UP              = DPAD_UP;
		/** KeyEvent.KEYCODE_DPAD_DOWN       */ static final int DOWN            = DPAD_DOWN;
		/** KeyEvent.KEYCODE_DPAD_LEFT       */ static final int LEFT            = DPAD_LEFT;
		/** KeyEvent.KEYCODE_DPAD_RIGHT      */ static final int RIGHT           = DPAD_RIGHT; 
		/** KeyEvent.KEYCODE_DPAD_CENTER     */ static final int CLICK           = DPAD_CENTER;
	}
	
	/** Constants from MotionEvent. */
	public static interface Motion {
		/** MotionEvent.ACTION_DOWN   */ static final int ACTION_DOWN   = 0;
		/** MotionEvent.ACTION_UP     */ static final int ACTION_UP     = 1;
		/** MotionEvent.ACTION_MOVE   */ static final int ACTION_MOVE   = 2;
		/** MotionEvent.ACTION_CANCEL */ static final int ACTION_CANCEL = 3;
		/** MotionEvent.EDGE_TOP      */ static final int EDGE_TOP      = 0x00000001;
		/** MotionEvent.EDGE_BOTTOM   */ static final int EDGE_BOTTOM   = 0x00000002;
		/** MotionEvent.EDGE_LEFT     */ static final int EDGE_LEFT     = 0x00000004;
		/** MotionEvent.EDGE_RIGHT    */ static final int EDGE_RIGHT    = 0x00000008;
	}

	/** Constants from Intent. */
	public static interface Intents {
		/** Intent.ACTION_MAIN                              */ static final String ACTION_MAIN                             = "android.intent.action.MAIN";
		/** Intent.ACTION_VIEW                              */ static final String ACTION_VIEW                             = "android.intent.action.VIEW";
		/** Intent.ACTION_DEFAULT                           */ static final String ACTION_DEFAULT                          = ACTION_VIEW;
		/** Intent.ACTION_ATTACH_DATA                       */ static final String ACTION_ATTACH_DATA                      = "android.intent.action.ATTACH_DATA";
		/** Intent.ACTION_EDIT                              */ static final String ACTION_EDIT                             = "android.intent.action.EDIT";
		/** Intent.ACTION_INSERT_OR_EDIT                    */ static final String ACTION_INSERT_OR_EDIT                   = "android.intent.action.INSERT_OR_EDIT";
		/** Intent.ACTION_PICK                              */ static final String ACTION_PICK                             = "android.intent.action.PICK";
		/** Intent.ACTION_CREATE_SHORTCUT                   */ static final String ACTION_CREATE_SHORTCUT                  = "android.intent.action.CREATE_SHORTCUT";
		/** Intent.EXTRA_SHORTCUT_INTENT                    */ static final String EXTRA_SHORTCUT_INTENT                   = "android.intent.extra.shortcut.INTENT";
		/** Intent.EXTRA_SHORTCUT_NAME                      */ static final String EXTRA_SHORTCUT_NAME                     = "android.intent.extra.shortcut.NAME";
		/** Intent.EXTRA_SHORTCUT_ICON                      */ static final String EXTRA_SHORTCUT_ICON                     = "android.intent.extra.shortcut.ICON";
		/** Intent.EXTRA_SHORTCUT_ICON_RESOURCE             */ static final String EXTRA_SHORTCUT_ICON_RESOURCE            = "android.intent.extra.shortcut.ICON_RESOURCE";
		/** Intent.ACTION_CHOOSER                           */ static final String ACTION_CHOOSER                          = "android.intent.action.CHOOSER";
		/** Intent.ACTION_GET_CONTENT                       */ static final String ACTION_GET_CONTENT                      = "android.intent.action.GET_CONTENT";
		/** Intent.ACTION_DIAL                              */ static final String ACTION_DIAL                             = "android.intent.action.DIAL";
		/** Intent.ACTION_CALL                              */ static final String ACTION_CALL                             = "android.intent.action.CALL";
		/** Intent.ACTION_CALL_EMERGENCY                    */ static final String ACTION_CALL_EMERGENCY                   = "android.intent.action.CALL_EMERGENCY";
		/** Intent.ACTION_CALL_PRIVILEGED                   */ static final String ACTION_CALL_PRIVILEGED                  = "android.intent.action.CALL_PRIVILEGED";
		/** Intent.ACTION_SENDTO                            */ static final String ACTION_SENDTO                           = "android.intent.action.SENDTO";
		/** Intent.ACTION_SEND                              */ static final String ACTION_SEND                             = "android.intent.action.SEND";
		/** Intent.ACTION_ANSWER                            */ static final String ACTION_ANSWER                           = "android.intent.action.ANSWER";
		/** Intent.ACTION_INSERT                            */ static final String ACTION_INSERT                           = "android.intent.action.INSERT";
		/** Intent.ACTION_DELETE                            */ static final String ACTION_DELETE                           = "android.intent.action.DELETE";
		/** Intent.ACTION_RUN                               */ static final String ACTION_RUN                              = "android.intent.action.RUN";
		/** Intent.ACTION_SYNC                              */ static final String ACTION_SYNC                             = "android.intent.action.SYNC";
		/** Intent.ACTION_PICK_ACTIVITY                     */ static final String ACTION_PICK_ACTIVITY                    = "android.intent.action.PICK_ACTIVITY";
		/** Intent.ACTION_SEARCH                            */ static final String ACTION_SEARCH                           = "android.intent.action.SEARCH";
		/** Intent.ACTION_WEB_SEARCH                        */ static final String ACTION_WEB_SEARCH                       = "android.intent.action.WEB_SEARCH";
		/** Intent.ACTION_ALL_APPS                          */ static final String ACTION_ALL_APPS                         = "android.intent.action.ALL_APPS";
		/** Intent.ACTION_SET_WALLPAPER                     */ static final String ACTION_SET_WALLPAPER                    = "android.intent.action.SET_WALLPAPER";
		/** Intent.ACTION_BUG_REPORT                        */ static final String ACTION_BUG_REPORT                       = "android.intent.action.BUG_REPORT";
		/** Intent.ACTION_FACTORY_TEST                      */ static final String ACTION_FACTORY_TEST                     = "android.intent.action.FACTORY_TEST";
		/** Intent.ACTION_CALL_BUTTON                       */ static final String ACTION_CALL_BUTTON                      = "android.intent.action.CALL_BUTTON";
		/** Intent.ACTION_VOICE_COMMAND                     */ static final String ACTION_VOICE_COMMAND                    = "android.intent.action.VOICE_COMMAND";
		/** Intent.ACTION_SCREEN_OFF                        */ static final String ACTION_SCREEN_OFF                       = "android.intent.action.SCREEN_OFF";
		/** Intent.ACTION_SCREEN_ON                         */ static final String ACTION_SCREEN_ON                        = "android.intent.action.SCREEN_ON";
		/** Intent.ACTION_TIME_TICK                         */ static final String ACTION_TIME_TICK                        = "android.intent.action.TIME_TICK";
		/** Intent.ACTION_TIME_CHANGED                      */ static final String ACTION_TIME_CHANGED                     = "android.intent.action.TIME_SET";
		/** Intent.ACTION_DATE_CHANGED                      */ static final String ACTION_DATE_CHANGED                     = "android.intent.action.DATE_CHANGED";
		/** Intent.ACTION_TIMEZONE_CHANGED                  */ static final String ACTION_TIMEZONE_CHANGED                 = "android.intent.action.TIMEZONE_CHANGED";
		/** Intent.ACTION_ALARM_CHANGED                     */ static final String ACTION_ALARM_CHANGED                    = "android.intent.action.ALARM_CHANGED";
		/** Intent.ACTION_SYNC_STATE_CHANGED                */ static final String ACTION_SYNC_STATE_CHANGED               = "android.intent.action.SYNC_STATE_CHANGED";
		/** Intent.ACTION_BOOT_COMPLETED                    */ static final String ACTION_BOOT_COMPLETED                   = "android.intent.action.BOOT_COMPLETED";
		/** Intent.ACTION_CLOSE_SYSTEM_DIALOGS              */ static final String ACTION_CLOSE_SYSTEM_DIALOGS             = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
		/** Intent.ACTION_PACKAGE_INSTALL                   */ static final String ACTION_PACKAGE_INSTALL                  = "android.intent.action.PACKAGE_INSTALL";
		/** Intent.ACTION_PACKAGE_ADDED                     */ static final String ACTION_PACKAGE_ADDED                    = "android.intent.action.PACKAGE_ADDED";
		/** Intent.ACTION_PACKAGE_REMOVED                   */ static final String ACTION_PACKAGE_REMOVED                  = "android.intent.action.PACKAGE_REMOVED";
		/** Intent.ACTION_PACKAGE_CHANGED                   */ static final String ACTION_PACKAGE_CHANGED                  = "android.intent.action.PACKAGE_CHANGED";
		/** Intent.ACTION_PACKAGE_RESTARTED                 */ static final String ACTION_PACKAGE_RESTARTED                = "android.intent.action.PACKAGE_RESTARTED";
		/** Intent.ACTION_UID_REMOVED                       */ static final String ACTION_UID_REMOVED                      = "android.intent.action.UID_REMOVED";
		/** Intent.ACTION_WALLPAPER_CHANGED                 */ static final String ACTION_WALLPAPER_CHANGED                = "android.intent.action.WALLPAPER_CHANGED";
		/** Intent.ACTION_CONFIGURATION_CHANGED             */ static final String ACTION_CONFIGURATION_CHANGED            = "android.intent.action.CONFIGURATION_CHANGED";
		/** Intent.ACTION_BATTERY_CHANGED                   */ static final String ACTION_BATTERY_CHANGED                  = "android.intent.action.BATTERY_CHANGED";
		/** Intent.ACTION_BATTERY_LOW                       */ static final String ACTION_BATTERY_LOW                      = "android.intent.action.BATTERY_LOW";
		/** Intent.ACTION_POWER_CONNECTED                   */ static final String ACTION_POWER_CONNECTED                  = "android.intent.action.POWER_CONNECTED";
		/** Intent.ACTION_POWER_DISCONNECTED                */ static final String ACTION_POWER_DISCONNECTED               = "android.intent.action.POWER_DISCONNECTED";
		/** Intent.ACTION_DEVICE_STORAGE_LOW                */ static final String ACTION_DEVICE_STORAGE_LOW               = "android.intent.action.DEVICE_STORAGE_LOW";
		/** Intent.ACTION_DEVICE_STORAGE_OK                 */ static final String ACTION_DEVICE_STORAGE_OK                = "android.intent.action.DEVICE_STORAGE_OK";
		/** Intent.ACTION_MANAGE_PACKAGE_STORAGE            */ static final String ACTION_MANAGE_PACKAGE_STORAGE           = "android.intent.action.MANAGE_PACKAGE_STORAGE";
		/** Intent.ACTION_UMS_CONNECTED                     */ static final String ACTION_UMS_CONNECTED                    = "android.intent.action.UMS_CONNECTED";
		/** Intent.ACTION_UMS_DISCONNECTED                  */ static final String ACTION_UMS_DISCONNECTED                 = "android.intent.action.UMS_DISCONNECTED";
		/** Intent.ACTION_MEDIA_REMOVED                     */ static final String ACTION_MEDIA_REMOVED                    = "android.intent.action.MEDIA_REMOVED";
		/** Intent.ACTION_MEDIA_UNMOUNTED                   */ static final String ACTION_MEDIA_UNMOUNTED                  = "android.intent.action.MEDIA_UNMOUNTED";
		/** Intent.ACTION_MEDIA_MOUNTED                     */ static final String ACTION_MEDIA_MOUNTED                    = "android.intent.action.MEDIA_MOUNTED";
		/** Intent.ACTION_MEDIA_SHARED                      */ static final String ACTION_MEDIA_SHARED                     = "android.intent.action.MEDIA_SHARED";
		/** Intent.ACTION_MEDIA_BAD_REMOVAL                 */ static final String ACTION_MEDIA_BAD_REMOVAL                = "android.intent.action.MEDIA_BAD_REMOVAL";
		/** Intent.ACTION_MEDIA_UNMOUNTABLE                 */ static final String ACTION_MEDIA_UNMOUNTABLE                = "android.intent.action.MEDIA_UNMOUNTABLE";
		/** Intent.ACTION_MEDIA_EJECT                       */ static final String ACTION_MEDIA_EJECT                      = "android.intent.action.MEDIA_EJECT";
		/** Intent.ACTION_MEDIA_SCANNER_STARTED             */ static final String ACTION_MEDIA_SCANNER_STARTED            = "android.intent.action.MEDIA_SCANNER_STARTED";
		/** Intent.ACTION_MEDIA_SCANNER_FINISHED            */ static final String ACTION_MEDIA_SCANNER_FINISHED           = "android.intent.action.MEDIA_SCANNER_FINISHED";
		/** Intent.ACTION_MEDIA_SCANNER_SCAN_FILE           */ static final String ACTION_MEDIA_SCANNER_SCAN_FILE          = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
		/** Intent.ACTION_MEDIA_BUTTON                      */ static final String ACTION_MEDIA_BUTTON                     = "android.intent.action.MEDIA_BUTTON";
		/** Intent.ACTION_CAMERA_BUTTON                     */ static final String ACTION_CAMERA_BUTTON                    = "android.intent.action.CAMERA_BUTTON";
		/** Intent.ACTION_GTALK_SERVICE_CONNECTED           */ static final String ACTION_GTALK_SERVICE_CONNECTED          = "android.intent.action.GTALK_CONNECTED";
		/** Intent.ACTION_GTALK_SERVICE_DISCONNECTED        */ static final String ACTION_GTALK_SERVICE_DISCONNECTED       = "android.intent.action.GTALK_DISCONNECTED";
		/** Intent.ACTION_AIRPLANE_MODE_CHANGED             */ static final String ACTION_AIRPLANE_MODE_CHANGED            = "android.intent.action.AIRPLANE_MODE";
		/** Intent.ACTION_PROVIDER_CHANGED                  */ static final String ACTION_PROVIDER_CHANGED                 = "android.intent.action.PROVIDER_CHANGED";
		/** Intent.ACTION_HEADSET_PLUG                      */ static final String ACTION_HEADSET_PLUG                     = "android.intent.action.HEADSET_PLUG";
		/** Intent.ACTION_NEW_OUTGOING_CALL                 */ static final String ACTION_NEW_OUTGOING_CALL                = "android.intent.action.NEW_OUTGOING_CALL";
		/** Intent.ACTION_REBOOT                            */ static final String ACTION_REBOOT                           = "android.intent.action.REBOOT";
		/** Intent.CATEGORY_DEFAULT                         */ static final String CATEGORY_DEFAULT                        = "android.intent.category.DEFAULT";
		/** Intent.CATEGORY_BROWSABLE                       */ static final String CATEGORY_BROWSABLE                      = "android.intent.category.BROWSABLE";
		/** Intent.CATEGORY_ALTERNATIVE                     */ static final String CATEGORY_ALTERNATIVE                    = "android.intent.category.ALTERNATIVE";
		/** Intent.CATEGORY_SELECTED_ALTERNATIVE            */ static final String CATEGORY_SELECTED_ALTERNATIVE           = "android.intent.category.SELECTED_ALTERNATIVE";
		/** Intent.CATEGORY_TAB                             */ static final String CATEGORY_TAB                            = "android.intent.category.TAB";
		/** Intent.CATEGORY_GADGET                          */ static final String CATEGORY_GADGET                         = "android.intent.category.GADGET";
		/** Intent.CATEGORY_LAUNCHER                        */ static final String CATEGORY_LAUNCHER                       = "android.intent.category.LAUNCHER";
		/** Intent.CATEGORY_HOME                            */ static final String CATEGORY_HOME                           = "android.intent.category.HOME";
		/** Intent.CATEGORY_PREFERENCE                      */ static final String CATEGORY_PREFERENCE                     = "android.intent.category.PREFERENCE";
		/** Intent.CATEGORY_DEVELOPMENT_PREFERENCE          */ static final String CATEGORY_DEVELOPMENT_PREFERENCE         = "android.intent.category.DEVELOPMENT_PREFERENCE";
		/** Intent.CATEGORY_EMBED                           */ static final String CATEGORY_EMBED                          = "android.intent.category.EMBED";
		/** Intent.CATEGORY_MONKEY                          */ static final String CATEGORY_MONKEY                         = "android.intent.category.MONKEY";
		/** Intent.CATEGORY_TEST                            */ static final String CATEGORY_TEST                           = "android.intent.category.TEST";
		/** Intent.CATEGORY_UNIT_TEST                       */ static final String CATEGORY_UNIT_TEST                      = "android.intent.category.UNIT_TEST";
		/** Intent.CATEGORY_SAMPLE_CODE                     */ static final String CATEGORY_SAMPLE_CODE                    = "android.intent.category.SAMPLE_CODE";
		/** Intent.CATEGORY_OPENABLE                        */ static final String CATEGORY_OPENABLE                       = "android.intent.category.OPENABLE";
		/** Intent.CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST  */ static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
		/** Intent.EXTRA_TEMPLATE                           */ static final String EXTRA_TEMPLATE                          = "android.intent.extra.TEMPLATE";
		/** Intent.EXTRA_TEXT                               */ static final String EXTRA_TEXT                              = "android.intent.extra.TEXT";
		/** Intent.EXTRA_STREAM                             */ static final String EXTRA_STREAM                            = "android.intent.extra.STREAM";
		/** Intent.EXTRA_EMAIL                              */ static final String EXTRA_EMAIL                             = "android.intent.extra.EMAIL";
		/** Intent.EXTRA_CC                                 */ static final String EXTRA_CC                                = "android.intent.extra.CC";
		/** Intent.EXTRA_BCC                                */ static final String EXTRA_BCC                               = "android.intent.extra.BCC";
		/** Intent.EXTRA_SUBJECT                            */ static final String EXTRA_SUBJECT                           = "android.intent.extra.SUBJECT";
		/** Intent.EXTRA_INTENT                             */ static final String EXTRA_INTENT                            = "android.intent.extra.INTENT";
		/** Intent.EXTRA_TITLE                              */ static final String EXTRA_TITLE                             = "android.intent.extra.TITLE";
		/** Intent.EXTRA_KEY_EVENT                          */ static final String EXTRA_KEY_EVENT                         = "android.intent.extra.KEY_EVENT";
		/** Intent.EXTRA_DONT_KILL_APP                      */ static final String EXTRA_DONT_KILL_APP                     = "android.intent.extra.DONT_KILL_APP";
		/** Intent.EXTRA_PHONE_NUMBER                       */ static final String EXTRA_PHONE_NUMBER                      = "android.intent.extra.PHONE_NUMBER";
		/** Intent.EXTRA_UID                                */ static final String EXTRA_UID                               = "android.intent.extra.UID";
		/** Intent.EXTRA_ALARM_COUNT                        */ static final String EXTRA_ALARM_COUNT                       = "android.intent.extra.ALARM_COUNT";

		/** Intent.FLAG_GRANT_READ_URI_PERMISSION       */ static final int FLAG_GRANT_READ_URI_PERMISSION      = 0x00000001;
		/** Intent.FLAG_GRANT_WRITE_URI_PERMISSION      */ static final int FLAG_GRANT_WRITE_URI_PERMISSION     = 0x00000002;
		/** Intent.FLAG_FROM_BACKGROUND                 */ static final int FLAG_FROM_BACKGROUND                = 0x00000004;
		/** Intent.FLAG_DEBUG_LOG_RESOLUTION            */ static final int FLAG_DEBUG_LOG_RESOLUTION           = 0x00000008;
		/** Intent.FLAG_ACTIVITY_NO_HISTORY             */ static final int FLAG_ACTIVITY_NO_HISTORY            = 0x40000000;
		/** Intent.FLAG_ACTIVITY_SINGLE_TOP             */ static final int FLAG_ACTIVITY_SINGLE_TOP            = 0x20000000;
		/** Intent.FLAG_ACTIVITY_NEW_TASK               */ static final int FLAG_ACTIVITY_NEW_TASK              = 0x10000000;
		/** Intent.FLAG_ACTIVITY_MULTIPLE_TASK          */ static final int FLAG_ACTIVITY_MULTIPLE_TASK         = 0x08000000;
		/** Intent.FLAG_ACTIVITY_CLEAR_TOP              */ static final int FLAG_ACTIVITY_CLEAR_TOP             = 0x04000000;
		/** Intent.FLAG_ACTIVITY_FORWARD_RESULT         */ static final int FLAG_ACTIVITY_FORWARD_RESULT        = 0x02000000;
		/** Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP        */ static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP       = 0x01000000;
		/** Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS   */ static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS  = 0x00800000;
		/** Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT       */ static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT      = 0x00400000;
		/** Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED   */ static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED  = 0x00200000;
		/** Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY  */ static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 0x00100000;
		/** Intent.FLAG_RECEIVER_REGISTERED_ONLY        */ static final int FLAG_RECEIVER_REGISTERED_ONLY       = 0x40000000;
		/** Intent.FILL_IN_ACTION                       */ static final int FILL_IN_ACTION                      = 1<<0;
		/** Intent.FILL_IN_DATA                         */ static final int FILL_IN_DATA                        = 1<<1;
		/** Intent.FILL_IN_CATEGORIES                   */ static final int FILL_IN_CATEGORIES                  = 1<<2;
		/** Intent.FILL_IN_COMPONENT                    */ static final int FILL_IN_COMPONENT                   = 1<<3;
	}
	
	/** Constants from Menu. */
	public static interface Menu {
		/** Menu.FIRST */ static final int FIRST = 1;
	}
	
	/** Backup all databases in the tested application. */
	void backup();
	
	/** Backup the given database. */
	void backup(String database);
	
	/**
	 * Evaluate the ViewShorthand path starting from the activity at the given depth.
	 * @return The result as a boolean.
	 */
	boolean booleanAt(int depth, String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * @return The result as a boolean.
	 */
	boolean booleanAt(String path);
	
	/** sendKeyDownUp(KEYCODE_DPAD_CENTER) */
	void click();
	
	/**
	 * Evaluate the ViewShorthand path starting from the activity at the given depth.
	 * @return The result as a double.
	 */
	double doubleAt(int depth, String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * @return The result as a double.
	 */
	double doubleAt(String path);
	
	/** Execute the given Sql script. */
	void sql(String database, String script);
	
	/** Finish the current activity. */
	void finish();

	/** Activity.finish() the activity with the given depth on the stack. */
	void finish(int depth);
	
	/** Finish all detected activities on the stack. */
	void finishAll();

	/**
	 * Evaluate the ViewShorthand path starting from the activity at the given depth.
	 * @return The result as a float.
	 */
	float floatAt(int depth, String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * @return The result as a float.
	 */
	float floatAt(String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the activity at the given depth.
	 * @return The result as an int.
	 */
	int intAt(int depth, String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * @return The result as an int.
	 */
	int intAt(String path);
	
	/** invokeMenuAction with the current activity. */
	boolean menu(int id);
	
	/** invokeMenuAction with the current activity. */
	boolean menu(int id, int flag);
	
	/** invokeMenuActionSync with the activity at the given depth on the stack, resuming momentarily if necessary. */
	boolean menu(int depth, int id, int flag);
	
	/** invokeContextMenuActionSync with the current activity. */
	boolean contextMenu(int id);
	
	/** invokeContextMenuActionSync with the current activity. */
	boolean contextMenu(int id, int flag);
	
	/** invokeContextMenuActionSync with the activity at the given depth on the stack, resuming momentarily if necessary. */
	boolean contextMenu(int depth, int id, int flag);
	
	/**
	 * Pause execution of the target package.
	 *
	 * This is useful for inspecting the view state of the target package,
	 * since doing so while it is running can create race conditions.  Inspect
	 * this state using the various 'at' methods.
	 * 
	 * Resume execution with resume().
	 *
	 * Pausing an already-paused application is a safe no-op.
	 *
	 * Paused applications can't respond to events, nor execute synchronous
	 * commands such as sendStringSync (or any *Sync.)  Keep this in mind when
	 * writing tests.
	 *
	 * Pausing is done by asynchronously calling Instrumentation.runOnMainSync
	 * on a blocking operation.
	 */
	void pause();

	/** Is the target package paused? */
	boolean paused();
	
	/**
	 * sendKeyDownUpSync on all keys in order, resuming momentarily if necessary.
	 *
	 * Combined with the renamed constants available in positron.TestCase, this
	 * enables things like press(UP, UP, DOWN, DOWN, LEFT, RIGHT, LEFT, RIGHT, "ba")
	 * which may otherwise be cumbersome.
	 *
	 * @param keys A mixture of ints and Strings.  ints are sent with sendKeyDownUpSync,
	 * 		       Strings are send with sendStringSync.
	 * @throws IllegalArgumentException if something other than an int or String was passed.
	 */
	void press(Object... keys);
	
	/** Stop the positron instrumentation and cleanup. */
	void quit();
	
	/**
	 * Resume executing the target package if it has been paused.
	 *
	 * Resuming an unpaused package is a safe noop.
	 */
	void resume();

	/**
	 * Restore the given database from a previous backup call.
	 * This deletes the backup as well.
	 */
	void restore(String database);

	/** Restore all databases in the tested application that have backups. */
	void restore();
	
	/** sendCharacterSync, resuming momentarily if necessary. */
	void sendCharacter(int keyCode);

	/** sendKeySync, resuming momentarily if necessary. */
	void sendKey(int action, int code);
	
	/**
	 * sendKeySync, resuming momentarily if necessary.
	 * 
 	 * KeyEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendKey(long eventTimeAfterDown, int action, int code, int repeat);
	
	/**
	 * sendKeySync, resuming momentarily if necessary.
	 * 
	 * KeyEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendKey(long eventTimeAfterDown, int action, int code, int repeat, int metaState);
	
	/**
	 * sendKeySync, resuming momentarily if necessary.
	 * 
	 * KeyEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendKey(long eventTimeAfterDown, int action, int code, int repeat, int metaState, int device, int scancode);
		
	/**
	 * sendKeySync, resuming momentarily if necessary.
	 * 
	 * KeyEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendKey(long eventTimeAfterDown, int action, int code, int repeat, int metaState, int device, int scancode, int flags);
	
	/** sendKeyDownUpSync, resuming momentarily if necessary. */
	void sendKeyDownUp(int key);
	
	/** sendStringSync, resuming momentarily if necessary. */
	void sendString(String text);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String action);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String packageName, String className);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String action, String data, String type);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String action, String data, String type, String [] categories, int [] flags);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String action, String [] categories);
	
	/** startActivitySync, resuming momentarily if necessary. */
	void startActivity(String action, String [] categories, int [] flags);

	/**
	 * Evaluate the ViewShorthand path starting from the activity at the given depth.
	 * @return The result as a String.
	 */
	String stringAt(int depth, String path);
	
	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * @return The result as a String.
	 */
	String stringAt(String path);

	/**
	 * Touch the screen (or click the pointer) at the given location.
	 * 
	 * This uses sendPointer to send an ACTION_DOWN, and an ACTION_UP a
	 * little later.
	 */
	void touch(float x, float y);

	/**
	 * Touch the screen (or click the pointer) at the center of the view with the given path.
	 * 
	 * Once the center of the view is found, this behaves as touch(float, float).
	 */
	void touch(String path);
	
	/**
	 * Drag from one point on the screen to another.
	 * 
	 * This uses sendPointer to send an ACTION_DOWN at (startX, startY),
	 * followed by an ACTION_MOVE to (endX, endY), and finally an ACTION_UP.
	 */
	void drag(float startX, float startY, float endX, float endY);

	/**
	 * Drag from the center of one view to the center of another.
	 * 
	 * Once the centers have been found, this behaves as drag(float, float, float, float).
	 */
	void drag(String start, String end);
	
	/**
	 * Send a MotionEvent via sendPointerSync, resuming momentarily if needed.
	 * 
	 * The eventTimeAfterDown and metaState are both taken to be 0.
	 */
	void sendPointer(int action, float x, float y);
	
	/**
	 * Send a MotionEvent via sendPointerSync, resuming momentarily if needed.
	 * 
	 * The eventTimeAfterDown is taken to be 0.
	 */
	void sendPointer(int action, float x, float y, int metaState);
	
	/**
	 * Send a MotionEvent via sendPointerSync, resuming momentarily if needed.
	 * 
	 * MotionEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 * 
	 * @param eventTimeAfterDown Time in milliseconds since the initial down event.
	 * @param action One of the MotionEvent action constants.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param metaState Any meta / modifier keys to hold while the sending the event.
	 */
	void sendPointer(long eventTimeAfterDown, int action, float x, float y, int metaState);

	/**
	 * Send a MotionEvent via sendPointerSync, resuming momentarily if needed.
	 * 
	 * MotionEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendPointer(long eventTimeAfterDown, int action, float x, float y, float pressure, float size, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags);
		
	/**
	 * Flick the trackball in the given direction.
	 */
	void flick(float x, float y);

	/**
	 * Send a MotionEvent via sendTrackballEventSync, resuming momentarily if needed.
	 * 
	 * The eventTimeAfterDown and metaState are both taken to be 0.
	 */
	void sendTrackball(int action, float x, float y);
	
	/**
	 * Send a MotionEvent via sendTrackballEventSync, resuming momentarily if needed.
	 * 
	 * The eventTimeAfterDown is taken to be 0.
	 */
	void sendTrackball(int action, float x, float y, int metaState);
	
	/**
	 * Send a MotionEvent via sendTrackballEventSync, resuming momentarily if needed.
	 * 
	 * MotionEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 * 
	 * @param eventTimeAfterDown Time in milliseconds since the initial down event.
	 * @param action One of the MotionEvent action constants.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param metaState Any meta / modifier keys to hold while the sending the event.
	 */
	void sendTrackball(long eventTimeAfterDown, int action, float x, float y, int metaState);

	/**
	 * Send a MotionEvent via sendTrackballEventSync, resuming momentarily if needed.
	 * 
	 * MotionEvents (within the android api) take a downTime which needs to be
	 * given as milliseconds from system start, from the uptimeMillis() call.
	 * 
	 * Since stories won't likely have that number around, positron uses the
	 * current uptimeMillis for the downTime.  An eventTime may be given as
	 * milliseconds relative to downTime; the downTime will be added.
	 */
	void sendTrackball(long eventTimeAfterDown, int action, float x, float y, float pressure, float size, int metaState, float xPrecision, float yPrecision, int deviceId, int edgeFlags);

	/**
	 * Wait until value.equals(stringAt(path)) or the given timeout expires, then pause().
	 * 
	 * If paused() initially, resume() before waiting.
	 * 
	 * Handy for events (like those from the touch screen) that continue to
	 * have side-effects even after the initial event has been processed.
	 * 
	 * This method necessarily polls state in a pause() / resume() cycle.  It
	 * is possible for the condition to be missed if it is only briefly or
	 * intermittently true.
	 * 
	 * @param path The view shorthand expression to watch.
	 * @param value The value to wait for.
	 * @param timeout Time in milliseconds to wait before giving up and pausing anyway.
	 * @return True if the condition was met before the timeout expired.
	 */
	boolean waitFor(String path, String value, long timeout);
	
	/**
	 * Wait until value.equals(stringAt(depth, path)) or the given timeout expires, then pause().
	 * 
	 * If paused() initially, resume() before waiting.
	 * 
	 * Handy for events (like those from the touch screen) that continue to
	 * have side-effects even after the initial event has been processed.
	 * 
	 * This method necessarily polls state in a pause() / resume() cycle.  It
	 * is possible for the condition to be missed if it is only briefly or
	 * intermittently true.
	 * 
	 * @param depth The depth down the activity stack
	 * @param path The view shorthand expression to watch.
	 * @param value The value to wait for.
	 * @param timeout Time in milliseconds to wait before giving up and pausing anyway.
	 * @return True if the condition was met before the timeout expired.
	 */
	boolean waitFor(int depth, String path, String value, long timeout);
	
}