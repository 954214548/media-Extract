package lm.com.audioextract.adapater;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.model.VideoModel;

public class VideoAdapater extends RecyclerView.Adapter<VideoAdapater.ViewHolder> {

    private List<VideoModel> mList = new ArrayList<>();
    @NonNull
    @Override
    public VideoAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapater.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
