package br.com.nurik.mynetshoes.gistDetail

import android.content.Intent
import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist

/**
 * Implementation of GistPresenter, handle interaction between
 * Gist and GistMainView
 */
class GistPresenter(private var gistMainView: GistContract.MainView?,
                    private val gistModel: GistContract.GistModel) : GistContract.presenter, GistContract.OnFinishedListener {



    /**
     * Get a Gist from intent
     *
     * @param intent    Intent from View
     */
    override fun getGist(intent: Intent) {
        gistMainView!!.showProgress()
        try {
            var gist = intent.extras.getSerializable("gist") as Gist
            onFinished(gist)
        } catch (e: Exception) {
            onFailure(e)
        }
    }


    /**
     * Remove from favorite list a gist and calling the Model interface
     *
     * @param gist          item to be excluded
     * @param repository    location of fetched data
     */
    override fun removeFavoriteGist(gist: Gist, repository: SharedPreferences) {
        gistMainView!!.showProgress()
        removeGistFromFavorite(gist, repository, this)
    }

    /**
     * Save into repository a gist which a user favorited
     *
     * @param gist          item to be saved
     * @param repository    location of fetched data
     */
    override fun saveFavoriteGist(gist: Gist, repository: SharedPreferences) {
        gistMainView!!.showProgress()
        gistModel.setGistToFavorite(gist, repository, this)
    }

    /**
     * When View call itself onDestroy() method, needs to call this one,
     * to be sure of resource release
     */
    override fun onDestroy() {
        gistMainView = null
    }

    /**
     * When the API returns the positive results, this is notified to View
     *
     * @param gist        result gist
     */
    override fun onFinished(gist: Gist) {
        if (gistMainView != null) {
            gistMainView!!.setGistData(gist)
            gistMainView!!.hideProgress()
        }
    }

    /**
     * When the API returns the positive results, this is notified to View
     *
     * @param gist        result gist
     */
    override fun onFavorite(gist: Gist?) {
        if (gistMainView != null) {
            gistMainView!!.onFavorite(gist!=null)
            gistMainView!!.hideProgress()
        }
    }

    /**
     * When the API returns the negative results, this is notified to View
     *
     * @param gist        result gist
     */
    override fun onFailure(t: Throwable) {
        if (gistMainView != null) {
            gistMainView!!.onResponseFailure(t)
            gistMainView!!.hideProgress()
        }
    }

    /**
     * Method which recover a favorite list from SharedPreferences and remove the gist instance
     * from there, and save the list without the excluded item
     *
     * @param gist                  item to be excluded
     * @param repository            local of fetched data
     * @param onFinishedListener    listener to call presenter
     */
    private fun removeGistFromFavorite(gist: Gist, repository: SharedPreferences, onFinishedListener: GistContract.OnFinishedListener) {
        try {
            var list = gistModel.getListFromSP(repository)
            if(list!=null || !list.equals("")){
                searchAndRemoveItem(list, gist)
                gistModel.setListToSP(list, repository)
                onFinishedListener.onFavorite(null)
            }else{
                onFinishedListener.onFailure(Throwable("Houve um problema"))
            }
        }catch (e: Exception){
            onFinishedListener.onFailure(Throwable(e.message))
        }
    }

    /**
     * Here the item which be deleted is compared to recovery list, and removed
     * if founded
     *
     * @param list      recovered list
     * @param gist      item to be deleted
     */
    private fun searchAndRemoveItem(list: ArrayList<Gist>, gist: Gist) {
        if(list.size > 0){
            var indexToDelete = 0
            list.forEachIndexed { index, item ->
                if (item.id.equals(gist.id)) {
                    indexToDelete = index
                }
            }
            list.removeAt(indexToDelete)
        }
    }
}