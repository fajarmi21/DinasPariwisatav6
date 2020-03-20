package com.polinema.android.kotlin.dinaspariwisatav6

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.polinema.android.kotlin.dinaspariwisatav6.Admin.AdminMain
import com.polinema.android.kotlin.dinaspariwisatav6.Super.SuperDashboard
import com.polinema.android.kotlin.dinaspariwisatav6.Super.SuperMain
import com.polinema.android.kotlin.dinaspariwisatav6.User.UserMain
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var db : FirebaseFirestore
    lateinit var auth: FirebaseAuth
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btLogin -> {
                var email = etUsername.text.toString()
                var password = etPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Username / password can't be empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage("Authenticating....")
                    progressDialog.show()

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) return@addOnCompleteListener
                            else {
                                db.collection("users")
                                    .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                                    .addOnSuccessListener {
                                        val level = it.get("level").toString()
                                        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .update("status", "Online")
                                        when (level) {
                                            "super admin" -> {
                                                val intent = Intent(this, SuperDashboard::class.java)
                                                startActivity(intent)
                                            }
                                            "admin" -> {
                                                val intent = Intent(this, AdminMain::class.java)
                                                startActivity(intent)
                                            }
                                            "user" -> {
                                                val intent = Intent(this, UserMain::class.java)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        progressDialog.hide()
                                        Log.e("tag", it.message)
                                        Toast.makeText(this, "Cannot get user by username : $email", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            progressDialog.hide()
                            Log.e("tag", it.message)
                            Toast.makeText(this, "Username / password incorrect", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        btLogin.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
//        setupPermissions()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI()
        }
    }

    private fun setupPermissions() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(baseContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(baseContext, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setRationaleTitle("Permission required")
            .setRationaleMessage(R.string.rationale_message)
            .setDeniedTitle("Permission denied")
            .setDeniedMessage(
                "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]"
            )
            .setGotoSettingButtonText("Settings")
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun updateUI() {
        db.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                val level = it.get("level").toString()
                when (level) {
                    "super admin" -> {
                        val intent = Intent(this, SuperDashboard::class.java)
                        startActivity(intent)
                    }
                    "admin" -> {
                        val intent = Intent(this, AdminMain::class.java)
                        startActivity(intent)
                    }
                    "user" -> {
                        val intent = Intent(this, UserMain::class.java)
                        startActivity(intent)
                    }
                }
            }
    }
}
