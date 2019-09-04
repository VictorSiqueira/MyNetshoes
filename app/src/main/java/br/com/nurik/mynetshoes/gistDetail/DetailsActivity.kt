package br.com.nurik.mynetshoes.gistDetail

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import br.com.nurik.mynetshoes.R
import br.com.nurik.mynetshoes.pojo.Gist
import br.com.nurik.mynetshoes.utils.InteractionsUtils
import br.com.nurik.mynetshoes.utils.InteractionsUtils.createDialog
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_details.*

/**
 * Class where Gist's Details is showed to user
 */
class DetailsActivity : AppCompatActivity(), GistContract.MainView {

    private var isFavorite = false
    private var gist: Gist? = null
    private var progressBar: ProgressBar? = null
    private var presenter: GistContract.presenter? = null
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Inflating layout, setting UI, getting presenter and sharedprefence instance,
     * during on activity creation
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setUI()
        presenter = GistPresenter(this, GistModel())
        sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)
    }

    /**
     * During onResume method, the presenter is called to get Gist from intent,
     * is set the favorite field according Gist, to show in fab the correct icon
     */
    override fun onResume() {
        super.onResume()
        presenter!!.getGist(intent)
        isFavorite = gist!!.favorite
        checkFabIcon()
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
        setToolbar()
        setFab()
        progressBar = InteractionsUtils.initProgressBar(this)
    }

    /**
     * setting FAB appearance and click listenter, where is possible
     * add or remove the gist from favorite list
     */
    fun setFab() {
        fab.setOnClickListener { view ->
            if(gist!=null) {
                if (isFavorite) {
                    createDialog(this, "Tem certeza que deseja retirar este item de Favoritos?",
                            DialogInterface.OnClickListener {dialogInterface, i ->
                                presenter!!.removeFavoriteGist(gist!!, sharedPreferences!!)
                                dialogInterface.dismiss()
                            })
                } else {
                    presenter!!.saveFavoriteGist(gist!!, sharedPreferences!!)
                }
            }
        }
    }

    /**
     * Method to check which icon will be set according favorite field
     */
    fun checkFabIcon() {
        if (isFavorite) {
            Glide.with(this).load(R.drawable.ic_favorite_on).into(fab)
        } else {
            Glide.with(this).load(R.drawable.ic_favorite_off).into(fab)
        }
    }

    /**
     * Setuo of collapse toolbar, putting Owner name and Avatar inside
     */
    fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })
    }

    /**
     * method to call progressBar
     */
    override fun showProgress() {
        if(progressBar!=null){
            progressBar!!.visibility  = View.VISIBLE
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
     * when something went wrong during a presente process, the exception will come here
     */
    override fun onResponseFailure(throwable: Throwable) {
        Snackbar.make(details_container, throwable.message.toString(), Snackbar.LENGTH_LONG).show()
    }

    /**
     * when favorite action be successfully released during
     * a presente process, the result will come here
     */
    override fun onFavorite(favorite: Boolean) {
        isFavorite = favorite
        checkFabIcon()
        Snackbar.make(details_container, resources.getString(R.string.successful_action), Snackbar.LENGTH_LONG)
                //.setAction("Action", null) //Fazer Undo e OK
                .show()
    }

    /**
     * Setting Gist Owner data into toolbar
     */
    override fun setGistData(gist: Gist) {
        if (gist != null && gist!!.owner != null) {
            this.gist = gist
            Glide.with(this).load(gist!!.owner!!.avatar_url).into(details_image)
            collapsing_toolbar.title = gist!!.owner!!.login
        }
    }
}
