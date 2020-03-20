package com.polinema.android.kotlin.dinaspariwisatav6.Admin


import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.User.UserListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import kotlinx.android.synthetic.main.item_admin_user.*
import kotlinx.android.synthetic.main.item_admin_user.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class AdminUserFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var userAdapter: AdapterUser
    var daftarUser = mutableListOf<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_blank, container, false)
        db = FirebaseFirestore.getInstance()
        userAdapter = AdapterUser(daftarUser)
        view.btInsert.setOnClickListener {
            val intent = Intent(this.context, AdminUser::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        txTotal.text = 0.toString()
        showData()
    }

    private fun showData() {
        daftarUser.clear()
        db.collection("users").whereEqualTo("level", "user")
            .get().addOnSuccessListener { result ->
                var cUser = 0
                for (doc in result) {
                    val hm = HashMap<String, String>()
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                        .addOnSuccessListener {
                            if (doc.get("status").toString() != "Deleted" ) {
                                hm.put("id", doc.get("id").toString())
                                hm.put("email1", it.get("email").toString())
                                hm.put("password1", it.get("password").toString())
                                hm.put("email", doc.get("email").toString())
                                hm.put("password", doc.get("password").toString())
                                hm.put("status", doc.get("status").toString())
                                hm.put(
                                    "username",
                                    doc.get("username").toString() + " " + doc.get("status").toString()
                                )
                                hm.put("level", doc.get("level").toString())
                                hm.put("img", doc.get("picture").toString())
                                hm.put("users", doc.get("username").toString())
                                daftarUser.add(hm)
                                cUser += 1
                            }
                            txTotal.text = cUser.toString()
                            userAdapter.notifyDataSetChanged()
                        }
                }
//                Log.e("add", cUser.toString())
            }
        listUser.layoutManager = LinearLayoutManager(this.context)
        listUser.adapter = userAdapter
    }

    inner class AdapterUser(val dataUser: List<HashMap<String, String>>) :
        RecyclerView.Adapter<AdapterUser.HolderUser>() {
        inner class HolderUser(v: View) : RecyclerView.ViewHolder(v) {
            val txNama = v.findViewById<TextView>(R.id.txNama)
            val txLevel = v.findViewById<TextView>(R.id.txLevel)
            val btDelete = v.findViewById<CardView>(R.id.cardAU)
            val img = v.findViewById<ImageView>(R.id.imageViewAU)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUser.HolderUser {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user, parent, false)
            return HolderUser(v)
        }

        override fun getItemCount(): Int {
            return dataUser.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: AdapterUser.HolderUser, position: Int) {
            val data = dataUser.get(position)
            when(data.get("status")) {
                "Online" -> {
                    val nn = SpannableString(data.get("username").toString())
                    nn.setSpan(ForegroundColorSpan(Color.GREEN), nn.indexOf(" Online"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txNama.text = nn
                }
                "Offline" -> {
                    val nn = SpannableString(data.get("username").toString())
                    nn.setSpan(ForegroundColorSpan(Color.RED), nn.indexOf(" Offline"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txNama.text = nn
                }
                "Deleted" -> {
                    val nn = SpannableString(data.get("username").toString())
                    nn.setSpan(StrikethroughSpan(), 0, nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txNama.text = nn
                }
                else -> {
                    holder.txNama.text = data.get("username").toString().replace(" null","")
                }
            }
            holder.txLevel.text = data.get("level") + " (" + data.get("email") + ")"
            Picasso.get().load(data.get("img")).into(holder.img)
            holder.btDelete.setOnClickListener {
                val a = ArrayList<Any>()
                a.add(data["id"].toString())
                this@AdminUserFragment.context?.let { it1 -> a.add(it1) }
                activity?.let { it1 -> a.add(it1) }
                a.add(data["email"].toString())
                a.add(data["password"].toString())
                a.add(data["email1"].toString())
                a.add(data["password1"].toString())
                a.add(data["users"].toString())
                ADF(a).show(requireFragmentManager(), "TAG")
            }
        }
    }

    class ADF(
        a : ArrayList<Any>
    ) : DialogFragment() {

        private val id = a[0]
        private val c = a[1] as Context
        private val ac = a[2] as FragmentActivity
        private val e = a[3].toString()
        private val p = a[4].toString()
        private val e1 = a[5].toString()
        private val p1 = a[6].toString()
        private val u = a[7].toString()

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            Log.e("email", e)
            Log.e("password", p)
            Log.e("email1", e1)
            Log.e("password1", p1)
            return MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Theme_MaterialComponents_Light_Dialog_Alert
            )
                .setTitle("Delete (${id})")
                .setMessage("are you sure you want to delete this user?")
                .setPositiveButton("Yes") { _, _ ->
                    val progressDialog = ProgressDialog(c)
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage("Deleting....")
                    progressDialog.show()

                    FirebaseAuth.getInstance().signOut()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(e, p)
                        .addOnSuccessListener { it1 ->
                            val user = FirebaseAuth.getInstance().currentUser!!
                            val uid = user.uid
                            val credential = EmailAuthProvider.getCredential(e, p)
                            user.reauthenticate(credential)
                                .addOnCompleteListener { it2 ->
                                    FirebaseFirestore.getInstance().collection("tour").whereEqualTo("users", u).get()
                                        .addOnCompleteListener {
                                            for (d in it.result!!.documents) {
                                                val i = d.id
                                                FirebaseFirestore.getInstance().collection("tour").document(i).update(
                                                    "users", FieldValue.delete()
                                                )
                                            }
                                        }
                                    FirebaseFirestore.getInstance().collection("users").document(uid).delete()
                                        .addOnSuccessListener {
                                            user.delete()
                                                .addOnCompleteListener { it3 ->
                                                    FirebaseAuth.getInstance().signOut()
                                                    FirebaseAuth.getInstance()
                                                        .signInWithEmailAndPassword(e1, p1)
                                                        .addOnSuccessListener { it4 ->
                                                            val date = LocalDate.now().format(
                                                                DateTimeFormatter.ofPattern("dd - MM - yyyy"))
                                                                FirebaseFirestore.getInstance().collection("logs").whereEqualTo("email", e1).get()
                                                                    .addOnSuccessListener { it5 ->
                                                                        var ide = ""
                                                                        var x = ArrayList<java.util.HashMap<String, ArrayList<String>>>()
                                                                        var y = 0
                                                                        Log.e("id", it5.count().toString())
                                                                        for (d in it5) {
                                                                            ide = d.id
                                                                            x = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                                                                            y = x[0].size
                                                                            Log.e("id", y.toString())
                                                                        }
                                                                        if (it5.count() != 0) {
                                                                            x[0].put((y + 1).toString(), arrayListOf("delete user(${id})", date))
                                                                            FirebaseFirestore.getInstance().collection("logs").document(ide).update(
                                                                                "log", x
                                                                            )
                                                                        } else {
                                                                            val hm = hashMapOf<String, Any>()
                                                                            val hm2 = hashMapOf<String, Any>()
                                                                            val email = FirebaseAuth.getInstance().currentUser!!.email
                                                                            hm2.put("1", arrayListOf("delete user(${id})", date))
                                                                            hm.put("email", email.toString())
                                                                            hm.put("log", arrayListOf(hm2))
                                                                            FirebaseFirestore.getInstance().collection("logs").document().set(hm)
                                                                        }
                                                                    }

                                                            progressDialog.hide()
                                                            Toast.makeText(
                                                                c,
                                                                "deleted users($uid) is succesfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            ac.supportFragmentManager.findFragmentByTag("AdminUserFragment")!!.onStart()
                                                            Log.e("email", e1)
                                                            Log.e("password", p1)
                                                        }
                                                        .addOnFailureListener { it4 ->
                                                            progressDialog.hide()
                                                            Toast.makeText(
                                                                c,
                                                                "deleted users($uid) is unsuccesfully",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            Log.e("fail1", it4.message)
                                                        }
                                                }
                                        }
                                }
                                .addOnFailureListener { it2 ->
                                    progressDialog.hide()
                                    Toast.makeText(
                                        c,
                                        "deleted users($uid) is unsuccesfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("fail", it2.message)
                                }
                        }
                        .addOnFailureListener {
                            progressDialog.hide()
                            Toast.makeText(
                                c,
                                "deleted users is unsuccesfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("fail", it.message)
                        }
                }
                .setNegativeButton("No") { _, _ -> null }
                .create()

        }
    }
}
