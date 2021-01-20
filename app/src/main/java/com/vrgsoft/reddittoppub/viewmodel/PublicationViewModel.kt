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
import com.vrgsoft.reddittoppub.model.Publication
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable

class PublicationViewModel(private var publication: Publication) : BaseObservable(), Serializable {

    private var id: String = publication.id
    private var author: String = publication.author
    private var created: Long = publication.created
    private var thumbnail: String = publication.thumbnail
    private var numComments: Int = publication.numComments
    private var bitmapThumbnail: Bitmap? = publication.bitmapThumbnail

    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, bitmap: Bitmap?) {
            if (bitmap == null) {
                return
            }
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams.width = 500
            imageView.layoutParams.height = 500
            //imageView.requestLayout()
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
    fun itemLongClicked(context: Context, bitmapThumbnail: Bitmap?) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (!path.exists()) {
                path.mkdir()
            }

            val file = File(path, "img.jpg")
            FileOutputStream(file).use {
                bitmapThumbnail?.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }

            Toast.makeText(context, "Image is downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.i("PublicationViewModel", "ErrorMessage - " + e.message)
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun getID(): String {
        return id
    }

    fun getAuthor(): String {
        return author
    }

    fun getCreated(): String {
        return "$created hours ago"
    }

    fun getThumbnail(): String {
        return thumbnail
    }

    fun getNumComments(): String {
        return "$numComments Comments"
    }

    fun getBitmapThumbnail(): Bitmap? {
        return bitmapThumbnail
    }
}