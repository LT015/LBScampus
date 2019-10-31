package com.lbs.cheng.lbscampus.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.activity.AddNoticeActivity;
import com.lbs.cheng.lbscampus.activity.LoginActivity;
import com.lbs.cheng.lbscampus.activity.NoticeActivity;
import com.lbs.cheng.lbscampus.adapter.NoticeAdapter;
import com.lbs.cheng.lbscampus.bean.NoticeBean;
import com.lbs.cheng.lbscampus.bean.NoticeDetailBean;
import com.lbs.cheng.lbscampus.bean.UserBean;
import com.lbs.cheng.lbscampus.util.CommonUtils;
import com.lt.common.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

/**
 * Created by cheng on 2019/4/26.
 */

public class DraftFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.draft_recycler_view)
    RecyclerView recyclerView;
    private View view;
    PopupWindow popupWindow;
    TextView popupCancle;
    TextView popupConfirm;
    TextView popupContent;
    Unbinder unbinder;
    public List<NoticeDetailBean> noticeList=new ArrayList<>();
    private int noticePosition = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_draft, container, false);
            initData();
            initView();
        }
        return view;
    }

    private void initData() {
        unbinder = ButterKnife.bind(this, view);

    }

    private void initView() {
        initRecyclerView();
    }
    private void initRecyclerView() {
        NoticeAdapter adapter = new NoticeAdapter(R.layout.item_draft,noticeList,getContext());
        adapter.openLoadAnimation(ALPHAIN);
        adapter.isFirstOnly(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                noticePosition=position;

                switch (view.getId()){
                    case R.id.item_notice_layout:
                        Intent toContent = new Intent(getActivity(), NoticeActivity.class);
                        String json = new Gson().toJson(noticeList.get(position));
                        toContent.putExtra("noticeDetail",json);
                        startActivity(toContent);
                        break;
                    case R.id.verify_successs:
                        showShadow("编辑草稿?",1);
                        break;
                    case R.id.verify_fail:
                        showShadow("删除草稿?",0);
                        break;
                }

            }
        });
    }
//    public void getNoticeData() {
//        List<NoticeDetailBean> list = new ArrayList<>();
//        for(int i=0;i<20;i++){
//            list.add(new NoticeDetailBean());
//        }
//
//    }
    public void getNoticeData() {
        UserBean user = DataSupport.findLast(UserBean.class);
        String url= HttpUtil.HOME_PATH + HttpUtil.GET_MY_NOTICE+"/publisher/"+user.getUserId()+"/status/0";
        HttpUtil.sendOkHttpGetRequest(url, new ArrayList<String>(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            final JSONArray jsonArray = new JSONArray(responseText);
                            noticeList = new Gson().fromJson(jsonArray.toString(),new TypeToken<List<NoticeDetailBean>>(){}.getType());

                            initRecyclerView();

                        }catch (JSONException e){

                            Toast.makeText(getActivity(), "获取信息失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });





            }
        });

    }

    public void deleteNotice(){

        int id = noticeList.get(noticePosition).getNoticeId();

        HttpUtil.sendOkHttpDeleteRequest( HttpUtil.HOME_PATH + HttpUtil.DELETE_NOTICE + "/" + id, ""+id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "请求失败，请检查网络!", Toast.LENGTH_SHORT).show();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNoticeData();
                        // progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


    }
    @Override
    public void onClick(View view) {

    }
    private void showShadow(String content, final int value) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopupWindow = inflater.inflate(R.layout.popup_add, null, false);//引入弹窗布局
        popupWindow = new PopupWindow(vPopupWindow, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        //设置背景透明
        addBackground();

        //引入依附的布局
        View parentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_add, null);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupConfirm = (vPopupWindow.findViewById(R.id.popup_add_confirm));
        popupCancle = (vPopupWindow.findViewById(R.id.popup_add_cancel));
        popupContent = (vPopupWindow.findViewById(R.id.popup_content));
        popupContent.setText(content);
        popupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (value){
                    case 1:
                        CommonUtils.noticeEditType = 1;
                        Intent intent = new Intent(getActivity(), AddNoticeActivity.class);
                        String json = new Gson().toJson(noticeList.get(noticePosition));
                        intent.putExtra("noticeDetail",json);
                        startActivity(intent);
                        popupWindow.dismiss();
                        break;
                    case 0:
                        deleteNotice();
                        popupWindow.dismiss();
                        break;
                }

            }
        });
        popupCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
    private void addBackground() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;//调节透明度
        getActivity().getWindow().setAttributes(lp);
        //dismiss时恢复原样
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getNoticeData();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            getNoticeData();
        }
    }
}

