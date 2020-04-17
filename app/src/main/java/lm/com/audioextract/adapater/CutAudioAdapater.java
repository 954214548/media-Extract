package lm.com.audioextract.adapater;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.R;
import lm.com.audioextract.model.AudioModel;
import lm.com.audioextract.utils.ExactorMediaUtils;
import lm.com.audioextract.utils.LogUtil;
import lm.com.audioextract.utils.MediaManager;
import lm.com.audioextract.utils.TimeFormat;


public class CutAudioAdapater extends RecyclerView.Adapter<CutAudioAdapater.ViewHolder> {

    private List<AudioModel> mList = new ArrayList<>();
    private Context mContext;
    private ItemOnClickListener onClickListener;
    private int playposition;


    public CutAudioAdapater(List<AudioModel> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public void setmList(List<AudioModel> mList) {
        this.mList = mList;
    }

    public void notifyData(){
        notifyDataSetChanged();
    }

    public ItemOnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ItemOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_adapater_layout, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String duation = TimeFormat.long2String(mList.get(position).getDuation());
        String size    = ExactorMediaUtils.getFormatSize(mList.get(position).getSize());
        holder.audioDuration.setText(String.format("%s",size ));
        holder.audioName.setText(mList.get(position).getName());
        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v,mList.get(position));
            }
        });
        holder.audioThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaManager.isPlaying()){
                    MediaManager.stop();
                    holder.audioThumb.setBackgroundResource(R.drawable.icon_audio);

                    if (playposition == position){
                        return;
                    }
                }
                holder.audioThumb.setBackgroundResource(R.drawable.anim_play_audio);
                final AnimationDrawable anim = (AnimationDrawable) holder.audioThumb.getBackground();
                anim.start();
                MediaManager.playSound(mContext, mList.get(position).getUrl(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.audioThumb.setBackgroundResource(R.drawable.anim_play_audio);
                        anim.stop();
                    }
                }, new AudioPlayError() {
                    @Override
                    public void PlayError(String messasge) {
                        anim.stop();
                        Toast.makeText(mContext, mContext.getString(R.string.play_error), Toast.LENGTH_SHORT).show();
                    }
                });
                playposition = position;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView audioName;
        private TextView audioDuration;
        private ImageButton actionBtn;
        private View audioThumb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            audioThumb      = itemView.findViewById(R.id.audioThumb);
            audioName       = itemView.findViewById(R.id.audioname);
            audioDuration   = itemView.findViewById(R.id.audioduration);
            actionBtn       = itemView.findViewById(R.id.right_imbtn);
        }
    }
}
