package br.com.nurik.mynetshoes.gistDetail

import android.content.Intent
import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist

interface GistContract {

    /**
     * Business logic layer, which do all treatment
     */
    interface presenter {
        fun onDestroy()

        fun getGist(intent: Intent)

        fun saveFavoriteGist(gist: Gist, repository: SharedPreferences)

        fun removeFavoriteGist(gist: Gist, repository: SharedPreferences)
    }

    /**
     * Presentation layer, which do interaction
     */
    interface MainView {

        fun showProgress()

        fun hideProgress()

        fun onResponseFailure(throwable: Throwable)

        fun setGistData(gist: Gist)

        fun onFavorite(favorite: Boolean)
    }

    /**
     * Model layer, which brings and fetch all data
     */
    interface GistModel {

        fun setGistToFavorite(gist: Gist, repository: SharedPreferences, onFinishedListener: OnFinishedListener)

        fun getListFromSP(repository: SharedPreferences): ArrayList<Gist>

        fun setListToSP(list: ArrayList<Gist>, repository: SharedPreferences)
    }

    interface OnFinishedListener {
        fun onFinished(gist: Gist)
        fun onFavorite(gist: Gist?)
        fun onFailure(t: Throwable)
    }
}
