package us.originally.teamtrack.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.originally.teamtrack.R;
import us.originally.teamtrack.models.Comment;

/**
 * Created by VietHoa on 14/09/15.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> mDataArray;

    public CommentAdapter(ArrayList<Comment> data) {
        this.mDataArray = data;
    }

    public void refreshDataChange(ArrayList<Comment> data) {
        this.mDataArray = data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.tv_user_name)
        TextView tvUserName;
        @InjectView(R.id.tv_message)
        TextView tvUserMessage;

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

        if (comment != null) {
            message = comment.message;
            if (comment.user != null)
                user = comment.user.name;
        }

        viewHolder.tvUserName.setText(user);
        viewHolder.tvUserMessage.setText(message);
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }
}
