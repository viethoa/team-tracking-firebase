package us.originally.teamtrack.customviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.modules.audio.AudioRecordManager;

/**
 * Created by VietHoa on 19/09/15.
 */
public class AudioMessageView extends RelativeLayout {

    private static final String TAG = "AudioMessageView";
    private static final int WAIT_TIME_FOR_SPEAK = 1000;
    private static final float MAX_AFFECTED_SIZE = 2f;
    private static final float MIN_AFFECTED_SIZE = 0.3f;
    private static final int DURATION = 600;

    protected AnimateListener AnimateListener;
    protected AudioMessageListener AudioListener;

    @InjectView(R.id.btn_speak)
    View mSpeakView;
    @InjectView(R.id.iv_back)
    View vBackToComment;
    @InjectView(R.id.iv_effect)
    View mEffectView;

    public AudioMessageView(Context context) {
        super(context);
        initialiseView(context);
    }

    public AudioMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public AudioMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AudioMessageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialiseView(context);
    }

    protected void initialiseView(Context context) {
        View.inflate(context, R.layout.layout_audio_message, this);
        ButterKnife.inject(this);

        initialiseData();
        initialiseUI();
    }

    public interface AnimateListener {
        void OnAudioBoxHidden();
    }

    public interface AudioMessageListener {
        void OnPushAudioMessage(ArrayList<AudioModel> audios);
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {

    }

    protected void initialiseUI() {
        //Speaker
        mSpeakView.setOnTouchListener(new AudioRecordTouchListener());

        //Animation
        mEffectView.post(new Runnable() {
            @Override
            public void run() {
                performEffectAnimate();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // Setting & Option
    //----------------------------------------------------------------------------------------------

    public void setAnimateListener(AnimateListener listener) {
        this.AnimateListener = listener;
    }

    public void setAudioListener(AudioMessageListener listener) {
        this.AudioListener = listener;
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.iv_back)
    protected void OnBackToMessage() {
        this.post(new Runnable() {
            @Override
            public void run() {
                hiddenSelf();
            }
        });
    }

    protected void hiddenSelf() {
        float height = this.getHeight();
        if (height <= 0)
            return;

        this.animate().translationY(height).setDuration(DURATION).start();
        if (AnimateListener == null)
            return;

        AnimateListener.OnAudioBoxHidden();
    }

    protected void performEffectAnimate() {
        AnimatorSet animator = new AnimatorSet();
        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(mEffectView, "scaleX", MIN_AFFECTED_SIZE);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(mEffectView, "scaleY", MIN_AFFECTED_SIZE);
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(mEffectView, "scaleX", MAX_AFFECTED_SIZE);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(mEffectView, "scaleY", MAX_AFFECTED_SIZE);

        animator.play(scaleInX).with(scaleInY).after(scaleOutX).after(scaleOutY);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                performEffectAnimate();
            }
        });

        animator.setDuration(DURATION);
        animator.start();
    }

    //----------------------------------------------------------------------------------------------
    // Audio record section
    //----------------------------------------------------------------------------------------------

    protected class AudioRecordTouchListener implements OnTouchListener {
        protected boolean isSpeakStopped;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSpeakStopped = false;
                    mEffectView.setVisibility(GONE);

                    Handler wait = new Handler();
                    wait.postDelayed(new WaitTimeToSpeak(), WAIT_TIME_FOR_SPEAK);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mEffectView.setVisibility(VISIBLE);
                    isSpeakStopped = true;
                    OnSpeakStopped();
                    break;
            }

            return true;
        }

        protected void OnStartSpeak () {
            if (AudioRecordManager.isRecording())
                return;
            new RecordAudioMessage().start();
        }

        protected void OnSpeakStopped() {
            ArrayList<AudioModel> audios = AudioRecordManager.stopRecording();
            if (audios == null || audios.size() <= 0) {
                Log.d(TAG, "Record error problem");
                return;
            }
            if (AudioListener == null)
                return;

            AudioListener.OnPushAudioMessage(audios);
        }

        protected class WaitTimeToSpeak implements Runnable {
            @Override
            public void run() {
                if (isSpeakStopped)
                    return;
                OnStartSpeak();
            }
        }

        protected class RecordAudioMessage extends Thread {
            @Override
            public void run() {
                AudioRecordManager.startRecording(getContext());
            }
        }
    }
}
