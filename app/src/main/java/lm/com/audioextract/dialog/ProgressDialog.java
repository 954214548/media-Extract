package lm.com.audioextract.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.TextView;

import lm.com.audioextract.R;

public class ProgressDialog extends Dialog {

    private ProgressBar mProgressBar;
    private Context mContext;
    private TextView mTextView;
    private String title;

    public ProgressDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog_layout);
        title = mContext.getString(R.string.string_extract);
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setMax(100);

        mTextView = findViewById(R.id.dialog_title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressLint("SetTextI18n")
    public void setProgressBarValue(int value){
        mTextView.setText(String.format("%s  %s",title,value)+"%");
        mProgressBar.setProgress(value);
    }



}
