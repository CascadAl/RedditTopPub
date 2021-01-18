package com.vrgsoft.reddittoppub.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.BindingAdapter
import com.vrgsoft.reddittoppub.R
import com.vrgsoft.reddittoppub.model.Publication
import java.io.File
import java.io.FileOutputStream

class PublicationViewModel(private var publication: Publication) : BaseObservable() {

    private var author: String = publication.author
    private var created: Long = publication.created
    private var thumbnail: String = publication.thumbnail
    private var numComments: Int = publication.numComments
    private var bitmapThumbnail: Bitmap? = publication.bitmapThumbnail
    private val TAG: String = "PublicationViewModel"

    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, bitmap: Bitmap/* uri: String*/) {
            Log.i("TAG", "bitmap")
            imageView.setImageBitmap(bitmap)

            //if (bitmap != null) {
                //imageView.setImageResource(R.drawable.ic_launcher_foreground)
            //}
            // https://medium.com/@gunayadem.dev/boost-your-android-apps-with-koin-and-coroutines-using-mvvm-in-kotlin-d30fe436ab4c
            // https://medium.com/@gunayadem.dev/add-a-click-listener-to-your-adapter-using-mvvm-in-kotlin-part-2-9dce852e96d5
        }
    }

    // открытие картинки в большем формате
    fun itemClicked(context: Context, thumbnailUrl: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(thumbnailUrl)
        ContextCompat.startActivity(context, intent, null)
    }

    // сохранение картинки
    fun itemLongClicked(context: Context, bitmapThumbnail: Bitmap) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (!path.exists()) {
                path.mkdir()
            }

            val file = File(path, "img.jpg")
            val out = FileOutputStream(file).use {
                bitmapThumbnail.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }

            Log.i("Seiggailion", "Image saved.")
            Toast.makeText(context, "Image is downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.i("Seiggailion", "Failed to save image.")
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAuthor(): String {
        return author
    }

    fun getCreated(): String {
        return " $created hours ago"
    }

    fun getThumbnail(): String {
        return thumbnail
    }

    fun getNumComments(): String {
        return "$numComments Comments"
    }

    fun getBitmapThumbnail(): Bitmap {
        return bitmapThumbnail!!
    }
}