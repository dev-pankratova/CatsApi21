package com.project.catsapi21.ui
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.project.catsapi21.R
import com.project.catsapi21.databinding.CatItemBinding
import com.project.catsapi21.listeners.OnItemClickListener
import com.project.catsapi21.model.CatsList

class CatAdapter(private val list: ArrayList<CatsList>, private val context: Context) :
    RecyclerView.Adapter<CatAdapter.ListViewHolder>() {

    private lateinit var binding: CatItemBinding
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = CatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val url = list[position].url
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(ROUNDING_RADIUS))
        Glide.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progress.visibility = View.GONE
                    binding.img.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progress.visibility = View.GONE
                    binding.img.visibility = View.VISIBLE
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

    inner class ListViewHolder(itemView: CatItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val img: ImageView = itemView.img
    }

    fun setListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    private companion object {
        private const val ROUNDING_RADIUS = 25
    }
}
