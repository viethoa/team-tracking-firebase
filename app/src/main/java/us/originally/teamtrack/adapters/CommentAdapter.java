package us.originally.teamtrack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorem_ipsum.utils.DeviceUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;
import us.originally.teamtrack.models.Comment;

/**
 * Created by VietHoa on 14/09/15.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mDataArray;

    public CommentAdapter(Context context, ArrayList<Comment> data) {
        this.mDataArray = data;
        this.mContext = context;
    }

    public void refreshDataChange(ArrayList<Comment> data) {
        this.mDataArray = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_user_icon)
        ImageView ivIconUser;
        @InjectView(R.id.tv_user_name)
        TextView tvUserName;
        @InjectView(R.id.tv_message)
        TextView tvUserMessage;
        @InjectView(R.id.tv_time_stamp)
        TextView tvTimeStamp;
        @InjectView(R.id.view_left)
        View vSpaceLeft;
        @InjectView(R.id.view_right)
        View vSpaceRight;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int iviewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (position < 0 || position >= mDataArray.size())
            return;

        Comment comment = mDataArray.get(position);
        String message = "";
        String user = "";
        String userUuid = "";
        String timeStamp = "";
        boolean isOnl = true;

        if (comment != null) {
            message = comment.message;
            isOnl = comment.user.state;

            timeStamp = convertToDateTime(comment.time_stamp);
            if (comment.user != null) {
                user = comment.user.name;
                userUuid = comment.user.device_uuid;
            }
        }

        viewHolder.tvUserName.setText(user);
        viewHolder.tvUserMessage.setText(message);
        viewHolder.tvTimeStamp.setText(timeStamp);

        String uuid = DeviceUtils.getDeviceUUID(mContext);
        boolean isMyComment = uuid.equals(userUuid);
        viewHolder.vSpaceLeft.setVisibility(isMyComment ? View.VISIBLE : View.GONE);
        viewHolder.vSpaceRight.setVisibility(isMyComment ? View.GONE : View.VISIBLE);
        viewHolder.tvUserName.setText(isMyComment ? "me" : user);
        viewHolder.ivIconUser.setImageResource(isOnl ? R.mipmap.ic_user_onl : R.mipmap.ic_user_off);
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    protected String convertToDateTime(long millisecond) {
        long today = System.currentTimeMillis();
        String dateFormat = "hh:mm";

        long day = Math.abs((today - millisecond) / (1000 * 60 * 60 * 24));
        if (day > 0)
            dateFormat = "MM/dd/yyyy hh:mm";

        return DateFormat.format(dateFormat, millisecond).toString();
    }
}
