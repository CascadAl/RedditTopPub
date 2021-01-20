package com.vrgsoft.reddittoppub.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class PublicationUtil {
    companion object {
        fun getURLImage(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return image
        }

        fun getHoursAgo(epochSeconds: Long): Long {
            val milliseconds = 1000
            val createdDate = Date(epochSeconds * milliseconds)
            val nowDate = Date()

            val diffDate = nowDate.time - createdDate.time
            val timeUnit = TimeUnit.HOURS
            return timeUnit.convert(diffDate, TimeUnit.MILLISECONDS)
        }
    }
}