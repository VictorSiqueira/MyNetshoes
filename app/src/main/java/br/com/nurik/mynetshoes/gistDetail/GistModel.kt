package br.com.nurik.mynetshoes.gistDetail

import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist
import br.com.nurik.mynetshoes.gistDetail.GistContract.OnFinishedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Class where the data is accessed or treated
 */
class GistModel : GistContract.GistModel {

    /**
     * Method which recover a favorite list from SharedPreferences and save the gist instance
     * there, and save the list with the new item
     *
     * @param gist                  item to be added
     * @param repository            local of fetched data
     * @param onFinishedListener    listener to call presenter
     */
    override fun setGistToFavorite(gist: Gist, repository: SharedPreferences, onFinishedListener: OnFinishedListener) {
        try {
            var list = getListFromSP(repository)
            if(list==null || list.equals("")){
                var newList = ArrayList<Gist>()
                gist.favorite = true
                newList.add(gist)
                setListToSP(newList, repository)
            }else{
                setItemOnExistingList(list, gist, repository)
            }
            onFinishedListener.onFavorite(gist)
        }catch (e: Exception){
            onFinishedListener.onFailure(e)
        }
    }

    /**
     * Here the favorite List is recovered from SharedPrefences, referenced by parameter
     *
     * @param repository        Repository of fetched data
     * @return                  Favorite list
     */
    override fun getListFromSP(repository: SharedPreferences): ArrayList<Gist> {
        val listType = object : TypeToken<ArrayList<Gist>>() {}.type
        var list = Gson().fromJson(repository.getString("favorites", ""), listType) as ArrayList<Gist>
        return list
    }

    /**
     * After receive treatment the favorite list is saved into SharedProferences
     *
     * @param list          list to be saved
     * @param repository    location of fetched data
     */
    override fun setListToSP(list: ArrayList<Gist>, repository: SharedPreferences) {
        var editor = repository.edit()
        editor.remove("favorites")
        editor.putString("favorites", Gson().toJson(list))
        editor.apply()
    }

    /**
     * When a item is favorited, if doesn't already exist at list, it's added
     *
     * @param list              favorite list
     * @param gist              favorited item
     * @param repository        location of fetched data
     */
    private fun setItemOnExistingList(list: ArrayList<Gist>, gist: Gist, repository: SharedPreferences) {
        if (!verifyIteminFavorites(list, gist.id!!)) {
            gist.favorite = true
            list.add(gist)
            setListToSP(list, repository)
        }
    }

    /**
     * Treatment to remove duplicate items in the favorite list
     *
     * @param list          favorite list
     * @param id            item's id
     * return               item's existence
     */
    private fun verifyIteminFavorites(list: ArrayList<Gist>, id: String): Boolean {
        var exists = false
        for (item in list) {
            if (item.id != null && item.id!!.equals(id)) {
                exists = true
            }
        }
        return exists
    }
}