package lm.com.audioextract.adapater;

import android.view.View;

import lm.com.audioextract.model.VideoModel;

public interface ItemOnClickListener {

    void OnClick(View view,Object object);
    void OnClickModel(View view, VideoModel model);

}
