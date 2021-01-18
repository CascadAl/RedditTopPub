package com.vrgsoft.reddittoppub.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AbsListView.OnScrollListener
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vrgsoft.reddittoppub.R
import com.vrgsoft.reddittoppub.databinding.PublicListLayoutBinding
import com.vrgsoft.reddittoppub.model.Publication
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.ArrayList

class PublicationAdapter(private val context: Context,
                         private val onItemClick: (id: Int) -> Unit,
                         private val onItemLongClick: (id: Int) -> Unit) : BaseAdapter() {
    var item: List<PublicationViewModel> = emptyList()

    override fun getCount(): Int = item.size

    override fun getItem(position: Int): Any = item[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: PublicListLayoutBinding

        /*if ((parent as ListView).lastVisiblePosition < position) {
            return convertView!!
        }*/

        if (convertView == null) {
            binding = PublicListLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as PublicListLayoutBinding
        }

        binding.item = getItem(position) as PublicationViewModel

        // открытие картинки в большем формате
        binding.ivThumbnail.setOnClickListener{
            onItemClick.invoke(position)
        }

        // сохранение картинки
        binding.ivThumbnail.setOnLongClickListener{
            onItemLongClick.invoke(position)

            return@setOnLongClickListener true;
        }

        // открытие картинки в большем формате
        /*binding.ivThumbnail.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(binding.item.getThumbnail())
            ContextCompat.startActivity(context, intent, null)
        })*/


        // сохранение картинки
        /*thumbnail.setOnLongClickListener(View.OnLongClickListener {
            val filename = "$id.jpg"

            try {
                /*var fos: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver?.also { resolver ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                        val imageUri: Uri? =
                            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, filename)
                    fos = FileOutputStream(image)
                }
                fos?.use {
                    bitmapImg?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }*/





                val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                //val a = Environment.getDownloadCacheDirectory()
                //val path = Environment.getDataDirectory()
                //val c = Environment.getRootDirectory()

                //val path: File = File(context.filesDir, "mydir")
                if (!path.exists()) {
                    path.mkdir()
                }

                //var path = context.getDir("Images", Context.MODE_PRIVATE)
                //var path = ContextWrapper(context).getDir("Images", Context.MODE_PRIVATE)
                val file = File(path, "img.jpg")
                val out = FileOutputStream(file)
                bitmapImg?.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
                out.close()
                Log.i("Seiggailion", "Image saved.")
                Toast.makeText(context, "Image is downloaded", Toast.LENGTH_SHORT).show()


            } catch (e: Exception) {
                Log.i("Seiggailion", "Failed to save image.")
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

            return@OnLongClickListener true;
        })*/

        return binding.root
    }
}

/*open class PublicationAdapter(context: Context,
                              private var resource: Int,
                              private var list: ArrayList<Publication>) : ArrayAdapter<Publication>(context, resource, list) {

    private var publicListBinding: PublicListLayoutBinding? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource, parent,false)
            /*publicListBinding = convertView?.let { DataBindingUtil.bind(it) }
            convertView?.tag = publicListBinding*/
        } else {
            view = convertView!!
            //publicListBinding = convertView.tag as PublicListLayoutBinding?
        }

        //val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, parent,false)

        val author = view.findViewById<TextView>(R.id.tvAuthor)
        val created = view.findViewById<TextView>(R.id.tvCreated)
        val thumbnail = view.findViewById<ImageView>(R.id.ivThumbnail)
        val commentsCnt = view.findViewById<TextView>(R.id.tvCommentsCnt)

        val id = list[position].id
        val authorVal = list[position].author
        val createdHours = list[position].created
        val thumbnailURL = list[position].thumbnail
        val numComments = list[position].numComments
        val bitmapImg = list[position].bitmapThumbnail

        //DownloadImageFromInternet(thumbnail).execute(thumbnailURL)

        author.text = authorVal
        created.text = " $createdHours hours ago"
        thumbnail.setImageBitmap(bitmapImg)
        //thumbnail.layoutParams = LinearLayout.LayoutParams(500,500)
        commentsCnt.text = "$numComments Comments"

        /*publicListBinding.p
        publicListBinding?.item(Publication("", "", 0, "", 0, bitmapImg))*/

        //publicListBinding.item(Publication(id, authorVal, 0, "", 0, bitmapImg))
        //publicListBinding.set



        /*thumbnail.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(thumbnailURL)
            ContextCompat.startActivity(context, intent, null)
        })


        thumbnail.setOnLongClickListener(View.OnLongClickListener {
            val filename = "$id.jpg"

            try {
                /*var fos: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver?.also { resolver ->
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                        val imageUri: Uri? =
                            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, filename)
                    fos = FileOutputStream(image)
                }
                fos?.use {
                    bitmapImg?.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }*/





                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                //val a = Environment.getDownloadCacheDirectory()
                //val path = Environment.getDataDirectory()
                //val c = Environment.getRootDirectory()

                //val path: File = File(context.filesDir, "mydir")
                if (!path.exists()) {
                    path.mkdir()
                }

                //var path = context.getDir("Images", Context.MODE_PRIVATE)
                //var path = ContextWrapper(context).getDir("Images", Context.MODE_PRIVATE)
                val file = File(path, "img.jpg")
                val out = FileOutputStream(file)
                bitmapImg?.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
                out.close()
                Log.i("Seiggailion", "Image saved.")
                Toast.makeText(context, "Image is downloaded", Toast.LENGTH_SHORT).show()


            } catch (e: Exception) {
                Log.i("Seiggailion", "Failed to save image.")
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }

            return@OnLongClickListener true;
        })*/


        return view
    }

}*/