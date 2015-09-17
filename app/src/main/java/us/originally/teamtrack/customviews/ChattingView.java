package us.originally.teamtrack.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lorem_ipsum.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.originally.teamtrack.R;
import us.originally.teamtrack.adapters.CommentAdapter;
import us.originally.teamtrack.models.Comment;

/**
 * Created by VietHoa on 10/09/15.
 */
public class ChattingView extends RelativeLayout {

    private static final int LIMIT_TIME_TO_JOIN_COMMENT = 5;

    protected ChattingViewListener listener;
    protected ArrayList<Comment> mDataArray;
    protected CommentAdapter mAdapter;

    @InjectView(R.id.et_comment)
    EditText etComment;
    @InjectView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

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
    }

    //----------------------------------------------------------------------------------------------
    // Option Event
    //----------------------------------------------------------------------------------------------

    public void pushComment(Comment comment) {
        //Add new comment
        handleAddNewMessage(comment);
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

    protected void handleAddNewMessage(Comment comment) {
        if (comment == null || StringUtils.isNull(comment.message))
            return;

        if (mDataArray == null || mDataArray.size() <= 0) {
            mDataArray = new ArrayList<>();
            mDataArray.add(comment);
            return;
        }

        int lastIndex = mDataArray.size() - 1;
        Comment lastComment = mDataArray.get(lastIndex);
        if (lastComment == null || lastComment.user == null || comment.user == null ||
                StringUtils.isNull(comment.user.device_uuid) || StringUtils.isNull(lastComment.user.device_uuid)) {
            mDataArray.add(comment);
            Collections.sort(mDataArray);
            return;
        }

        long minute = Math.abs((lastComment.time_stamp - comment.time_stamp) / (1000 * 60)) % 60;
        if (comment.user.device_uuid.equals(lastComment.user.device_uuid) && minute <= LIMIT_TIME_TO_JOIN_COMMENT) {
            lastComment.message += "\n\n" + comment.message;
            Collections.sort(mDataArray);
            return;
        }

        mDataArray.add(comment);
        Collections.sort(mDataArray);
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
}
