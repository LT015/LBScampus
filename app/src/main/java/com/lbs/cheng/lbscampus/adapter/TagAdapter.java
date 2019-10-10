package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.TagBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;

import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by LT on 2019/5/13.
 */

public class TagAdapter extends BaseQuickAdapter<TagBean, BaseViewHolder> {
    Context context;
    List<Integer> list;
    public TagAdapter(int layoutResId, @Nullable List<TagBean> data,List<Integer> list, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
        this.list=list;
    }
    @Override
    protected void convert(BaseViewHolder helper, TagBean item) {
        helper.addOnClickListener(R.id.item_interest);

        TextView textView=helper.getView(R.id.item_interst_tv);
        textView.setText(item.getName());
        if(list.contains(item.getTagId())){
            textView.setBackgroundResource(R.drawable.checkbox_select);
        }

    }

}