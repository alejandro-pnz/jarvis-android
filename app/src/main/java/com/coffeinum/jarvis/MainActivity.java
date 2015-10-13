package com.coffeinum.jarvis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;


public class MainActivity extends AppCompatActivity implements AIListener{

    Button voiceControlButton;
    AIService aiService;
    final AIConfiguration config = new AIConfiguration( "9ad76731-7552-47f8-a3ef-7f906f5fbf00",
            "4f24c29c52a64232bc5d2b6057cf93f3",
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aiService = AIService.getService(this, config);

        aiService.setListener(this);

        voiceControlButton = (Button)findViewById(R.id.voice_control_btn);
        voiceControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiService.startListening();
            }
        });
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
        Toast.makeText(this, aiResponse.getResult().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(AIError aiError) {
        Toast.makeText(this, R.string.voice_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAudioLevel(float v) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
