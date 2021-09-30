package com.project.catsapi21.ui
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.catsapi21.R
import com.project.catsapi21.model.CatsList
import com.project.thecatapi.R
import com.project.thecatapi.model.CatsList
import com.project.thecatapi.ui.catlist.MainActivity

class CatAdapter(private val list: ArrayList<CatsList>, private val context: Context) :
    RecyclerView.Adapter<CatAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cat_item, parent, false)

        return ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val url: String = list[holder.adapterPosition].url!!
        Glide.with(context)
            .load(url)
            .centerCrop()
            .error(R.drawable.ic_launcher_background)
            .into(holder.img)

        holder.img.setOnClickListener { (context as MainActivity).showDetail(url) }
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
}
