package com.vrgsoft.reddittoppub.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.vrgsoft.reddittoppub.R
import com.vrgsoft.reddittoppub.databinding.ActivityPublicationBinding
import com.vrgsoft.reddittoppub.util.JSONParser
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.min


private var titleList = mutableListOf<PublicationViewModel>()
private var paginationTitleList = mutableListOf<PublicationViewModel>()
private lateinit var viewModel: PublicationViewModel
private var isDataDowload: Boolean = false

class PublicationActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    private val TAG: String = "PublicationActivity"

    private val REDDIT_URL = "https://www.reddit.com/top.json"

    private lateinit var dataBinding: ActivityPublicationBinding
    private lateinit var adapter: PublicationAdapter
    private lateinit var loader: View

    private var handler: Handler = Handler()
    private val DOWNLOAD_SUCCESS: Int = 0
    private val DOWNLOAD_ERROR: Int = -1

    private lateinit var sharePref: SharedPreferences
    private val SETTINGS = "settings"
    private val PUBLICATION_ID = "publication_id"
    private var publicID = ""

    private var visibleItemCountL: Int = 0
    private var visibleLastIndexL: Int = 0
    private val paginationCount: Int = 8
    private var firstVisibleItemL: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_publication)

        // загрузчик (пагинация)
        loader = layoutInflater.inflate(R.layout.loader, null)
        // загрузчик
        if (!isDataDowload) {
            dataBinding.mainProgress.visibility = View.VISIBLE
        }

        // ListView
        dataBinding.lvList.addFooterView(loader)
        dataBinding.lvList.setOnScrollListener(this)

        // картинка в большом формате
        val onItemClick: (id: Int) -> Unit = {
            viewModel.itemClicked(this@PublicationActivity, paginationTitleList[it].getThumbnail())
        }
        // сохранение картинки
        val onItemLongClick: (id: Int) -> Unit = {
            viewModel.itemLongClicked(this@PublicationActivity, paginationTitleList[it].getBitmapThumbnail())
        }

        // Adapter
        adapter = PublicationAdapter(applicationContext, onItemClick, onItemLongClick)
        adapter.item = paginationTitleList
        dataBinding.lvList.adapter = adapter

        // настройки первоначальной верхней позиции списка
        sharePref = applicationContext.getSharedPreferences(SETTINGS, MODE_PRIVATE)
        publicID = sharePref.getString(PUBLICATION_ID, "").toString()

        Log.i(TAG, "lvList.firstVisiblePosition - " + dataBinding.lvList.lastVisiblePosition)

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                //super.handleMessage(msg)
                if (msg.what == DOWNLOAD_SUCCESS) {
                    isDataDowload = true

                    for (i in 0 until titleList.size) {
                        if (titleList[i].getID() == publicID) {
                            firstVisibleItemL = i
                            break
                        }
                    }

                    loadPublicData(firstVisibleItemL)

                    adapter.item = paginationTitleList
                    dataBinding.lvList.adapter = adapter
                    dataBinding.lvList.requestFocusFromTouch()
                    dataBinding.lvList.setSelection(firstVisibleItemL)
                    dataBinding.mainProgress.visibility = View.GONE

                    Log.i(TAG, "firstVisibleItemL - $firstVisibleItemL - " + titleList[firstVisibleItemL].getAuthor())
                    Log.i(TAG, "JSON Download Success")
                } else {
                    Log.e(TAG, "JSON Download Error")
                }
            }
        }

        if (titleList.isEmpty()) {
            getPublications()
        }
    }

    override fun onStop() {
        super.onStop()

        var id = ""
        if (paginationTitleList.size > 0) {
            id = paginationTitleList[firstVisibleItemL].getID()
        }

        sharePref = applicationContext.getSharedPreferences(SETTINGS, MODE_PRIVATE)
        var edit = sharePref.edit()
        edit.putString(PUBLICATION_ID, id)
        edit.apply()

        Log.i(TAG, "onStop - id = $id")
    }


    private fun loadPublicData(lastIndex: Int) {
        val newLastIndex = min(titleList.size - 1, lastIndex + paginationCount)

        for (i in paginationTitleList.size..newLastIndex) {
            paginationTitleList.add(i, titleList[i])
        }
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        visibleItemCountL = visibleItemCount
        visibleLastIndexL = firstVisibleItem + visibleItemCount - 1

        firstVisibleItemL = firstVisibleItem
    }

    override fun onScrollStateChanged(p0: AbsListView?, scrollState: Int) {
        val itemsLastIndex = adapter.count - 1
        val lastIndex = itemsLastIndex + 1

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndexL == lastIndex && titleList.size != lastIndex) {
            loadPublicData(lastIndex)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getPublications() {
        thread {
            Log.i(TAG, "===================CREATE THREAD===================")
            if (titleList.size == 0) {
                try {
                    titleList = JSONParser.getDataFromJSON(REDDIT_URL)
                    viewModel = titleList[0]
                } catch (e: Exception) {
                    println(e.message)
                    handler.sendEmptyMessage(DOWNLOAD_ERROR)
                }
                handler.sendEmptyMessage(DOWNLOAD_SUCCESS)
            }
        }
    }
}