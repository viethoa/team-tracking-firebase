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
import us.originally.teamtrack.models.AudioData;

/**
 * Created by VietHoa on 08/09/15.
 */
public class VisualizerView extends RelativeLayout {
    private static final String TAG = "Visualize";
    private static final int DUARATION = 200;

    protected VisualizerListener listener;

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

        listener = (VisualizerListener) context;
        btnSpeak.setOnTouchListener(new VisualizeTouchEvent());
    }

    public interface VisualizerListener {
        void onEntered();
        void onLeaved();
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    public void OnSpeaking(AudioData buffer) {

    }

    protected void animationSpeaking(float scale) {
        vAnimationSpeaking.setScaleX(0.5f);
        vAnimationSpeaking.setScaleY(0.5f);

        vAnimationSpeaking.animate().scaleX(scale).setDuration(DUARATION).start();
        vAnimationSpeaking.animate().scaleY(scale).setDuration(DUARATION).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDone();
            }
        }, DUARATION);
    }

    protected void animationDone() {
        vAnimationSpeaking.animate().scaleX(1f).setDuration(DUARATION).start();
        vAnimationSpeaking.animate().scaleY(1f).setDuration(DUARATION).start();
    }

    protected class VisualizeTouchEvent implements OnTouchListener {
        private static final int MIN_CLICK_DURATION = 100;
        private long startClickTime;
        private boolean isTouching;

        @Override
        public boolean onTouch(View view, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "touch down");
                    isTouching = false;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    break;

                case MotionEvent.ACTION_MOVE:
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if (clickDuration >= MIN_CLICK_DURATION && !isTouching) {
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
            Resources res = getContext().getResources();
            final int newColor = res.getColor(R.color.visualizer_dark);
            vBackground.setColorFilter(newColor);

            animationSpeaking(2f);
            if (listener != null) {
                listener.onEntered();
            }
        }

        protected void onTouchLeaved() {
            vBackground.setColorFilter(null);
            if (listener != null) {
                listener.onLeaved();
            }
        }
    }
}
