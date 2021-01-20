package com.vrgsoft.reddittoppub.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.vrgsoft.reddittoppub.databinding.PublicListLayoutBinding
import com.vrgsoft.reddittoppub.viewmodel.PublicationViewModel

class PublicationAdapter(private val context: Context,
                         private val onItemClick: (id: Int) -> Unit,
                         private val onItemLongClick: (id: Int) -> Unit) : BaseAdapter() {
    var item: List<PublicationViewModel> = emptyList()

    override fun getCount(): Int = item.size

    override fun getItem(position: Int): Any = item[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: PublicListLayoutBinding

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

        return binding.root
    }
}