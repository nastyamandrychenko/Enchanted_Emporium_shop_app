package hu.bme.aut.qrvhfq.EnchantedEmporium.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.ShoppingActivity
import hu.bme.aut.qrvhfq.myapplication.R

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomMenu
    )
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomMenu
    )
    bottomNavigationView.visibility = View.VISIBLE
}