package com.and2long.deleteapps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var adapter: AppAdapter
    private val mData = mutableListOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = AppAdapter(this, mData)

        app_list.adapter = adapter
        app_list.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, RecyclerView.VERTICAL))
        adapter.setOnItemClickListener(object : MultiItemTypeAdapter.OnItemClickListener {
            override fun onItemLongClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int): Boolean {
                return false
            }

            override fun onItemClick(view: View?, holder: RecyclerView.ViewHolder?, position: Int) {
                deleteApp(mData[position].appPackage)
            }
        })

        pb.visibility = View.GONE

        showAllUserApps()
    }

    override fun onResume() {
        super.onResume()
        showAllUserApps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.i_refresh -> showAllUserApps()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * 显示所有的用户程序
     */
    @SuppressLint("CheckResult")
    private fun showAllUserApps() {
        pb.visibility = View.VISIBLE
        Observable.fromCallable {
            val myAppInfos = mutableListOf<AppInfo>()
            try {
                val packageInfos = packageManager.getInstalledPackages(0)
                for (i in packageInfos.indices) {
                    val packageInfo = packageInfos[i]
                    //过滤掉系统app
                    if ((ApplicationInfo.FLAG_SYSTEM and packageInfo.applicationInfo.flags) != 0) {
                        continue
                    }
                    //过滤掉本程序
                    if (packageInfo.packageName == packageName) {
                        continue
                    }
                    val myAppInfo = AppInfo()
                    myAppInfo.appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
                    myAppInfo.appPackage = packageInfo.packageName
                    myAppInfo.verName = packageInfo.versionName
                    if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                        continue
                    }
                    myAppInfo.appIcon = packageInfo.applicationInfo.loadIcon(packageManager)
                    myAppInfos.add(myAppInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "获取应用包信息失败")
            }

            myAppInfos
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mData.clear()
                    mData.addAll(t)
                    adapter.notifyDataSetChanged()
                    pb.visibility = View.INVISIBLE
                }, { t ->
                    t.printStackTrace()
                    pb.visibility = View.INVISIBLE
                })
    }


    /**
     * 删除程序
     */
    private fun deleteApp(packageName: String) {
        try {//通过程序的包名创建URI
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
