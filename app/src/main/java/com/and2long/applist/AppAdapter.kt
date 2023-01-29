package com.and2long.applist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.and2long.applist.databinding.ItemAppInfoBinding

class AppAdapter(private val dataList: List<AppInfo>) :
    RecyclerView.Adapter<AppAdapter.AppHolder>() {

    class AppHolder(private val itemBinding: ItemAppInfoBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(appInfo: AppInfo) {
            itemBinding.apply {
                ivIcon.setImageDrawable(appInfo.appIcon)
                tvName.text = appInfo.appName
                tvPackage.text = appInfo.appPackage
                tvVerName.text = appInfo.verName
            }
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        val itemBinding =
            ItemAppInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        holder.bind(dataList[position])
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(it, position) }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}