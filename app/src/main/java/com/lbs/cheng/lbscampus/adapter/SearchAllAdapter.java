package com.lbs.cheng.lbscampus.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lbs.cheng.lbscampus.R;
import com.lbs.cheng.lbscampus.util.SearchItem;

import java.util.List;

/**
 * Created by cheng on 2019/4/13.
 */

public class SearchAllAdapter extends BaseMultiItemQuickAdapter<SearchItem, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SearchAllAdapter(List<SearchItem> data) {
        super(data);
        addItemType(SearchItem.people, R.layout.item_people);
        addItemType(SearchItem.building, R.layout.item_building);
        addItemType(SearchItem.section, R.layout.item_department);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchItem item) {
        switch (item.getItemType()) {
            case SearchItem.people:
                helper.addOnClickListener(R.id.item_people_layout);
                break;
            case SearchItem.building:
                helper.addOnClickListener(R.id.building_item).addOnClickListener(R.id.item_building_go);
                break;
            case SearchItem.section:
                helper.addOnClickListener(R.id.item_department_layout);
                break;
        }
    }
}
