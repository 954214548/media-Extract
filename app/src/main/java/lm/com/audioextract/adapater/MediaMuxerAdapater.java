package lm.com.audioextract.adapater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.R;
import lm.com.audioextract.model.VideoModel;
import lm.com.audioextract.utils.ImageUtils;
import lm.com.audioextract.view.CustomVideoView;

public class MediaMuxerAdapater extends RecyclerView.Adapter<MediaMuxerAdapater.ViewHolder> {

    private List<VideoModel> mList = new ArrayList<>();
    private Context mContext;
    private ItemOnClickListener onClickListener;

    public MediaMuxerAdapater(List<VideoModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MediaMuxerAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.media_adapater_layout, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MediaMuxerAdapater.ViewHolder holder, final int postion) {
        holder.playVV.setUp(mList.get(postion).getUrl(),mList.get(postion).getName());
        holder.playVV.thumbImageView.setImageBitmap(ImageUtils.getVideoThumbnail(mList.get(postion).getUrl()));
        holder.muxerVideoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v,mList.get(postion));
            }
        });
        holder.muxerVideoRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v,mList.get(postion));
            }
        });
        holder.muxerVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v,mList.get(postion));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<VideoModel> getmList() {
        return mList;
    }

    public void setmList(List<VideoModel> mList) {
        this.mList = mList;
    }

    public ItemOnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void notifyData(){
        notifyDataSetChanged();
    }
    public void setOnClickListener(ItemOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CustomVideoView playVV;
        public TextView muxerVideoTv;
        public RelativeLayout muxerVideoRl;
        public ImageView muxerVideoIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playVV           = itemView.findViewById(R.id.play_video_vpv);
            muxerVideoTv     = itemView.findViewById(R.id.muxer_video_tv);
            muxerVideoRl     = itemView.findViewById(R.id.muxer_video_rl);
            muxerVideoIV     = itemView.findViewById(R.id.muxer_video_iv);

        }
    }
}
