package com.vrgsoft.reddittoppub.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.vrgsoft.reddittoppub.R
import com.vrgsoft.reddittoppub.databinding.ActivityPublicationBinding
import com.vrgsoft.reddittoppub.model.Publication
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel
import kotlin.math.min


var titleList = mutableListOf<PublicationViewModel>()
lateinit var viewModel: PublicationViewModel

class PublicationActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    var result: String = ""
    var jsonArray: JSONArray? = null
    lateinit var lvList: ListView
    lateinit var mainProgress: ProgressBar
    //var titleList = mutableListOf<PublicationViewModel>()
    //var dataBinding: PublicListLayoutBinding? = null
    var dataBinding: ActivityPublicationBinding? = null
    lateinit var adapter: PublicationAdapter
    var handler: Handler = Handler()
    private val DOWNLOAD_SUCCESS: Int = 0
    private val DOWNLOAD_ERROR: Int = -1
    private val TAG: String = "PublicationActivity"
    private var isDataDowload: Boolean = false
    //lateinit var viewModel: PublicationViewModel
    private lateinit var defaultPicture: Bitmap

    private var visibleItemCountL: Int = 0
    private var visibleLastIndexL: Int = 0
    private val paginationCount: Int = 4
    private var currTitle: Int = 0

    private lateinit var loader: View
    private lateinit var prLoader: ProgressBar

    private var paginationTitleList = mutableListOf<PublicationViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)

        loader = layoutInflater.inflate(R.layout.loader, null)
        prLoader = loader.findViewById(R.id.prLoader)


        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_publication)
        mainProgress = findViewById(R.id.mainProgress)
        if (!isDataDowload) {
            mainProgress.visibility = View.VISIBLE
        }

        lvList = findViewById(R.id.lvList)

        lvList.addFooterView(loader)

        lvList.setOnScrollListener(this)

        defaultPicture =
            BitmapFactory.decodeResource(resources, R.drawable.no_image_available);

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                //super.handleMessage(msg)
                if (msg.what == DOWNLOAD_SUCCESS) {
                    isDataDowload = true

                    loadPublicData(0)

                    adapter.item = paginationTitleList

                    //TODO("Реализовать пагинацию страницы. Метод для разделения titleList на части.")

                    dataBinding?.lvList?.adapter = adapter
                    mainProgress.visibility = View.GONE
                    Log.i(TAG, "JSON Download Success")
                } else {
                    Log.e(TAG, "JSON Download Error")
                }
            }
        }

        Log.i(TAG, "----------OnCreate----------")
        Log.i(TAG, "titleList size = ${titleList.size} is empty = ${titleList.isEmpty()} isDataDownload = $isDataDowload")

        if (titleList.isEmpty()) {
            getPublications()
        }
    }

    fun loadPublicData(lastIndex: Int) {
        val newLastIndex = min(titleList.size, lastIndex + paginationCount)

        for (i in paginationTitleList.size..newLastIndex) {
            paginationTitleList.add(i, titleList[i])
        }
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        visibleItemCountL = visibleItemCount;
        visibleLastIndexL = firstVisibleItem + visibleItemCount - 1;
    }

    override fun onScrollStateChanged(p0: AbsListView?, scrollState: Int) {
        val itemsLastIndex = adapter.count - 1
        val lastIndex = itemsLastIndex + 1

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndexL == lastIndex && titleList.size != lastIndex) {
            prLoader.visibility = View.VISIBLE

            loadPublicData(lastIndex)
            adapter.notifyDataSetChanged()

            prLoader.visibility = View.GONE
        }
    }

    fun getPublications() {
        thread {
            Log.i(TAG, "===================CREATE THREAD===================")
            if (titleList.size == 0) {
                result = URL("https://www.reddit.com/top.json").readText()
                try {
                    var jsonObject = JSONObject(result)

                    var data1 = jsonObject.get("data")
                    var data2 = JSONObject(data1.toString())

                    var child = data2.getJSONArray("children")

                    var i: Int = 0
                    var size: Int = child.length()

                    titleList = ArrayList(size)

                    for (i in 0..size - 1) {
                        var json_objectdetail: JSONObject = child.getJSONObject(i)
                        var dat = json_objectdetail.getJSONObject("data")

                        /*if (i == 15) {
                            println(i)
                        }*/

                        val id: String = dat.getString("id")
                        val author: String = "$i) " + dat.getString("author")
                        val created: Long = getHoursAgo(dat.getLong("created_utc"))
                        val thumbnail: String = dat.getString("thumbnail")
                        val numComments: Int = dat.getInt("num_comments")
                        val bitmapThumbnail: Bitmap? = getURLImage(thumbnail)

                        Log.i(TAG, "$i - $author - $thumbnail")

                        viewModel = PublicationViewModel(
                                Publication(
                                        id,
                                        author,
                                        created,
                                        thumbnail,
                                        numComments,
                                        bitmapThumbnail
                                )
                        )
                        //if (thumbnail != "default" && thumbnail != "self") {
                            titleList!!.add(viewModel)
                        //}
                    }
                } catch (e: Exception) {
                    println(e.message)
                    handler.sendEmptyMessage(DOWNLOAD_ERROR)
                }
                println(result)
                handler.sendEmptyMessage(DOWNLOAD_SUCCESS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "----------onResume----------")

        // открытие картинки в большем формате
        val onItemClick: (id: Int) -> Unit = {
            viewModel.itemClicked(this@PublicationActivity, titleList[it].getThumbnail())
        }
        // сохранение картинки
        val onItemLongClick: (id: Int) -> Unit = {
            viewModel.itemLongClicked(this@PublicationActivity, titleList[it].getBitmapThumbnail())
        }

        adapter = PublicationAdapter(applicationContext, onItemClick, onItemLongClick)

        adapter.item = paginationTitleList
        dataBinding?.lvList?.adapter = adapter
    }

    private fun getURLImage(vararg urls: String): Bitmap {
        val imageURL = urls[0]
        var image: Bitmap? = null
        try {
            val `in` = java.net.URL(imageURL).openStream()
            image = BitmapFactory.decodeStream(`in`)
        }
        catch (e: Exception) {
            Log.e("Error Message", e.message.toString())
            e.printStackTrace()
        }

        if (image == null) {
            image = defaultPicture
        }

        return image
    }

    fun getHoursAgo(epochSeconds: Long) : Long {
        val milliseconds = 1000
        val createdDate = Date(epochSeconds * milliseconds)
        val nowDate = Date()

        val diffDate = nowDate.time - createdDate.time
        val timeUnit = TimeUnit.HOURS
        return timeUnit.convert(diffDate, TimeUnit.MILLISECONDS)
    }
}