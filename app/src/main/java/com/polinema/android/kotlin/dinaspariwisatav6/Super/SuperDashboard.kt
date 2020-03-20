package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_super_dashboard.*

class SuperDashboard : AppCompatActivity() {

    lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_dashboard)
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                txSDName.text = """( ${it.get("username").toString()} )"""
            }
        cvSHome.setOnClickListener {
            val intent = Intent(this, SuperMain::class.java)
            startActivity(intent)
        }

        cvSChart.setOnClickListener {
//            Toast.makeText(this, "chart click", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SuperFilter::class.java)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}
