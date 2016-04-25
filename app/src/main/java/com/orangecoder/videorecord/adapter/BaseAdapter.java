package com.orangecoder.videorecord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by apple on 12/10/15.
 */
public abstract class BaseAdapter<ItemType> extends android.widget.BaseAdapter {

    public Context mContext = null;
    private ArrayList<ItemType> mList = new ArrayList<ItemType>();
    private LayoutInflater mLayoutInflater = null;

    public BaseAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void add(ItemType item) {
        if (item == null) {
            return;
        }
        mList.add(item);
    }

    public void addAll(Collection<ItemType> clc) {
        if (clc == null) {
            return;
        }
        mList.addAll(clc);
    }

    public void update(Collection<ItemType> clc) {
        if (clc == null) {
            return;
        }
        mList.clear();
        mList.addAll(clc);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mList.clear();
    }

    public Context getContext() {
        return mContext;
    }

    public View inflate(int id) {
        return mLayoutInflater.inflate(id, null);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ItemType getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
