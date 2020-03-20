package com.polinema.android.kotlin.dinaspariwisatav6.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.MainActivity
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_admin_main.*

class AdminMain : AppCompatActivity() {

    private val mSelected = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.navigationMyProfile -> {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("status", "Offline")
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationMyCourses -> {
                val fragment = AdminUploadFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationHome -> {
                val fragment = AdminUserFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationSearch -> {
                val fragment = AdminSearchFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationMenu -> {
                val fragment = AdminLogFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)
        val fragment = AdminUserFragment()
        addFragment(fragment)
        navigationM.setOnNavigationItemSelectedListener(mSelected)
        navigationM.selectedItemId = R.id.navigationHome
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
        Log.e(this.toString(), fragment.javaClass.simpleName)
    }
}
