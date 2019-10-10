package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.DepartmentDetailBean;
import com.lbs.cheng.lbscampus.bean.SectionBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.RCImageView;

import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by cheng on 2019/4/12.
 */

public class DepartmentAdapter extends BaseQuickAdapter<DepartmentDetailBean, BaseViewHolder> {
    Context context;
    public DepartmentAdapter(int layoutResId, @Nullable List<DepartmentDetailBean> data, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
    }
    @Override
    protected void convert(BaseViewHolder helper, DepartmentDetailBean item) {
        helper.addOnClickListener(R.id.item_department_layout);

        if(item.getPicturePath() != null){
            String path = HttpUtil.HOME_PATH + HttpUtil.Image +"department/"+ item.getPicturePath();
            RCImageView imageView = helper.getView(R.id.item_department_image);
            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
            GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
        }


        helper.setText(R.id.item_department_title,item.getName());
        if(item.getDescription()!=null){
            helper.setText(R.id.item_department_description,"简介："+item.getDescription());
        }


    }

}