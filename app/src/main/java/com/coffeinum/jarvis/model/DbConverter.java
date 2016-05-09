package com.coffeinum.jarvis.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.coffeinum.jarvis.device.Device;

public class DbConverter {

    public static ContentValues convertToContentValues(Device device) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeviceContract.DeviceEntry.COLUMN_NAME_SERVICE_ID, device.externalId);
        contentValues.put(DeviceContract.DeviceEntry.COLUMN_NAME, device.type);
        contentValues.put(DeviceContract.DeviceEntry.COLUMN_STATE, device.isTurnedOn);
        return contentValues;
    }

    public static Device convertToDevice(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DeviceContract.DeviceEntry._ID));
        String serviceId = cursor.getString(cursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_NAME_SERVICE_ID));
        String title = cursor.getString(cursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_NAME));
        Integer state = cursor.getInt(cursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_STATE));
        String updateDate = cursor.getString(cursor.getColumnIndex(DeviceContract.DeviceEntry.COLUMN_UPDATE_DATE));
        Device device = new Device();
        device.externalId = serviceId;
        device.id = id;
        device.type = title;
        device.isTurnedOn = getBooleanFromInt(state);
        device.updateDate = updateDate;
        return device;
    }

    private static boolean getBooleanFromInt(Integer state) {
        if (state == null) {
            return false;
        }
        if (state.equals(1)) {
            return true;
        }
        return false;
    }
}
