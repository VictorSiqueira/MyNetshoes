package br.com.nurik.mynetshoes.gistList

import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist

interface GistListContract {

    /**
     * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
     * while the setDataToRecyclerView and onResponseFailure is fetched from the GistListModel
     * class
     */
    interface GistListMainView {

        fun showProgress()

        fun hideProgress()

        fun onResponseFailure(throwable: Throwable)

        fun setDataToRecyclerView(searchList: ArrayList<Gist>?, favList: ArrayList<Gist>?)
    }

    /**
     * Business logic layer, which do all treatment
     */
    interface GistListPresenter {

        fun onDestroy()

        fun requestDataFromServer()

        fun loadMoreDataFromServer()

        fun requestFavoriteData(repository: SharedPreferences)

        fun saveFavoriteGist(gist: Gist, repository: SharedPreferences)

        fun removeFavoriteGist(gist: Gist, repository: SharedPreferences)
    }

    /**
     * getGistList(), getFavoriteGistList(),removeGistFromFavorite(), setGistToFavorite()
     * are classes built for fetching data from your database, web services,
     * or any other data source.
     */
    interface GistListModel {

        fun getGistList(page: Int, onFinishedListener: OnFinishedListener)

        fun getFavoriteGistList(repository: SharedPreferences, onFinishedListener: OnFinishedListener)

        fun setGistToFavorite(gist: Gist, repository: SharedPreferences, onFinishedListener: OnFinishedListener)

        fun getListFromSP(repository: SharedPreferences): ArrayList<Gist>

        fun setListToSP(list: ArrayList<Gist>, repository: SharedPreferences)
    }

    interface OnFinishedListener {
        fun onFinished(gistList: ArrayList<Gist>)
        fun onFavorite(gistList: ArrayList<Gist>)
        fun onFailure(t: Throwable)
    }
}
