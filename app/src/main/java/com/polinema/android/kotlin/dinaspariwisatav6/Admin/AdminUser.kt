package com.polinema.android.kotlin.dinaspariwisatav6.Admin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.activity_admin_user.*
import kotlinx.android.synthetic.main.fragment_user_upload.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.HashMap

class AdminUser : AppCompatActivity(), View.OnClickListener {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel_button -> {
                onBackPressed()
            }
            R.id.next_button -> {
                if (next_button.isClickable) {
                    if (!spinnerTour.isEnabled) Toast.makeText(
                        this,
                        "Tempat wisata telah digunakan semua. Mohon dicek ulang!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    else {
                        if (txaUsername.text.isNullOrEmpty() || txaEmail.text.isNullOrEmpty() || txaNumber.text.isNullOrEmpty() || txaAddress.text.isNullOrEmpty()) {
                            Toast.makeText(
                                this,
                                "Data tidak boleh kosong. Mohon dicek ulang!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                if (!txaUsername.text.isNullOrEmpty() && !txaEmail.text.isNullOrEmpty() && !txaNumber.text.isNullOrEmpty() && !txaAddress.text.isNullOrEmpty()) {
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .get()
                        .addOnSuccessListener {
                            val user = it["email"].toString()
                            val pass = it["password"].toString()

                            val ud = txaPassword.text.toString().replace("User", "")
                            val username = txaUsername.text.toString()
                            val email = txaEmail.text.toString()
                            val number = txaNumber.text.toString()
                            val address = txaAddress.text.toString()
                            val password = txaPassword.text.toString()
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
                                            "level" to "user",
                                            "password" to password,
                                            "number" to number,
                                            "address" to address,
                                            "picture" to picture
                                        )
                                        db.collection("users").document(id).set(data)
                                            .addOnSuccessListener {
                                                val hm = HashMap<String, Any>()
                                                hm.put("users", username)
                                                db.collection("tour").whereEqualTo(
                                                        "nama",
                                                        spinnerTour.selectedItem.toString()
                                                    ).get()
                                                    .addOnSuccessListener {
                                                        var i = ""
                                                        for (d in it) i = d.id
                                                        db.collection("tour").document(i).update(hm)
                                                            .addOnSuccessListener {
                                                                progressDialog.hide()
                                                                FirebaseAuth.getInstance().signOut()
                                                                auth.signInWithEmailAndPassword(user, pass)
                                                                    .addOnCompleteListener {
                                                                        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))
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
                                                                        val intent = Intent(this, AdminMain::class.java)
                                                                        startActivity(intent)
                                                                    }
                                                            }
                                                    }
                                            }
                                            .addOnFailureListener {
                                                progressDialog.hide()
                                                Log.w("gagal", "Error adding document", it)
                                                Toast.makeText(
                                                    this, "Error adding document with ID: ${it}",
                                                    Toast.LENGTH_LONG
                                                ).show()
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
        setContentView(R.layout.activity_admin_user)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        cancel_button.setOnClickListener(this)
        next_button.setOnClickListener(this)
        getUser()
        spin()
        getID()
        spinnerTour.isEnabled = true
    }

    private fun getUser() {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                val username = it.get("username").toString()
                txaUser.setText(username)
            }
    }

    private fun getID() {
        db.collection("users").whereEqualTo("level", "user").get()
            .addOnSuccessListener {
                var num = 1
                var pass = "User001"
                for (d in it) {
                    if (d["password"].toString().replace("User", "").toInt() >= num) {
                        num = d["password"].toString().replace("User", "").toInt() + 1
                        when {
                            num < 10 -> pass = "User00$num"
                            num < 100 -> pass = "User0$num"
                            num > 99 -> pass = "User$num"
                        }
                    }
                }
                txaPassword.setText(pass)
            }
    }

    private fun spin() {
        db.collection("tour").orderBy("nama", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                val hm = ArrayList<String>()
                for (doc in it) {
                    val ar = doc.get("event") as ArrayList<*>
                    if (ar[0] == "false" && doc["users"] == null) {
                        spinnerTour.isEnabled = true
                        hm.add(doc["nama"].toString())
                    }
                }
                if (hm.size == 0) spinnerTour.isEnabled = false
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hm)
                spinnerTour.adapter = adapter
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}
