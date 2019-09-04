package br.com.nurik.mynetshoes.gistList

import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist

/**
 * Implementation of GistListPresenter, handle interaction between
 * GistListModel and GistListMainView
 */
class GistListPresenter(private var gistListView: GistListContract.GistListMainView?,
                        private val gistListModel: GistListContract.GistListModel) : GistListContract.GistListPresenter, GistListContract.OnFinishedListener {

    private var lastLoadedPage = 1
    private var rawList =  ArrayList<Gist>()
    private var favoriteList =  ArrayList<Gist>()
    private var reloadList = false


    /**
     * Get from GistListModel a favorite list from repository
     *
     * @param repository    location of fetched data
     */
    override fun requestFavoriteData(repository: SharedPreferences) {
        gistListModel.getFavoriteGistList(repository, this)
    }

    /**
     * Remove from favorite list a gist and save the list on the repository
     * without this item
     *
     * @param gist          item to be excluded
     * @param repository    location of fetched data
     */
    override fun removeFavoriteGist(gist: Gist, repository: SharedPreferences) {
        removeGistFromFavorite(gist, repository, this)
    }

    /**
     * Save into repository a gist which a user favorited
     *
     * @param gist          item to be saved
     * @param repository    location of fetched data
     */
    override fun saveFavoriteGist(gist: Gist, repository: SharedPreferences) {
        gistListModel.setGistToFavorite(gist, repository, this)
    }

    /**
     * When View call itself onDestroy() method, needs to call this one,
     * to be sure of resource release
     */
    override fun onDestroy() {
        gistListView = null
    }

    /**
     * Call the API to retrieve all gists, during this action
     * showProgress() is called to notify the user which something is
     * happening. This is the first call of View to load items or
     * reload/update the list
     */
    override fun requestDataFromServer() {
        reloadList = true
        if (gistListView != null) {
            gistListView!!.showProgress()
        }
        gistListModel.getGistList(1, this)
    }

    /**
     * When you need to load more items, this method call the API passing a
     * page counting to bring the correct list, and showing the showProgress()
     * during the action.
     */
    override fun loadMoreDataFromServer() {
        reloadList = false
        if (gistListView != null) {
            gistListView!!.showProgress()
        }
        gistListModel.getGistList(lastLoadedPage, this)
        lastLoadedPage++
    }

    /**
     * When the API returns the positive results of requestDataFromServer()
     * and loadMoreDataFromServer() methods , all items pass through
     * this method and receive the right treatment to be sure the list
     * will carry correctly all data, verifying duplicate items, favorite
     * items, and if need to reset the list (when a reload is called)
     *
     * @param mArrayList        result list
     */
    override fun onFinished(mArrayList: ArrayList<Gist>) {
        if (gistListView != null) {
            if(reloadList && rawList!=null){
                rawList.clear()
            }
            if(rawList==null){
                rawList.addAll(mArrayList)
            }
            updateList(mArrayList)
            updateView()
        }
    }

    /**
     * When the repository interaction is finished by requestFavoriteData(),
     * removeFavoriteGist() and saveFavoriteGist() methos, this method is called
     * to send to View the favorite list
     *
     * @param gistList      favorite list
     */
    override fun onFavorite(gistList: ArrayList<Gist>) {
        favoriteList = gistList
        updateView()
    }


    /**
     * When something went wrong during requestDataFromServer, loadMoreDataFromServer,
     * requestFavoriteData(), removeFavoriteGist() or saveFavoriteGist() process, this
     * method is called to notify the View
     *
     * @param t     Exception of process
     */
    override fun onFailure(t: Throwable) {
        if (gistListView != null) {
            gistListView!!.onResponseFailure(t)
            gistListView!!.hideProgress()
        }
    }

    /**
     * Method what organize the gist list and send to recyclerView in the View
     */
    fun updateView() {
        verifyFavoriteItem()
        gistListView!!.setDataToRecyclerView(rawList, favoriteList)
        gistListView!!.hideProgress()
    }

    /**
     * Method what verify duplicate item of list and add only unique items
     *
     * @param newList       result list
     */
    fun updateList(newList: ArrayList<Gist>) {
        for(item in newList ){
            var exists = false
            for(raw in rawList){
                if(item.id!!.equals(raw.id!!)){
                    exists = true
                }
            }
            if(!exists){
                rawList.add(item)
            }
        }
    }

    /**
     * Method what check if a item is favorited comparing with the favorite list
     */
    fun verifyFavoriteItem() {
        rawList.forEach {
            it.favorite = false
            favoriteList.forEach { fav: Gist ->
                if (it.id!!.equals(fav.id)) {
                    it.favorite = true
                }
            }
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
    private fun removeGistFromFavorite(gist: Gist, repository: SharedPreferences, onFinishedListener: GistListContract.OnFinishedListener) {
        try {
            var list = gistListModel.getListFromSP(repository)
            if(list!=null || !list.equals("")){
                searchAndRemoveItem(list, gist)
                gistListModel.setListToSP(list, repository)
                onFinishedListener.onFavorite(list)
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