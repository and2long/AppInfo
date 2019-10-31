package com.and2long.appinfo

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
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val TYPE_ALL = 0
        const val TYPE_USER = 1
        const val TYPE_SYSTEM = 2
    }

    private val TAG = this.javaClass.simpleName

    private lateinit var adapter: AppAdapter
    private val mData = mutableListOf<AppInfo>()

    private var type = TYPE_ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolBar.title = ""
        setSupportActionBar(toolBar)
        val spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.options_main, android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = this
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
    }

    override fun onResume() {
        super.onResume()
        showApps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.i_refresh -> showApps()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.i(TAG, "nothing select")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        type = position
        showApps()
        Log.i(TAG, "$position")
    }

    /**
     * 显示所有的用户程序
     */
    @SuppressLint("CheckResult")
    private fun showApps() {
        pb.visibility = View.VISIBLE
        Observable.fromCallable {
            val result = mutableListOf<AppInfo>()
            try {
                val packageInfoList = packageManager.getInstalledPackages(0)
                val temp = when (type) {
                    TYPE_SYSTEM -> {
                        packageInfoList.filter { (ApplicationInfo.FLAG_SYSTEM and it.applicationInfo.flags) != 0 }
                    }
                    TYPE_USER -> {
                        packageInfoList.filter { (ApplicationInfo.FLAG_SYSTEM and it.applicationInfo.flags) == 0 }
                    }
                    else -> {
                        packageInfoList
                    }
                }

                temp.forEach {
                    val myAppInfo = AppInfo()
                    myAppInfo.appName = packageManager.getApplicationLabel(it.applicationInfo).toString()
                    myAppInfo.appPackage = it.packageName
                    myAppInfo.verName = it.versionName
                    myAppInfo.appIcon = it.applicationInfo.loadIcon(packageManager)
                    result.add(myAppInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "获取应用包信息失败")
            }
            result.sortBy { it.appName }
            result
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
