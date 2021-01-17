package com.mstoica.dogoapp.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.network.ImageOptions
import org.jetbrains.anko.find

class DogImageAdapter(
    context: Context
): RecyclerView.Adapter<DogImageAdapter.ViewHolder>() {

    interface IDogoListener {
        fun onItemClicked(dog: Dog, position: Int)
        fun onLikeChanged(dog: Dog, like: Boolean)
    }

    private val inflater = LayoutInflater.from(context)

    var items: MutableList<Dog> = mutableListOf()
        set(value) {
            field.clear()
            field = value
            notifyDataSetChanged()
        }

    var listener: IDogoListener? = null

    fun addDog(dogItem: Dog) {
        items.add(dogItem)
        notifyItemInserted(items.size - 1)
    }

    fun notifyItemChanged(dogItem: Dog) {
        val position = items.indexOf(dogItem)
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    fun removeDog(dogItem: Dog) {
        val position = items.indexOf(dogItem)
        if (position != -1) {
            items.remove(dogItem)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.dog_image_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivDog = itemView.find<ImageView>(R.id.iv_dog)
        private val ivLike = itemView.find<ImageView>(R.id.iv_like)

        private val colorLiked = ContextCompat.getColor(itemView.context, R.color.materialRed700)
        private val colorDefault = ContextCompat.getColor(itemView.context, R.color.white)

        private val imageRequestOptions = RequestOptions().centerCrop()

        fun bind(dogItem: Dog, position: Int) {
            val liked = dogItem.favId != null

            val resource = if(liked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            val tint = if(liked) colorLiked else colorDefault

            ivLike.apply {
                setImageResource(resource)
                imageTintList = ColorStateList.valueOf(tint)
                setOnClickListener {
                    listener?.onLikeChanged(dogItem, liked.not())
                }
            }

            Glide.with(itemView.context)
                .load(dogItem.imageUrl)
                .apply(imageRequestOptions.override(ImageOptions.THUMBNAIL_SIZE))
                .into(ivDog)

            itemView.setOnClickListener {
                listener?.onItemClicked(dogItem, position)
            }
        }
    }

}