package us.originally.teamtrack.modules.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.VisualizeEvent;
import us.originally.teamtrack.models.AudioModel;

/**
 * Created by VietHoa on 03/09/15.
 */
public class AudioRecordManager {

    private static final String LOG_TAG = "AudioRecordManager";

    private static AudioRecord mRecorder = null;
    private static AudioTrack mPlayer = null;

    private static int sampleRate = 8000;
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private static int bufferSize = 4096;

    private static boolean isRecording = false;
    private static CMG711 uLawCodec = new CMG711();
    private static EventBus eventBus = new EventBus();
    public static ArrayList<AudioModel> AudiosEndCoded;
    private static AudioRecordListener mListener;

    public interface AudioRecordListener {
        void OnPlayStopped();
    }

    public static void setAudioRecordListener(AudioRecordListener listener) {
        mListener = listener;
    }

    //**********************************************************************************************
    //  Player
    //**********************************************************************************************

    public static boolean isPlaying() {
        return mPlayer != null;
    }

    public static void startPlaying(List<AudioModel> AudiosEndCoded) {
        mPlayer = new AudioTrack(android.media.AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                audioFormat,
                minBufSize,
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
        if (mListener == null)
            return;

        mListener.OnPlayStopped();
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

    public static boolean isRecording() {
        return mRecorder != null;
    }

    public static void startRecording(Context context) {
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufSize * 10);
        mRecorder.startRecording();

        isRecording = true;
        int audioTimeStamp = 0;

        //Encoding:
        Log.d(LOG_TAG, String.valueOf(minBufSize));
        byte[] buffer = new byte[minBufSize];
        byte[] outBuffer = new byte[minBufSize];
        int size;

        //Audio byte store
        AudiosEndCoded = new ArrayList<>();

        //Capture audio
        while (mRecorder != null && isRecording) {
            audioTimeStamp += 1;
            size = mRecorder.read(buffer, 0, buffer.length);

            //Take audio waveform
            float soundValue = calculatePowerDb(buffer, size);
            eventBus.getDefault().post(new VisualizeEvent(soundValue));

            //uLaw Encoding:
            uLawCodec.encode(buffer, 0, size, outBuffer);

            //Base64 encoding
            String strEncoded = Base64.encodeToString(outBuffer, 2);

            //Capture audio
            long timeStamp = System.currentTimeMillis();
            AudioModel item = new AudioModel(strEncoded, size, audioTimeStamp, timeStamp, null);
            AudiosEndCoded.add(item);
        }
    }

    protected static float calculatePowerDb(byte[] buffer, int readSize) {
        if (readSize <= 0)
            return 0;

        float max = 0;
        for (int i = 0; i < readSize; i+=2) {
            int intSample = (buffer[i+1] << 8) | (buffer[i]) & 0xFF;
            float floatSample = intSample / 32767.0f;
            floatSample = Math.abs(floatSample);
            max = Math.max(floatSample, max);
        }

        return max;
    }

    public static ArrayList<AudioModel> stopRecording() {
        isRecording = false;
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
        }

        mRecorder = null;
        Log.d(LOG_TAG, "encode: stop");

        return AudiosEndCoded;
    }
}
