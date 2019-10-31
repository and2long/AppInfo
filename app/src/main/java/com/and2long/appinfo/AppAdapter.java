package com.and2long.appinfo;

import android.content.Context;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 程序适配器
 */
public class AppAdapter extends CommonAdapter<AppInfo> {


    public AppAdapter(Context context, List<AppInfo> datas) {
        super(context, R.layout.item_app_info, datas);
    }

    @Override
    protected void convert(ViewHolder holder, AppInfo appInfo, int position) {
        holder.setImageDrawable(R.id.iv_icon, appInfo.getAppIcon());
        holder.setText(R.id.tv_name, appInfo.getAppName());
        holder.setText(R.id.tv_package, appInfo.getAppPackage());
        holder.setText(R.id.tv_ver_name, appInfo.getVerName());
    }
}
