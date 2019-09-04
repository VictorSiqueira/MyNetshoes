package br.com.nurik.mynetshoes.utils


import br.com.nurik.mynetshoes.pojo.Gist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface which keep all connections Url and endpoints
 */
interface NetworkInterface {

    companion object {
        val BASE_URL = "https://api.github.com/"
    }

    /**
     * Method to get gist list passing as parameter the page number,
     *
     * @param page      page of list, if you pass 0 or 1, you will get the same result
     * @return          the gist list with 30 items
     */
    @GET("gists/public")
    fun getGists(@Query("page") page: Int): Call<ArrayList<Gist>>

}
