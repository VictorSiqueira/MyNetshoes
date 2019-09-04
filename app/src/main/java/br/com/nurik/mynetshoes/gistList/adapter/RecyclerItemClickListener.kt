package br.com.nurik.mynetshoes.gistList.adapter

import br.com.nurik.mynetshoes.pojo.Gist

/**
 * Listener to handle when a recycler item is clicked, this method will be called by View layer
 * where the recycler is instantiated
 */
interface RecyclerItemClickListener {
    fun onItemClick(gist: Gist, favorite: Boolean)
}