package us.originally.teamtrack.modules.chat.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.VisualizeEvent;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.UserTeamModel;
import us.originally.teamtrack.modules.dagger.managers.UserManager;

/**
 * Created by VietHoa on 07/09/15.
 */
public class AudioStreamManager {
    private static final String LOG_TAG = "AudioStreamManager";

    private static AudioRecord mRecorder = null;
    private static AudioTrack mPlayer = null;

    private static int sampleRate = 8000; //44100;
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private static int bufferSize = 10240;

    private static CMG711 uLawCodec = new CMG711();
    private static EventBus eventBus = new EventBus();
    private static int audio_limit_id = 50;

    //**********************************************************************************************
    //  Player
    //**********************************************************************************************

    protected static void initialisePlayer() {
        mPlayer = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize,
                AudioTrack.MODE_STREAM);
        mPlayer.play();
    }

    public static void startPlaying(final AudioModel audioModel) {
        if (mPlayer == null) {
            initialisePlayer();
        }

        //Base 64 decode
        byte[] audio = Base64.decode(audioModel.encode, 2);

        //uLaw Decoding
        final byte[] byteArray = new byte[audioModel.size * 2];
        uLawCodec.decode(audio, 0, audioModel.size, byteArray);

        //Play
        final byte[] buffer = byteArray;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mPlayer) {
                    mPlayer.write(buffer, 0, audioModel.size);
                }
            }
        }).start();
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
        return mPlayer != null;
    }

    public static void startRecording(UserTeamModel user, UserManager userManager) {
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
        mRecorder.startRecording();
        int audioId = 0;

        //Encoding:
        byte[] buffer = new byte[bufferSize];
        byte[] outBuffer = new byte[bufferSize];
        int size;

        //Capture audio
        while (mRecorder != null) {
            size = mRecorder.read(buffer, 0, buffer.length);

            //Take audio waveform
            float soundValue = calculatePowerDb(buffer, size);
            eventBus.getDefault().post(new VisualizeEvent(soundValue));

            //uLaw Encoding:
            uLawCodec.encode(buffer, 0, size, outBuffer);

            //Base64 encoding
            String strEncoded = Base64.encodeToString(outBuffer, 2);

            //Stream audio data
            audioId += 1;
            if (audioId > audio_limit_id) {
                audioId = 1;
            }
            long timeStamp = System.currentTimeMillis();
            AudioModel audio = new AudioModel(strEncoded, size, audioId, timeStamp, user);
            userManager.pushAudio(audio);
        }
    }

    public static void stopRecording() {
        if (mRecorder == null)
            return;

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        Log.d(LOG_TAG, "encode: stop");
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
}
