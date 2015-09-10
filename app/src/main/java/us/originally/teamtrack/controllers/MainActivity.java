package us.originally.teamtrack.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lorem_ipsum.utils.DeviceUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.controllers.base.MapBaseActivity;
import us.originally.teamtrack.customviews.ChattingView;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.managers.GPSTrackerManager;

public class MainActivity extends MapBaseActivity implements GPSTrackerManager.GPSListener,
        ChattingView.ChattingViewListener {

    private static final int DUARATION = 300;

    @InjectView(R.id.visualizer)
    VisualizerView mVisualiser;
    @InjectView(R.id.chatting_box)
    ChattingView mChattingBox;
    @InjectView(R.id.ll_footer)
    View mFooter;

    public static Intent getInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseData();
        initialiseUI();
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {

    }

    protected void initialiseUI() {
        //Chatting box
        mChattingBox.setOnChattingListener(this);
        float height = DeviceUtils.getDeviceScreenHeight(this);
        mChattingBox.setTranslationY(height);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_open_chat_box)
    protected void onBtnOpenChatBoxClicked() {
        float footerHeight = mFooter.getHeight();
        mFooter.animate()
                .translationY(footerHeight)
                .setDuration(DUARATION).start();

        mVisualiser.animate().alpha(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mVisualiser.setVisibility(View.GONE);
                    }
                })
                .start();

        mChattingBox.animate()
                .translationY(0)
                .setDuration(DUARATION).start();
    }

    @Override
    public void onCloseChatBox() {
        mFooter.animate()
                .translationY(0)
                .setDuration(DUARATION).start();

        mVisualiser.setVisibility(View.VISIBLE);
        mVisualiser.animate().alpha(1f).start();

        float height = DeviceUtils.getDeviceScreenHeight(this);
        mChattingBox.animate()
                .translationY(height)
                .setDuration(DUARATION).start();
    }

    @Override
    public void onLocationChange(double lat, double lng) {
        takeLocationWithCamera(lat, lng);
    }
}
