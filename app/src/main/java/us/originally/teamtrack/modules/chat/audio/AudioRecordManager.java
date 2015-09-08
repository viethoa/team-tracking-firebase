package us.originally.teamtrack.modules.chat.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import com.lorem_ipsum.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.AudioEvent;
import us.originally.teamtrack.models.AudioData;

/**
 * Created by VietHoa on 03/09/15.
 */
public class AudioRecordManager {

    private static final String LOG_TAG = "AudioRecordManager";

    private static AudioRecord mRecorder = null;
    private static AudioTrack mPlayer = null;

    private static int sampleRate = 8000; //44100;
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

    private static String uuid = null;
    private static int audioTimeStamp = 0;
    private static CMG711 uLawCodec = new CMG711();
    public static ArrayList<AudioModel> AudiosEndCoded;

    private static boolean isRecording = false;
    private static EventBus eventBus = new EventBus();

    public static boolean isRecording() {
        return mPlayer != null;
    }

    //**********************************************************************************************
    //  Player
    //**********************************************************************************************

    public static void startPlaying(List<AudioModel> AudiosEndCoded) {
        mPlayer = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize,
                AudioTrack.MODE_STREAM);
        mPlayer.play();

        Log.d(LOG_TAG, "AudioEndCode: " + AudiosEndCoded.size());
        for (AudioModel item : AudiosEndCoded) {
            byte[] audio = Base64.decode(item.encode, 2);

            //Decoding:
            byte[] byteArray = new byte[item.size * 2];
            uLawCodec.decode(audio, 0, item.size, byteArray);

            //Play
            mPlayer.write(byteArray, 0, item.size);
        }

        stopPlaying();
    }

    public static void stopPlaying() {
        if (mPlayer == null)
            return;

        mPlayer.release();
        mPlayer = null;
    }

    //**********************************************************************************************
    //  Recorder
    //**********************************************************************************************

    public static void startRecording(Context context) {
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
        mRecorder.startRecording();
        isRecording = true;

        audioTimeStamp = 0;
        if (uuid == null) {
            uuid = DeviceUtils.getDeviceUUID(context);
        }

        //Encoding:
        byte[] buffer = new byte[4096];
        byte[] outBuffer = new byte[4096];
        int size;

        //Audio byte store
        AudiosEndCoded = new ArrayList<>();

        //Capture audio
        while (mRecorder != null && isRecording) {
            audioTimeStamp += 1;
            size = mRecorder.read(buffer, 0, buffer.length);

            //Take audio waveform
            AudioData audioData = new AudioData(buffer, size);
            eventBus.getDefault().post(new AudioEvent(audioData));

            //uLaw Encoding:
            uLawCodec.encode(buffer, 0, size, outBuffer);

            //Base64 encoding
            String strEncoded = Base64.encodeToString(outBuffer, 2);

            //Capture audio
            AudioModel item = new AudioModel(strEncoded, size, String.valueOf(audioTimeStamp), uuid);
            AudiosEndCoded.add(item);
        }
    }

    public static ArrayList<AudioModel> stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
        }

        Log.d(LOG_TAG, "encode: stop");
        mRecorder = null;
        isRecording = false;

        return AudiosEndCoded;
    }
}
