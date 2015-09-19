package us.originally.teamtrack.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorem_ipsum.utils.DeviceUtils;
import com.lorem_ipsum.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;
import us.originally.teamtrack.models.AudioModel;
import us.originally.teamtrack.models.Comment;
import us.originally.teamtrack.modules.audio.AudioRecordManager;

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
        @InjectView(R.id.btn_play)
        Button btnPlay;

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
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (position < 0 || position >= mDataArray.size())
            return;

        //Model------------------------------------------------
        final Comment comment = mDataArray.get(position);
        List<AudioModel> audios = null;
        String message = "";
        String user = "";
        String userUuid = "";
        String timeStamp = "";

        if (comment != null) {
            message = comment.message;
            audios = comment.audios;
            timeStamp = convertToDateTime(comment.time_stamp);
            if (comment.user != null) {
                user = comment.user.name;
                userUuid = comment.user.device_uuid;
            }
        }

        //View--------------------------------------------------
        viewHolder.tvUserName.setText(user);
        viewHolder.tvUserMessage.setText(message);
        viewHolder.tvTimeStamp.setText(timeStamp);

        //Setup my comment
        String uuid = DeviceUtils.getDeviceUUID(mContext);
        boolean isMyComment = uuid.equals(userUuid);
        viewHolder.vSpaceLeft.setVisibility(isMyComment ? View.VISIBLE : View.GONE);
        viewHolder.vSpaceRight.setVisibility(isMyComment ? View.GONE : View.VISIBLE);
        viewHolder.tvUserName.setText(isMyComment ? "me" : user);
        boolean isShowAudioPlayer = (audios != null && audios.size() > 0);
        viewHolder.btnPlay.setVisibility(isShowAudioPlayer ? View.VISIBLE : View.GONE);
        boolean isShowMessage = StringUtils.isNotNull(message);
        viewHolder.tvUserMessage.setVisibility(isShowMessage ? View.VISIBLE : View.GONE);

        //Play audio
        viewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.audios == null || comment.audios.size() <= 0)
                    return;
                OnPlayAudioMessage(viewHolder, comment.audios);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    protected void OnPlayAudioMessage(ViewHolder holder, List<AudioModel> audios) {
        if (AudioRecordManager.isPlaying())
            return;

        holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(
                mContext.getResources().getDrawable(R.mipmap.ic_pause), null, null, null);

        AudioRecordManager.setAudioRecordListener(new AudioListener(holder));
        Thread player = new AudioPlay(audios);
        player.start();
    }

    protected class AudioPlay extends Thread {
        private List<AudioModel> audios;

        public AudioPlay(List<AudioModel> audios) {
            this.audios = audios;
        }

        @Override
        public void run() {
            AudioRecordManager.startPlaying(audios);
        }
    }

    protected class AudioListener implements AudioRecordManager.AudioRecordListener {
        private ViewHolder holder;

        public AudioListener(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void OnPlayStopped() {
            AudioRecordManager.setAudioRecordListener(null);
            Activity activity = (Activity) mContext;
            if (activity == null)
                return;

            updatePlayIcon(activity);
        }

        protected void updatePlayIcon(Activity activity) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(
                            mContext.getResources().getDrawable(R.mipmap.ic_play), null, null, null);
                }
            });
        }
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
