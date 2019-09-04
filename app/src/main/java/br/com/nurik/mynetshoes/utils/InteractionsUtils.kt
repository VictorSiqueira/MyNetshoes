package br.com.nurik.mynetshoes.utils

import android.R
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

/**
 * Class to centralize all User's interactions
 */
object InteractionsUtils {

    /**
     * Method which create a ProgressBar inside a view during a data loading
     *
     * @param ctx       Context to show the progressBar
     * @return          the progressBar to be handled by show and hide methods
     */
    fun initProgressBar(ctx: AppCompatActivity): ProgressBar {
        var progressBar = ProgressBar(ctx, null, R.attr.progressBarStyleLarge)
        progressBar!!.setIndeterminate(true)

        val relativeLayout = RelativeLayout(ctx)
        relativeLayout.gravity = Gravity.CENTER
        relativeLayout.addView(progressBar)

        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        progressBar!!.setVisibility(View.INVISIBLE)

        ctx.addContentView(relativeLayout, params)
        return progressBar
    }

    /**
     * Funcion to centralize Snackbar creation only
     */
    fun showSnacknar(view: View, msg: String){
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }

    /**
     * When needs to ask something to user, this method is called to keep a dialog, and make the
     * right interaction with user
     */
    fun createDialog(ctx: Context, msg: String, listener: DialogInterface.OnClickListener){
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage(msg)

        builder.setPositiveButton("Sim",listener)
        builder.setNegativeButton("Cancelar"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }
}
