package br.com.nurik.mynetshoes.gistList

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import br.com.nurik.mynetshoes.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.ProgressBar
import br.com.nurik.mynetshoes.pojo.Gist
import br.com.nurik.mynetshoes.gistList.adapter.RecyclerItemClickListener
import br.com.nurik.mynetshoes.utils.InteractionsUtils
import br.com.nurik.mynetshoes.gistList.adapter.GistAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.nurik.mynetshoes.gistList.adapter.FavoriteAdapter
import br.com.nurik.mynetshoes.gistDetail.DetailsActivity
import com.google.android.material.appbar.AppBarLayout



/**
 * Class where Gist is listed, beeing favorited or not
 */
class MainActivity : AppCompatActivity(), GistListContract.GistListMainView  {

    private var progressBar: ProgressBar? = null
    private var presenter: GistListContract.GistListPresenter? = null
    private var sharedPreferences: SharedPreferences? = null
    private var tabVisible = 1


    /**
     * Inflating layout, setting UI, getting presenter and sharedprefence instance,
     * during on activity creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
        presenter = GistListPresenter(this, GistListModel())
        setUI()
    }
    /**
     * During onResume method, the presenter is called to get GistList from API
     */
    override fun onResume() {
        super.onResume()
        requestData()
    }

    /**
     * Removing presenter allocated, to release memory resource
     */
    override fun onDestroy() {
        super.onDestroy()
        if(presenter!=null){
            presenter!!.onDestroy()
        }
    }

    /**
     * UI widgets creation and setup
     */
    fun setUI() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initializeRecyclerView()
        progressBar = InteractionsUtils.initProgressBar(this)
        setSearchField()
    }

    fun setSearchField() {
        swipe_layout.setOnRefreshListener { requestData() }
        search_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0!!.length > 0) {
                    filterList(p0)

                    filterFav(p0)
                }
            }
        })
    }

    /**
     * Method to call favorite filter
     *
     * @param p0  text to consult when filter
     */
    fun filterFav(p0: CharSequence?) {
        if (recycler_view_fav != null && recycler_view_fav.adapter != null) {
            (recycler_view_fav.adapter as FavoriteAdapter).getFilter().filter(p0.toString());
            (recycler_view_fav.adapter as FavoriteAdapter).notifyDataSetChanged()
        }
    }


    /**
     * Method to call list filter
     *
     * @param p0  text to consult when filter
     */
    fun filterList(p0: CharSequence) {
        if (recycler_view != null && recycler_view.adapter != null) {
            (recycler_view.adapter as GistAdapter).getFilter().filter(p0.toString());
            (recycler_view.adapter as GistAdapter).notifyDataSetChanged()
        }
    }

    /**
     * requesting data from API and SharedPreferences to fill the recyclerViews
     */
    fun requestData() {
        if (presenter != null) {
            presenter!!.requestDataFromServer()
            presenter!!.requestFavoriteData(sharedPreferences!!)
        }
    }

    /**
     * Setting up recyclerViews, setting scroll listner into Gist default List
     */
    private fun initializeRecyclerView() {
        recycler_view_fav.layoutManager = LinearLayoutManager(this@MainActivity)

        recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
        recycler_view.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    if(presenter!=null){
                        presenter!!.loadMoreDataFromServer()
                    }
                }
            }
        })
    }

    /**
     * Implementing item click Adapter Listener, to handle caorrectly this action,
     *  in that case set or remove item from list, if Action Icon is clicked, or opening
     *  the details activity if the item body is clicked
     */
    private val recyclerItemClickListener = object : RecyclerItemClickListener {
        override fun onItemClick(gist: Gist, favoriteAction: Boolean) {
            if(favoriteAction){
                if(gist.favorite){
                    InteractionsUtils.createDialog(this@MainActivity, "Tem certeza que deseja retirar este item de Favoritos?",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                presenter!!.removeFavoriteGist(gist!!, sharedPreferences!!)
                                dialogInterface.dismiss()
                            })
                }else{
                    presenter!!.saveFavoriteGist(gist, sharedPreferences!!)
                }
            }else{
                var i =  Intent(this@MainActivity, DetailsActivity::class.java)
                i.putExtra("gist", gist)
                startActivity(i)
            }
        }
    }

    /**
     * method to call progressBar
     */
    override fun showProgress() {
        if(progressBar!=null){
            progressBar!!.visibility = View.VISIBLE
        }
    }

    /**
     * method to hide progressBar
     */
    override fun hideProgress() {
        if(progressBar!=null){
            progressBar!!.visibility = View.INVISIBLE
        }
    }

    /**
     * centering Deault and favorite recylerviews, and the swipeListener after interaction result
     *
     * @param favList       favorite List
     * @param searchList    result list
     */
    override fun setDataToRecyclerView(searchList: ArrayList<Gist>?, favList: ArrayList<Gist>?) {
        if (swipe_layout.isRefreshing()) {
            swipe_layout.setRefreshing(false);
        }
        setSearchRecycler(searchList)
        setFavRecycler(favList)
    }

    /**
     * setting favorite list from shared preferences
     *
     * @param favList       favorite list
     */
    fun setFavRecycler(favList: ArrayList<Gist>?) {
        if (favList != null) {
            val adapter = FavoriteAdapter(this, favList, recyclerItemClickListener)
            recycler_view_fav.adapter = adapter
            recycler_view_fav.adapter!!.notifyDataSetChanged()
            setRecyclerVisibility(favList, recycler_view_fav, 2)
            filterFav(search_field.text)
        }
    }

    /**
     * setting Result list from api
     *
     * @param searchList       result list
     */
    fun setSearchRecycler(searchList: ArrayList<Gist>?) {
        if (searchList != null) {
            val adapter = GistAdapter(this, searchList, recyclerItemClickListener)
            if (recycler_view.adapter == null) {
                recycler_view.adapter = adapter
            } else {
                recycler_view.adapter!!.notifyDataSetChanged()
            }
            filterList(search_field.text)
        }
    }

    fun setRecyclerVisibility(list: ArrayList<Gist>, view: View, tab: Int) {
        view.visibility = View.GONE
        if (list.size > 0 && tabVisible == tab) {
            view.visibility = View.VISIBLE
        }else if(list.size == 0 && tabVisible == tab){
            empty_view.visibility = View.VISIBLE
        }
    }

    /**
     * when something went wrong during a presente process, the exception will come here
     */
    override fun onResponseFailure(throwable: Throwable) {
        InteractionsUtils.showSnacknar(container, throwable.message.toString())
    }

    /**
     * Setting bottomNavigation listener to jump between default Gist list and Fsvorite list
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        showToolbar()
        when (item.itemId) {
            R.id.navigation_search-> {

                tabVisible = 1
                recycler_view.visibility = View.VISIBLE
                recycler_view_fav.visibility = View.GONE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorites -> {
                tabVisible = 2
                recycler_view.visibility = View.GONE
                recycler_view_fav.visibility = View.VISIBLE
                presenter!!.requestFavoriteData(sharedPreferences!!)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     * Method to reset the app barr appearence
     */
    fun showToolbar() {
        if (app_bar is AppBarLayout) {
            app_bar.setExpanded(true, true)
        }
    }
}
