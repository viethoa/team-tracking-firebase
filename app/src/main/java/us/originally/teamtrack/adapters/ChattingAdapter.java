package us.originally.teamtrack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;
import us.originally.teamtrack.modules.chat.MessageModel;
import us.originally.teamtrack.modules.chat.audio.AudioRecordManager;

/**
 * Created by VietHoa on 05/09/15.
 */
public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {

    private ArrayList<MessageModel> mDataArray;
    private Context mContext;

    public ChattingAdapter(Context context, ArrayList<MessageModel> data) {
        this.mDataArray = data;
        this.mContext = context;
    }

    public void refreshDataChange(ArrayList<MessageModel> data) {
        this.mDataArray = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.btn_play)
        Button btnPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public ChattingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlayClicked(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    protected void onPlayClicked(ViewHolder holder, int position) {
        if (position < 0 || position >= mDataArray.size())
            return;

        MessageModel messageModel = mDataArray.get(position);
        if (messageModel == null || messageModel.audio == null)
            return;

        holder.btnPlay.setCompoundDrawables(mContext.getResources().getDrawable(R.mipmap.ic_pause), null, null, null);
        AudioRecordManager.startPlaying(messageModel.audio);

        holder.btnPlay.setCompoundDrawables(mContext.getResources().getDrawable(R.mipmap.ic_play), null, null, null);
    }
}
