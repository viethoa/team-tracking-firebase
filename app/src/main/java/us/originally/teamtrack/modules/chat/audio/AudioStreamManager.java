package us.originally.teamtrack.modules.chat.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import us.originally.teamtrack.controllers.AudioStreamActivity;
import us.originally.teamtrack.modules.firebase.FireBaseAction;

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

    private static CMG711 uLawCodec = new CMG711();

    //**********************************************************************************************
    //  Player
    //**********************************************************************************************

    protected static void initialisePlayer() {
        mPlayer = new AudioTrack(android.media.AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize,
                AudioTrack.MODE_STREAM);
        mPlayer.play();
    }

    public static void startPlaying(AudioModel audioModel) {
        if (mPlayer == null) {
            initialisePlayer();
        }

        //Base 64 decode
        byte[] audio = Base64.decode(audioModel.encode, 2);

        //uLaw Decoding
        byte[] byteArray = new byte[audioModel.size * 2];
        uLawCodec.decode(audio, 0, audioModel.size, byteArray);

        //Play
        mPlayer.write(byteArray, 0, audioModel.size);
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

        //Encoding:
        byte[] buffer = new byte[4096];
        byte[] outBuffer = new byte[4096];
        int size;

        //Capture audio
        while (mRecorder != null) {
            size = mRecorder.read(buffer, 0, buffer.length);

            //uLaw Encoding:
            uLawCodec.encode(buffer, 0, size, outBuffer);

            //Base64 encoding
            String strEncoded = Base64.encodeToString(outBuffer, 2);

            //Stream audio data
            AudioModel audio = new AudioModel(strEncoded, size);
            FireBaseAction.pushAudio(context, AudioStreamActivity.AUDIO_CHANNEL, audio);
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
}
