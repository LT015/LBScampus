package com.example.kcb;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    Context context;
    public MyAdapter(int layoutResId, @Nullable List<String> data, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
    }
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView textView=helper.getView(R.id.class_name_tv);
        textView.setText(item);

    }
}
