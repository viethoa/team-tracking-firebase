package us.originally.teamtrack.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.modules.chat.audio.AudioStreamManager;
import us.originally.teamtrack.services.AudioService;

public class AudioStreamActivity extends AppCompatActivity {

    public static final String AUDIO_CHANNEL = "audio_channel";
    public static boolean isAudioStream;

    @InjectView(R.id.btn_speak)
    Button btnSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_stream);
        ButterKnife.inject(this);
        //FireBaseAction.registerEventListener(this, AUDIO_CHANNEL);

        initialiseData();

        Intent intent = new Intent(this, AudioService.class);
        startService(intent);
    }

    protected void initialiseData() {
        isAudioStream = false;
    }

    @OnClick(R.id.btn_speak)
    protected void onBtnSpeakClicked() {
        if (isAudioStream) {
            btnSpeak.setText(R.string.str_speak);
            AudioStreamManager.stopRecording();
            isAudioStream = false;
            return;
        }

        isAudioStream = true;
        btnSpeak.setText(R.string.str_recording);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //AudioStreamManager.startRecording(AudioStreamActivity.this);
            }
        }).start();
    }


}
