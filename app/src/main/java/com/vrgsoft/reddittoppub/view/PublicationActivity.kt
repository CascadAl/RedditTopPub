package com.vrgsoft.reddittoppub.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
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
import com.vrgsoft.reddittoppub.databinding.PublicListLayoutBinding
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel


class PublicationActivity : AppCompatActivity() {

    var result: String = ""
    var jsonArray: JSONArray? = null
    var lvList: ListView? = null
    var titleList = mutableListOf<PublicationViewModel>()
    //var dataBinding: PublicListLayoutBinding? = null
    var dataBinding: ActivityPublicationBinding? = null
    lateinit var adapter: PublicationAdapter
    lateinit var viewModel: PublicationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_publication)

        thread {
            result = URL("https://www.reddit.com/top.json").readText()
            try {
                var jsonObject = JSONObject(result)

                var data1 = jsonObject.get("data")
                var data2 = JSONObject(data1.toString())

                var child = data2.getJSONArray("children")

                var i: Int = 0
                var size:Int = child.length()

                titleList = ArrayList(size)

                //arrayList_details= ArrayList();
                for (i in 0.. size-1) {
                    var json_objectdetail: JSONObject =child.getJSONObject(i)
                    var dat = json_objectdetail.getJSONObject("data")

                    val id: String = dat.getString("id")
                    val author: String = dat.getString("author")
                    val created: Long = getHoursAgo(dat.getLong("created_utc"))
                    val thumbnail: String = dat.getString("thumbnail")
                    val numComments: Int = dat.getInt("num_comments")
                    val bitmapThumbnail: Bitmap? = getURLImage(thumbnail)

                    viewModel = PublicationViewModel(Publication(id, author, created, thumbnail, numComments, bitmapThumbnail))
                    titleList!!.add(viewModel)
                }

                //var y = JSONArray(result) //error
                var title : String = jsonObject.getJSONObject("kind").toString()
                //x.getJSONObject(0).getString()
                //x.getJSONArray(0)

                //jsonArray = JSONArray(result)
            } catch (e: Exception) {
                println(e.message)
            }
            println(result)
        }

        lvList = findViewById<ListView>(R.id.lvList)

    }

    private fun getURLImage(vararg urls: String): Bitmap? {
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


    // удалить
    fun getJson(view: View) {
        // открытие картинки в большем формате
        val onItemClick: (id: Int) -> Unit = {
            viewModel.itemClicked(this@PublicationActivity, titleList[it].getThumbnail())
        }
        // сохранение картинки
        val onItemLongClick: (id: Int) -> Unit = {
            viewModel.itemLongClicked(this@PublicationActivity, titleList[it].getBitmapThumbnail())
        }

        adapter = PublicationAdapter(applicationContext, onItemClick, onItemLongClick)

        //val adapter = PublicationAdapter(this, R.layout.public_list_layout, titleList!!)
        adapter.item = titleList
        dataBinding?.lvList?.adapter = adapter

        //lvList?.adapter = adapter
    }
}