package hu.bme.aut.qrvhfq.EnchantedEmporium.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.myapplication.R
@AndroidEntryPoint
class LoginRegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_reg)

    }
}