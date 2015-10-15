package com.coffeinum.jarvis.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.coffeinum.jarvis.com.coffeinum.jarvis.device.Device;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JarvisContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI_DEVICES = Uri.parse("content://com.coffeinum.jarvis.device/devices/");
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private ProjectDbHelper dbHelper;

    private static final int DEVICES = 0;
    private static final int DEVICES_ID = 1;

    private static String url = "https://api.parse.com/1/classes/Device";
    private static String POST = "POST";
    private static String GET = "GET";

    static {
        uriMatcher.addURI("com.coffeinum.jarvis.device", "devices", DEVICES);
        uriMatcher.addURI("com.coffeinum.jarvis.device", "devices/#", DEVICES_ID);
    }

    public JarvisContentProvider() {
    }

    public static Uri getUri(int id) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI_DEVICES, id);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //TODO
        return null;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new ProjectDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase writableDatabase = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case 0:
                break;
            case 1:
                selection = "_ID = " + uri.getLastPathSegment();
                break;
            default:
                break;
        }
        syncData();
        Cursor query = writableDatabase.query(
                DeviceContract.DeviceEntry.TABLE_NAME,
                projection, selection, selectionArgs,
                null,
                null,
                sortOrder
        );
        return query;
    }

    private void syncData() {
        new JsonTask(url, GET, null) {

            @Override
            protected String parseJson(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray results = json.getJSONArray("results");
                    Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT serviceid from device", null);
                    List<String> ids = new ArrayList<String>();
                    while (cursor.moveToNext()) {
                        String idss = cursor.getString(0);
                        ids.add(idss);
                    }
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = (JSONObject) results.get(i);
                        String type = result.getString("name");
                        String service_Id = result.getString("objectId");
                        String update_date = result.getString("updatedAt");
                        Boolean state = result.getBoolean("state");
                        Device device = new Device();
                        device.type = type;
                        device.externalId = service_Id;
                        device.updateDate = update_date;
                        device.isTurnedOn = state;
                        if (!ids.contains(service_Id)) {
                            dbHelper.getWritableDatabase().insert(DeviceContract.DeviceEntry.TABLE_NAME, null, DbConverter.convertToContentValues(device));
                        }
                    }

                    return "OK";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String result) {
                //Do nothing
            }

            @Override
            protected void onCancelled() {
                //Do nothong
            }
        }.execute();
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int update;
        switch (uriMatcher.match(uri)) {
            case DEVICES:
                update = dbHelper.getWritableDatabase().update(DeviceContract.DeviceEntry.TABLE_NAME, values, selection, selectionArgs);
                updateRestService(selection, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }

    private void updateRestService(String selection, ContentValues values) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT serviceid FROM  device where " + selection, null);
        if (true || cursor.moveToFirst()) {
//            String serviceId = cursor.getString(0);
            JSONObject json = new JSONObject();
            String updateUrl = url + "/" + "i0X22LO5o4";
            try {
                json.put("state", values.get(DeviceContract.DeviceEntry.COLUMN_STATE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new JsonTask(updateUrl, POST, json) {

                @Override
                protected String parseJson(String s) {
                    try {
                        JSONObject json = new JSONObject(s);
                        String name = json.getString("updatedAt");
                        return name;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected String convertToString(JSONObject json) {
                    return json.toString();
                }

                @Override
                protected void onPostExecute(String result) {
                    //Do nothing
                }

                @Override
                protected void onCancelled() {
                    //Do nothong
                }
            }.execute();
        }
    }
}
