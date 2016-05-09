package com.coffeinum.jarvis;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.coffeinum.jarvis.device.Device;
import com.coffeinum.jarvis.device.Dictionary;
import com.coffeinum.jarvis.model.DbConverter;
import com.coffeinum.jarvis.model.DeviceContract;
import com.coffeinum.jarvis.model.JarvisContentProvider;
import com.coffeinum.jarvis.util.JarvisBroadcastReceiver;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.skyfishjy.library.RippleBackground;

import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;


public class MainActivity extends AppCompatActivity implements AIListener {

    final AIConfiguration config = new AIConfiguration("4f24c29c52a64232bc5d2b6057cf93f3",
            "9ad76731-7552-47f8-a3ef-7f906f5fbf00",
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);
    ImageButton voiceControlButton;
    RippleBackground voiceRipple;
    AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadValues();

        aiService = AIService.getService(this, config);

        aiService.setListener(this);

        voiceControlButton = (ImageButton) findViewById(R.id.voice_control_btn);
        voiceControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JarvisBroadcastReceiver.class);
                sendBroadcast(intent);
                aiService.startListening();
            }
        });
        voiceRipple = (RippleBackground)findViewById(R.id.voice_ripple);
    }

    private void loadValues() {
        String[] projectionColumns =
                {
                        DeviceContract.DeviceEntry.COLUMN_NAME_SERVICE_ID,
                        DeviceContract.DeviceEntry.COLUMN_STATE,
                        DeviceContract.DeviceEntry.COLUMN_NAME,
                        DeviceContract.DeviceEntry._ID
                };

        Cursor query = getContentResolver().query(JarvisContentProvider.CONTENT_URI_DEVICES,
                projectionColumns,
                null,
                null,
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(AIResponse aiResponse) {
        Result result = aiResponse.getResult();
        Device device = new Device();
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                if (entry.getKey().equals(Dictionary.DEVICE_KEY)) {
                    device.type = entry.getValue().getAsString();
                } else if (entry.getKey().equals(Dictionary.STATE_KEY)) {
                    String value = entry.getValue().getAsString();
                    if (value.equals(Dictionary.STATE_OFF_KEY))
                        device.isTurnedOn = false;
                    else if (value.equals(Dictionary.STATE_ON_KEY))
                        device.isTurnedOn = true;
                }
            }
        }
        syncStateWithServer(device);
        //Convert to JSON
        String deviceState = (new GsonBuilder()).create().toJson(device);
        Log.d("DEVICE", deviceState);
        Toast.makeText(this, deviceState, Toast.LENGTH_SHORT).show();
        //NetworkConnectionTask.doInBackground( REQUEST);
    }

    private void syncStateWithServer(Device device) {
        ContentValues values = DbConverter.convertToContentValues(device);
        getContentResolver().update(JarvisContentProvider.CONTENT_URI_DEVICES, values, "name like \"" + device.type + "\"", null);
    }

    @Override
    public void onError(AIError aiError) {
        Toast.makeText(this, R.string.voice_error + aiError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAudioLevel(float v) {

    }

    @Override
    public void onListeningStarted() {
        voiceRipple.startRippleAnimation();
    }

    @Override
    public void onListeningCanceled() {
        voiceRipple.stopRippleAnimation();
    }

    @Override
    public void onListeningFinished() {
        voiceRipple.stopRippleAnimation();
    }
}
