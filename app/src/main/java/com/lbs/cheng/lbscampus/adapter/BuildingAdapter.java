package com.lbs.cheng.lbscampus.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.bean.BuildingBean;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.util.GlideUtil;
import com.lbs.cheng.lbscampus.util.HttpUtil;
import com.lbs.cheng.lbscampus.view.RCImageView;

import java.util.List;

import static com.lbs.cheng.lbscampus.MyApplication.getContext;

/**
 * Created by cheng on 2019/2/21.
 */

public class BuildingAdapter extends BaseQuickAdapter<BuildingBean, BaseViewHolder> {
    Context context;
    int flag = 0;//0表示显示导航按钮
    public BuildingAdapter(int layoutResId, @Nullable List<BuildingBean> data,int flag, Context itemContext) {
        super(layoutResId, data);
        context=itemContext;
        this.flag = flag;
    }
    @Override
    protected void convert(BaseViewHolder helper, BuildingBean item) {
        helper.addOnClickListener(R.id.item_building_go).addOnClickListener(R.id.building_item);

        if(item.getPicturePath() != null){
            String path = HttpUtil.HOME_PATH + HttpUtil.Image + item.getPicturePath();
            RCImageView imageView = helper.getView(R.id.item_building_image);
            GlideUtil.REQUEST_OPTIONS.signature(new ObjectKey(System.currentTimeMillis()));//签名  用以重新获取图片
            GlideUtil.load(context, path, imageView, GlideUtil.REQUEST_OPTIONS);
        }

        if(flag == 1){
            ImageView imageView = helper.getView(R.id.item_building_go);
            imageView.setVisibility(View.GONE);
        }

        helper.setText(R.id.item_building_name,item.getName());

    }
}
