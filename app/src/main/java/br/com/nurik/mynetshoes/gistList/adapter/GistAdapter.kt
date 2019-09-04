package br.com.nurik.mynetshoes.gistList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import java.util.ArrayList

import androidx.recyclerview.widget.RecyclerView
import br.com.nurik.mynetshoes.R
import br.com.nurik.mynetshoes.pojo.Gist
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.list_item_layout.view.*

/**
 * Adapter where the Gist list is built
 */
class GistAdapter(private val context: Context,
                  private val dataList: ArrayList<Gist>,
                  private val recyclerItemClickListener: RecyclerItemClickListener) : RecyclerView.Adapter<GistAdapter.ViewHolder>() {

    private var filteredList: ArrayList<Gist>? = null

    init {
        this.filteredList = dataList
    }

    /**
     * Inflating the layout where the data will be presented
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_layout, parent, false)
        return ViewHolder(view)
    }

    /**
     * When the holder is binded, a item into list is located and used to fill the holder with data
     *
     * @param holder       view holder where fields are into
     * @param position     gist item position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var obj = filteredList!![position]
        setOwnerData(obj, holder)
        holder.item_list_type.text = getFileName(obj)
        setActionButton(holder, obj)
        holder.itemView.setOnClickListener {
            recyclerItemClickListener.onItemClick(obj, false)
        }
    }

    /**
     * In gist list, the action is add or remove item from favorite list
     *
     * @param holder       view holder where fields are into
     * @param gist          gist item
     */
    fun setActionButton(holder: ViewHolder, gist: Gist) {
        if(gist.favorite){
            Glide.with(context).load(R.drawable.ic_favorite_on).into(holder.item_list_action)
        }else{
            Glide.with(context).load(R.drawable.ic_favorite_off).into(holder.item_list_action)
        }
        holder.item_list_action.setOnClickListener {
            recyclerItemClickListener.onItemClick(gist, true)
            notifyDataSetChanged()
        }
    }

    /**
     * Setting owner data into holder fields
     *
     * @param gist     gist item
     * @param holder    view holder where fields are into
     */
    fun setOwnerData(gist: Gist, holder: ViewHolder) {
        if (gist.owner != null) {
            Glide.with(context).load(gist.owner!!.avatar_url).into(holder.item_list_avatar)
            holder.item_list_name.text = gist.owner!!.login
        }
    }

    /**
     * Getting and concat all file names
     *
     * @param gist     gist item
     */
    fun getFileName(gist: Gist): String {
        if(gist.files!=null){
            var fileName = mutableListOf<String>()
            var list = (gist.files as LinkedTreeMap<String, LinkedTreeMap<String, String>>)
            for (i in list) {
                fileName.add(i.key)
            }
            return fileName.joinToString(", ")

        }else{
            return ""
        }
    }

    override fun getItemCount(): Int {
        return filteredList!!.size
    }

    /**
     * Filtering by owner name, a list is built and after finish, is associated to main filtered field
     */
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredList = dataList
                } else {
                    val filteredItems = ArrayList<Gist>()
                    for (row in dataList) {
                        if (row.owner!!.login!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredItems.add(row)
                        }
                    }
                    filteredList = filteredItems
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = filteredList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredList = filterResults.values as ArrayList<Gist>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_list_avatar =  itemView.item_list_avatar
        val item_list_name = itemView.item_list_name
        val item_list_type = itemView.item_list_type
        val item_list_action = itemView.item_list_action
    }
}
