package us.originally.teamtrack.customviews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.adapters.CommentAdapter;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.Comment;

/**
 * Created by VietHoa on 10/09/15.
 */
public class ChattingView extends RelativeLayout implements
        AudioMessageView.AnimateListener, AudioMessageView.AudioMessageListener {

    private static final int LIMIT_TIME_TO_JOIN_COMMENT = 5;
    private static final int DURATION = 300;

    protected ChattingViewListener listener;
    protected ArrayList<Comment> mDataArray;
    protected CommentAdapter mAdapter;

    @InjectView(R.id.et_comment)
    EditText etComment;
    @InjectView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.iv_voice_message)
    View vVoiceMessage;
    @InjectView(R.id.audio_box)
    AudioMessageView mAudioBox;
    @InjectView(R.id.send_comment_box)
    View mCommentBox;

    public ChattingView(Context context) {
        super(context);
        initialiseView(context);
    }

    public ChattingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public ChattingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChattingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialiseView(context);
    }

    protected void initialiseView(Context context) {
        View.inflate(context, R.layout.layout_chatting_view, this);
        ButterKnife.inject(this);

        initialiseData();
        initialiseUI();
    }

    public interface ChattingViewListener {
        void onCloseChatBox();

        void onPushComment(String comment);

        void OnPushAudioMessage(ArrayList<AudioModel> audios);
    }

    //----------------------------------------------------------------------------------------------
    // Setting
    //----------------------------------------------------------------------------------------------

    public void setOnChattingListener(ChattingViewListener listener) {
        this.listener = listener;
    }

    protected void initialiseData() {
        mDataArray = new ArrayList<>();
        mAdapter = new CommentAdapter(getContext(), mDataArray);
    }

    protected void initialiseUI() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //Audio box
        mAudioBox.setAnimateListener(this);
        mAudioBox.setAudioListener(this);
        mAudioBox.post(new Runnable() {
            @Override
            public void run() {
                hideAudioMessageView(false);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // Option Event
    //----------------------------------------------------------------------------------------------

    public void pushComment(Comment comment) {
        if (comment == null)
            return;

        //Add new comment
        if (comment.audios != null && comment.audios.size() > 0) {
            mDataArray.add(comment);
        } else {
            handleAddNewMessage(comment);
        }
        mAdapter.refreshDataChange(mDataArray);

        //Scroll to bottom
        int bottomPosition = mAdapter.getItemCount() - 1;
        if (bottomPosition < 0)
            bottomPosition = 0;
        mRecyclerView.smoothScrollToPosition(bottomPosition);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @Override
    public void OnPushAudioMessage(ArrayList<AudioModel> audios) {
        if (audios == null || audios.size() <= 0)
            return;
        if (listener == null)
            return;
        listener.OnPushAudioMessage(audios);
    }

    @OnClick(R.id.iv_voice_message)
    protected void onBtnVoiceClicked() {
        showAudioMessageBox();
        Activity activity = (Activity) getContext();
        if (activity == null)
            return;

        DeviceUtils.hideKeyboard(activity);
    }

    @OnClick(R.id.header_box)
    protected void onHeaderClicked() {
        if (listener != null) {
            listener.onCloseChatBox();
        }
    }

    @OnClick(R.id.btn_close)
    protected void onBtnCloseClicked() {
        if (listener != null) {
            listener.onCloseChatBox();
        }
    }

    @OnClick(R.id.btn_comment)
    protected void onBtnCommentClicked() {
        String comment = etComment.getText().toString();
        if (StringUtils.isNull(comment))
            return;

        etComment.setText("");
        if (listener == null)
            return;

        listener.onPushComment(comment);
    }

    @Override
    public void OnAudioBoxHidden() {
        mCommentBox.animate().translationY(0f).setDuration(DURATION).start();
    }

    protected void hideAudioMessageView(boolean withAnimate) {
        float height = mAudioBox.getHeight();
        if (height <= 0)
            return;

        if (!withAnimate) {
            mAudioBox.setTranslationY(height);
            return;
        }

        mAudioBox.animate().translationY(height).setDuration(DURATION).start();
    }

    protected void showAudioMessageBox() {
        float height = mCommentBox.getHeight();
        if (height <= 0)
            return;

        mCommentBox.animate().translationY(height).setDuration(DURATION).start();
        mAudioBox.animate().translationY(0f).setDuration(DURATION).start();
    }

    protected void handleAddNewMessage(Comment comment) {
        if (comment == null || StringUtils.isNull(comment.message))
            return;

        //Don't have anything in no comment
        if (mDataArray == null || mDataArray.size() <= 0) {
            mDataArray = new ArrayList<>();
            mDataArray.add(comment);
            return;
        }

        //Just add new comment by another user
        int lastIndex = mDataArray.size() - 1;
        Comment lastComment = mDataArray.get(lastIndex);
        if (lastComment == null || lastComment.user == null || comment.user == null ||
                StringUtils.isNull(comment.user.device_uuid) || StringUtils.isNull(lastComment.user.device_uuid)) {
            mDataArray.add(comment);
            Collections.sort(mDataArray);
            return;
        }

        //Need to join comment by myself
        long minute = Math.abs((lastComment.time_stamp - comment.time_stamp) / (1000 * 60)) % 60;
        if (comment.user.device_uuid.equals(lastComment.user.device_uuid) && minute <= LIMIT_TIME_TO_JOIN_COMMENT) {
            if (StringUtils.isNull(lastComment.message))
                lastComment.message = "\n" + comment.message;
            else
                lastComment.message += "\n\n" + comment.message;
            Collections.sort(mDataArray);
            return;
        }

        mDataArray.add(comment);
        Collections.sort(mDataArray);
    }
}
