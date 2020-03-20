package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.Admin.AdminMain
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_admin_user.*
import kotlinx.android.synthetic.main.activity_super_user.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.HashMap

class SuperUser : AppCompatActivity(), View.OnClickListener {

    lateinit var db : FirebaseFirestore
    lateinit var auth: FirebaseAuth
    companion object {
        val TAG = "coba"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel_button_super -> {
                onBackPressed()
            }
            R.id.next_button_super -> {
                if (!txaUsername_super.text.isNullOrEmpty() && !txaEmail_super.text.isNullOrEmpty() && !txaNumber_super.text.isNullOrEmpty() && !txaAddress_super.text.isNullOrEmpty()) {
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .get()
                        .addOnSuccessListener {
                            val user = it["email"].toString()
                            val pass = it["password"].toString()

                            val ud = txaPasswordSU.text.toString().replace("Admin", "")
                            val username = txaUsername_super.text.toString()
                            val email = txaEmail_super.text.toString()
                            val number = txaNumber_super.text.toString()
                            val address = txaAddress_super.text.toString()
                            val password = txaPasswordSU.text.toString()
                            val picture =
                                "https://firebasestorage.googleapis.com/v0/b/dinas-pariwisata-8a1e8.appspot.com/" +
                                        "o/default-user.png?alt=media&token=93493a07-ca34-4673-8de2-5fb58591da72"

                            val progressDialog = ProgressDialog(this)
                            progressDialog.isIndeterminate = true
                            progressDialog.setMessage("Uploading....")
                            progressDialog.show()

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val id = auth.currentUser!!.uid
                                        val data = hashMapOf(
                                            "id" to ud,
                                            "username" to username,
                                            "email" to email,
                                            "level" to "admin",
                                            "password" to password,
                                            "number" to number,
                                            "address" to address,
                                            "picture" to picture
                                        )
                                        db.collection("users").document(id).set(data)
                                            .addOnSuccessListener {
                                                progressDialog.hide()
                                                FirebaseAuth.getInstance().signOut()
                                                auth.signInWithEmailAndPassword(user, pass)
                                                    .addOnCompleteListener {
                                                        val date = LocalDate.now().format(
                                                            DateTimeFormatter.ofPattern("dd - MM - yyyy"))
                                                        db.collection("logs")
                                                            .whereEqualTo("email",  user).get()
                                                            .addOnSuccessListener { it5 ->
                                                                var id = ""
                                                                var x = ArrayList<HashMap<String, ArrayList<String>>>()
                                                                var y = 0
                                                                Log.e("cou", it5.count().toString())
                                                                for (d in it5) {
                                                                    id = d.id
                                                                    x = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                                                                    y = x[0].size
                                                                    Log.e("id", y.toString())
                                                                    Log.e("id", id)
                                                                }
                                                                if (it5.count() != 0) {
                                                                    x[0].put((y + 1).toString(), arrayListOf("add user(${ud})", date))
                                                                    db.collection("logs").document(id).update("log", x)
                                                                } else {
                                                                    val hm = hashMapOf<String, Any>()
                                                                    val hm2 = hashMapOf<String, Any>()
                                                                    val email = FirebaseAuth.getInstance().currentUser!!.email
                                                                    hm2.put("1", arrayListOf("add user(${ud})", date))
                                                                    hm.put("email", email.toString())
                                                                    hm.put("log", arrayListOf(hm2))
                                                                    db.collection("logs").document().set(hm)
                                                                }
                                                            }
                                                            .addOnFailureListener {
                                                                Log.e("fail", it.message)
                                                            }

                                                        Log.d("sukses", "DocumentSnapshot added with ID: ${id}")
                                                        Toast.makeText(this, "DocumentSnapshot added with ID: ${user}", Toast.LENGTH_LONG).show()
                                                        val intent = Intent(this, SuperMain::class.java)
                                                        startActivity(intent)
                                                    }
                                            }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressDialog.hide()
                                        Log.w("gagal", "createUserWithEmail:failure", it.exception)
                                        Toast.makeText(
                                            this, "Added user failed.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_user)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        getUser()
        getID()
        cancel_button_super.setOnClickListener(this)
        next_button_super.setOnClickListener(this)
    }

    private fun getUser() {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                val username = it.get("username").toString()
                txaUser_super.setText(username)
            }
    }

    private fun getID() {
        db.collection("users").whereEqualTo("level", "admin").get()
            .addOnSuccessListener {
                var num = 1
                var pass = "Admin001"
                for (d in it) {
                    if (d["password"].toString().replace("Admin", "").toInt() >= num) {
                        num = d["password"].toString().replace("Admin", "").toInt() + 1
                        when {
                            num < 10 -> pass = "Admin00$num"
                            num < 100 -> pass = "Admin0$num"
                            num > 99 -> pass = "Admin$num"
                        }
                    }
                }
                txaPasswordSU.setText(pass)
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}
