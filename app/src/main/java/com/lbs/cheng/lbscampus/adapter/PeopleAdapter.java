package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.UserDetailBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.RCImageView;

import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by cheng on 2019/4/12.
 */

public class PeopleAdapter extends BaseQuickAdapter<UserDetailBean, BaseViewHolder> {
    Context context;
    public PeopleAdapter(int layoutResId, @Nullable List<UserDetailBean> data, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
    }
    @Override
    protected void convert(BaseViewHolder helper, UserDetailBean item) {
        helper.addOnClickListener(R.id.item_people_layout);

        if(item.getUserImage() != null){
            String path = HttpUtil.HOME_PATH + HttpUtil.Image + "user/"+item.getUserImage();
            RCImageView imageView = helper.getView(R.id.item_people_image);
            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
            GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
        }
        if(item.getTelNumber()!=null){
            helper.setText(R.id.item_people_tel,"电话："+item.getTelNumber());
        }
        if(item.getEmail()!=null){
            helper.setText(R.id.item_people_email,"邮箱："+item.getEmail());
        }
        helper.setText(R.id.item_people_name,item.getUserName());

    }

}