package us.originally.teamtrack.controllers;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lorem_ipsum.activities.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import us.originally.teamtrack.EventBus.MessageEvent;
import us.originally.teamtrack.EventBus.VisualizeEvent;
import us.originally.teamtrack.R;
import us.originally.teamtrack.adapters.ChattingAdapter;
import us.originally.teamtrack.customviews.VisualizerView;
import us.originally.teamtrack.modules.chat.MessageModel;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.modules.chat.audio.AudioRecordManager;

public class AudioMessageActivity extends BaseActivity implements VisualizerView.VisualizerListener {

    private static final String CHANNEL_NAME = "temp_channel";
    protected LinearLayoutManager mLayoutManager;

    @InjectView(R.id.visualizer)
    VisualizerView mVisualizer;
    @InjectView(R.id.my_recycler_view)
    RecyclerView mRecycleView;

    protected EventBus eventBus;
    protected ChattingAdapter mAdapter;
    protected ArrayList<MessageModel> mDataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_recoding);
        ButterKnife.inject(this);
        eventBus = new EventBus();
        //FireBaseAction.registerEventListener(this, CHANNEL_NAME);

        initialiseData();
        initialiseUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.getDefault().unregister(this);
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {
        mDataArray = new ArrayList<MessageModel>();
        mAdapter = new ChattingAdapter(this, mDataArray);
    }

    protected void initialiseUI() {
        //Recycle view
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        //Audio Visualizer
        mVisualizer.setOnSpeakListener(this);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    public void onEventMainThread(MessageEvent event) {
        logDebug("message added");
        if (event == null || event.getMessageModel() == null)
            return;

        mDataArray.add(event.getMessageModel());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.refreshDataChange(mDataArray);
            }
        });
    }

    public void onEventMainThread(VisualizeEvent event) {
        if (event == null)
            return;

        logDebug("sound value: " + event.getSoudValue());
        mVisualizer.OnSpeaking(event.getSoudValue());
    }

    @Override
    public void onEntered() {
        if (AudioRecordManager.isRecording())
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioRecordManager.startRecording(AudioMessageActivity.this);
            }
        }).start();
    }

    @Override
    public void onLeaved() {
        ArrayList<AudioModel> audio = AudioRecordManager.stopRecording();
        if (audio == null || audio.size() <= 0) {
            logError("Record error problem");
            return;
        }

        Random random = new Random();
        int id = random.nextInt(1000000);
        MessageModel message = new MessageModel(id, null, audio);

        //FireBaseAction.pushMessage(this, CHANNEL_NAME, message);
    }
}
