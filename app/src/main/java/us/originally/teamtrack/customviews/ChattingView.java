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
        mAdapter = new CommentAdapter(mDataArray);
    }

    protected void initialiseUI() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    //----------------------------------------------------------------------------------------------
    // Option Event
    //----------------------------------------------------------------------------------------------

    public void pushComment(Comment comment) {
        if (comment == null || StringUtils.isNull(comment.message))
            return;

        if (mDataArray == null) {
            mDataArray = new ArrayList<>();
        }

        mDataArray.add(comment);
        mAdapter.refreshDataChange(mDataArray);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

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
