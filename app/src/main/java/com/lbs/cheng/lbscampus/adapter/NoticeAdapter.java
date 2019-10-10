package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lt.common.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.RCImageView;
import com.zhy.autolayout.AutoLinearLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by cheng on 2019/1/20.
 */

public class NoticeAdapter extends BaseQuickAdapter<NoticeDetailBean, BaseViewHolder> {
    Context context;
    public NoticeAdapter(int layoutResId, @Nullable List<NoticeDetailBean> data, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, NoticeDetailBean item) {
        String time;
        if(item.getPublishTime()!=null){
            Date date=item.getPublishTime();
            SimpleDateFormat simleDateFormat=new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            time=simleDateFormat.format(date);
        }else{
            time="暂无时间";
        }
//

        helper.addOnClickListener(R.id.item_notice_layout);
        helper.addOnLongClickListener(R.id.item_notice_layout);
        helper.addOnClickListener(R.id.verify_successs).addOnClickListener(R.id.verify_fail);

        //获取公告内的图片
//        if(item.getImageList() != null && item.getImageList().size() > 0){
//            String path = HttpUtil.HOME_PATH + HttpUtil.Image +"notice/"+ item.getImageList().get(0).getImagePath();
//            RCImageView imageView = helper.getView(R.id.item_notice_image);
//            imageView.setRadius(18);
//            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
//            GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
//        }
        //获取封面图片
        if(item.getPicturePath() != null ){
            String path = HttpUtil.HOME_PATH + HttpUtil.Image +"notice/"+ item.getPicturePath();
            RCImageView imageView = helper.getView(R.id.item_notice_image);
            imageView.setRadius(18);
//            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
            GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
        }


        helper.setText(R.id.item_notice_title,item.getTitle());
        helper.setText(R.id.item_notice_date,time);

    }
}
