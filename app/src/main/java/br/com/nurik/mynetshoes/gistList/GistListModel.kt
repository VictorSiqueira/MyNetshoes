package br.com.nurik.mynetshoes.gistList

import android.content.SharedPreferences
import br.com.nurik.mynetshoes.pojo.Gist
import br.com.nurik.mynetshoes.utils.NetworkInterface
import br.com.nurik.mynetshoes.gistList.GistListContract.OnFinishedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class where the data is accessed or treated
 */
class GistListModel : GistListContract.GistListModel {

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
            gist.favorite = true
            if(list==null || list.equals("")){
                list = ArrayList<Gist>()
                list.add(gist)
                setListToSP(list, repository)
            }else{
                setItemOnExistingList(list, gist, repository)
            }
            onFinishedListener.onFavorite(list)
        }catch (e: Exception){
            onFinishedListener.onFailure(e)
        }
    }

    /**
     * Method which recover a favorite list from SharedPreferences and send to presenter
     *
     * @param repository            local of fetched data
     * @param onFinishedListener    listener to call presenter
     */
    override fun getFavoriteGistList(repository: SharedPreferences, onFinishedListener: OnFinishedListener) {
        var list = getListFromSP(repository)
        if(list!=null){
            onFinishedListener.onFavorite(list)
        }else{
           onFinishedListener.onFailure(throw Throwable("Erro"))
        }
    }

    /**
     * Method which recover a favorite list from API and send to presenter,
     * through a Retrofit call and treat the response
     *
     * @param page                  pagination number
     * @param onFinishedListener    listener to call presenter
     */
    override fun getGistList(page: Int, onFinishedListener: OnFinishedListener) {
        var call = buildNetworkService()!!.getGists(page)
        call.enqueue(object : Callback<ArrayList<Gist>> {
            override fun onResponse(call: Call<ArrayList<Gist>>?, response: Response<ArrayList<Gist>>) {
                if (onFinishedListener != null) {
                    if (response!!.body() != null) {
                        onFinishedListener!!.onFinished(response.body()!!)
                    }else{
                        onFinishedListener!!.onFailure(Throwable(response.message()))
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<Gist>>?, t: Throwable) {
                if (onFinishedListener != null) {
                    onFinishedListener!!.onFailure(t)
                }
            }
        })
    }

    /**
     * Building of network interface instance which will be used on Retrofit API calls
     *
     * @return      network service interface
     */
    fun buildNetworkService(): NetworkInterface? {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        var okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(NetworkInterface.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(NetworkInterface::class.java)
    }

    /**
     * Here the favorite List is recovered from SharedPrefences, referenced by parameter
     *
     * @param repository        Repository of fetched data
     * @return                  Favorite list
     */
    override fun getListFromSP(repository: SharedPreferences): ArrayList<Gist> {
        val listType = object : TypeToken<ArrayList<Gist>>() {}.type
        var list = Gson().fromJson(repository.getString("favorites", ""), listType) as Any?
        if(list!=null){
            return list as ArrayList<Gist>
        }else{
            return ArrayList()
        }
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
            list.add(gist)
            setListToSP(list, repository)
        }
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
        editor.commit()
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