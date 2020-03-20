package com.polinema.android.kotlin.dinaspariwisatav6.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.MainActivity
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_user_main.*

class UserMain : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)
        navigationU.setOnNavigationItemSelectedListener(mSelected)
        navigationU.selectedItemId = R.id.navigationHomeU
    }

    private val mSelected = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.navigationLogU -> {
                val fragment = UserLogFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationUploadU -> {
                val fragment = UserListFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationHomeU -> {
                val fragment = UserUploadFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationTourU -> {
                val fragment = UserTourFragment()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigationMyProfileU -> {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("status", "Offline")
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentU, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}
