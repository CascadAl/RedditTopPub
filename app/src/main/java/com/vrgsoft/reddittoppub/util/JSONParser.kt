package com.vrgsoft.reddittoppub.util

import android.graphics.Bitmap
import com.vrgsoft.reddittoppub.model.Publication
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel
import org.json.JSONObject
import java.net.URL

class JSONParser {
    companion object {
        fun getDataFromJSON(url: String) : MutableList<PublicationViewModel> {
            var list = mutableListOf<PublicationViewModel>()
            val result = URL(url).readText()

            var res =
                JSONObject(JSONObject(result).get("data").toString()).getJSONArray("children")
            var size: Int = res.length()

            for (i in 0 until size) {
                var data = res.getJSONObject(i).getJSONObject("data")

                val id: String = data.getString("id")
                val author: String = data.getString("author")
                val created: Long = PublicationUtil.getHoursAgo(data.getLong("created_utc"))
                val thumbnail: String = data.getString("thumbnail")
                val numComments: Int = data.getInt("num_comments")
                val bitmapThumbnail: Bitmap? = PublicationUtil.getURLImage(thumbnail)

                list.add(
                    PublicationViewModel(
                        Publication(
                            id,
                            author,
                            created,
                            thumbnail,
                            numComments,
                            bitmapThumbnail
                        )
                    )
                )
            }

            return list
        }
    }
}