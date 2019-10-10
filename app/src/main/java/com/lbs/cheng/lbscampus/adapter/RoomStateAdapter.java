package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.RoomStateBean;
import com.lbs.cheng.lbscampus.bean.TagBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;

import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by LT on 2019/5/26.
 */

public class RoomStateAdapter extends BaseQuickAdapter<RoomStateBean, BaseViewHolder> {
    Context context;
    public RoomStateAdapter(int layoutResId, @Nullable List<RoomStateBean> data, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
    }
    @Override
    protected void convert(BaseViewHolder helper, RoomStateBean item) {
        helper.addOnClickListener(R.id.item_room_state);

        TextView textView=helper.getView(R.id.item_room_tv);
        textView.setText(item.getRoomName());
        if(item.getState() == 0){
            textView.setBackgroundResource(R.drawable.checkbox_unselect);
        }else if(item.getState() == 1){
            textView.setBackgroundResource(R.drawable.checkbox_select);
        }

    }

}