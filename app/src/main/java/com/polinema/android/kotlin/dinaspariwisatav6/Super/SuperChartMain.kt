package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.polinema.android.kotlin.dinaspariwisatav6.MainActivity
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_super_chart_main.*

class SuperChartMain : AppCompatActivity() {

    private val mSelected = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.navigationSCList -> {
                val f = SuperExcelFragment()
                addFragment(f)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationSCChart -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationSCLogout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_chart_main)
        navigationSC.setOnNavigationItemSelectedListener(mSelected)
        navigationSC.selectedItemId = R.id.navigationSCList
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentS, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}
