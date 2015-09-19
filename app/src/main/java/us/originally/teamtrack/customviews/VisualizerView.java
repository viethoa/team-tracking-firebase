package us.originally.teamtrack.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;

/**
 * Created by VietHoa on 08/09/15.
 */
public class VisualizerView extends RelativeLayout {
    private static final String TAG = "Visualize";

    private static final int WAIT_TIME_TO_SPEAK = 1000;
    private static final int DURATION = 200;
    private static final float MAX_SCALE = 2.5f;
    private static final float MIN_SCALE = 1.3f;
    private static final float NORMAL_SCALE = 1f;
    protected VisualizerListener listener;
    protected boolean isStopRecording;

    @InjectView(R.id.btn_speak)
    ImageView btnSpeak;
    @InjectView(R.id.view_background)
    ImageView vBackground;
    @InjectView(R.id.view_speak)
    View vAnimationSpeaking;

    public VisualizerView(Context context) {
        super(context);
        initialiseView(context);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialiseView(context);
    }

    protected void initialiseView(Context context) {
        View.inflate(context, R.layout.layout_visualize_view, this);
        ButterKnife.inject(this);

        btnSpeak.setOnTouchListener(new VisualizeTouchEvent());
    }

    public interface VisualizerListener {
        void onSpeaking();
        void onSpeakStopped();
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    public void setOnSpeakListener(VisualizerListener listener) {
        this.listener = listener;
    }

    public void OnSpeaking(float soundLevel) {
        if (isStopRecording)
            return;

        float scale = convertToScaleSize(soundLevel);
        vAnimationSpeaking.setScaleX(scale);
        vAnimationSpeaking.setScaleY(scale);
    }

    protected float convertToScaleSize(float soundValue) {
        if (soundValue == 0)
            return MIN_SCALE;
        if (soundValue == 1)
            return MAX_SCALE;

        float percent = (MAX_SCALE - MIN_SCALE) / 10;
        return (percent * soundValue * 10) + 1.3f;
    }

    protected void animationSpeaking(float scale, int duration) {
        vAnimationSpeaking.setScaleX(0.5f);
        vAnimationSpeaking.setScaleY(0.5f);
        vAnimationSpeaking.animate().scaleX(scale).setDuration(duration).start();
        vAnimationSpeaking.animate().scaleY(scale).setDuration(duration).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDone();
            }
        }, duration);
    }

    protected void animationDone() {
        vAnimationSpeaking.animate().scaleX(MIN_SCALE).setDuration(DURATION).start();
        vAnimationSpeaking.animate().scaleY(MIN_SCALE).setDuration(DURATION).start();
    }

    //----------------------------------------------------------------------------------------------
    // Touch AnimateListener
    //----------------------------------------------------------------------------------------------

    protected class VisualizeTouchEvent implements OnTouchListener {
        private static final int MIN_CLICK_DURATION = 100;
        private long startClickTime;
        private boolean isTouching;

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = false;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;

                case MotionEvent.ACTION_MOVE:
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration >= MIN_CLICK_DURATION && !isTouching) {
                        Log.d(TAG, "touch down");
                        onTouchEntered();
                        isTouching = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "touch leave");
                    isTouching = false;
                    onTouchLeaved();
                    break;
            }

            return true;
        }

        protected void onTouchEntered() {
            //Take background effect
            Resources res = getContext().getResources();
            final int newColor = res.getColor(R.color.visualizer_dark);
            vBackground.setColorFilter(newColor);

            //Animate for speak
            isStopRecording = false;
            animationSpeaking(2f, DURATION);

            //delay before start record
            Handler waiTime = new Handler();
            waiTime.postDelayed(new WaitTimeRunnable(), WAIT_TIME_TO_SPEAK);
        }

        protected void onTouchLeaved() {
            if (listener != null) {
                listener.onSpeakStopped();
            }

            isStopRecording = true;
            vBackground.setColorFilter(null);
            vAnimationSpeaking.animate().scaleX(NORMAL_SCALE).start();
            vAnimationSpeaking.animate().scaleY(NORMAL_SCALE).start();
        }

        protected class WaitTimeRunnable implements Runnable {
            @Override
            public void run() {
                if (listener == null)
                    return;

                listener.onSpeaking();
            }
        }
    }
}
