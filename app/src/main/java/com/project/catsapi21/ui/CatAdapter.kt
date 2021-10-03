package com.project.catsapi21.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.catsapi21.R
import com.project.catsapi21.listeners.OnItemClickListener
import com.project.catsapi21.model.CatsList
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.cat_item.view.*
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

class CatAdapter(private val list: ArrayList<CatsList>, private val context: Context) :
    RecyclerView.Adapter<CatAdapter.ListViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)

        return ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val url = list[position].url
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(25))
        Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progress.visibility = View.GONE
                    holder.itemView.img.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progress.visibility = View.GONE
                    holder.itemView.img.visibility = View.VISIBLE
                    return false
                }

            })
            .apply(requestOptions)
            .error(R.drawable.ic_launcher_background)
            .into(holder.img)

        if (listener != null) {
            holder.itemView.setOnClickListener {
                listener!!.onCatClick(url)
            }
        }
    }

    fun addData(list: ArrayList<CatsList>) {
        val size = this.list.size
        this.list.addAll(list)
        val sizeNew = this.list.size
        notifyItemRangeChanged(size, sizeNew)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img)
    }

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}
