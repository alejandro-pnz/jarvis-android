package com.coffeinum.jarvis.model;

import android.provider.BaseColumns;

public class DeviceContract {

    private DeviceContract() {
    }

    public static abstract class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String COLUMN_NAME_SERVICE_ID = "serviceid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UPDATE_DATE = "date";
        public static final String COLUMN_STATE = "state";
    }
}
