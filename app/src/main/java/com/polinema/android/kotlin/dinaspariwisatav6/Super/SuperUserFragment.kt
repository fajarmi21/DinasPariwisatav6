package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.Admin.AdminUserFragment
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_super_user.*
import kotlinx.android.synthetic.main.fragment_super_user.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class SuperUserFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var adminAdapter: AdapterAdmin
    var daftarAdmin = mutableListOf<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_user, container, false)
        db = FirebaseFirestore.getInstance()
        adminAdapter = AdapterAdmin(daftarAdmin)
        view.btsInsert.setOnClickListener {
            val intent = Intent(this.context, SuperUser::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        txsTotal.text = "0"
        showData()
    }

    private fun showData() {
        daftarAdmin.clear()
        val collection = db.collection("users")
        collection.document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {it4 ->
                collection.whereEqualTo("level", "super admin").get()
                    .addOnSuccessListener {
                        var cAdmin = 0
                        for (doc in it) {
                            val hm = HashMap<String, String>()
                            hm.put("id", doc.get("id").toString())
                            hm.put("email1", it4.get("email").toString())
                            hm.put("password1", it4.get("password").toString())
                            hm.put("username", doc.get("username").toString())
                            hm.put("level", doc.get("level").toString())
                            hm.put("status", doc.get("status").toString())
                            hm.put("uri", doc.get("picture").toString())
                            hm.put("email", doc.get("email").toString())
                            hm.put("password", doc.get("password").toString())
                            daftarAdmin.add(hm)
                            cAdmin += 1
                        }
                        collection.whereEqualTo("level", "admin").get()
                            .addOnSuccessListener { result ->
                                for (doc2 in result) {
                                    val hm2 = HashMap<String, String>()
                                    hm2.put("id", doc2.get("id").toString())
                                    hm2.put("email1", it4.get("email").toString())
                                    hm2.put("password1", it4.get("password").toString())
                                    hm2.put("username", doc2.get("username").toString())
                                    hm2.put("status", doc2.get("status").toString())
                                    hm2.put("level", doc2.get("level").toString())
                                    hm2.put("uri", doc2.get("picture").toString())
                                    hm2.put("email", doc2.get("email").toString())
                                    hm2.put("password", doc2.get("password").toString())
                                    daftarAdmin.add(hm2)
                                    cAdmin += 1
                                }
                                collection.whereEqualTo("level", "user").get()
                                    .addOnSuccessListener { result2 ->
                                        for (doc3 in result2) {
                                            val hm3 = HashMap<String, String>()
                                            hm3.put("id", doc3.get("id").toString())
                                            hm3.put("email1", it4.get("email").toString())
                                            hm3.put("password1", it4.get("password").toString())
                                            hm3.put("username", doc3.get("username").toString())
                                            hm3.put("status", doc3.get("status").toString())
                                            hm3.put("level", doc3.get("level").toString())
                                            hm3.put("uri", doc3.get("picture").toString())
                                            hm3.put("email", doc3.get("email").toString())
                                            hm3.put("password", doc3.get("password").toString())
                                            daftarAdmin.add(hm3)
                                            cAdmin += 1
                                        }
                                        txsTotal.text = cAdmin.toString()
                                    }
                            }
                        adminAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Log.e("tag", it.toString())
                        txsTotal.text = "0"
                    }
                listAdmin.layoutManager = LinearLayoutManager(this.context)
                listAdmin.adapter = adminAdapter
            }
    }

    inner class AdapterAdmin(val dataAdmin : List<HashMap<String, String>>) :
        RecyclerView.Adapter<AdapterAdmin.HolderAdmin>() {
        inner class HolderAdmin(v: View) : RecyclerView.ViewHolder(v) {
            val image = v.findViewById<ImageView>(R.id.imageViewS)
            val txNama = v.findViewById<TextView>(R.id.txsNama)
            val txLevel = v.findViewById<TextView>(R.id.txsLevel)
            val txEmail = v.findViewById<TextView>(R.id.txsEmail)
            val txPassword = v.findViewById<TextView>(R.id.txsPassword)
            val btDelete = v.findViewById<ImageButton>(R.id.bts_deleteU)
            val card = v.findViewById<CardView>(R.id.cardSU)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAdmin {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_super_user, parent, false)
            return HolderAdmin(v)
        }

        override fun getItemCount(): Int {
            return dataAdmin.size
        }

        override fun onBindViewHolder(holder: HolderAdmin, position: Int) {
            val data = dataAdmin.get(position)
            val ste = data.get("username").toString() +" "+ data.get("status").toString()
            if (position == 0) holder.btDelete.visibility = View.INVISIBLE
            holder.image.setOnClickListener {
                Toast.makeText(context, "Click$position", Toast.LENGTH_SHORT).show()
                Log.e("picture", data.get("uri"))
            }
            when(data.get("status")) {
                "Online" -> {
                    val nn = SpannableString(ste)
                    nn.setSpan(ForegroundColorSpan(Color.GREEN), nn.indexOf(" Online"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txNama.text = nn
                }
                "Offline" -> {
                    val nn = SpannableString(ste)
                    nn.setSpan(ForegroundColorSpan(Color.RED), nn.indexOf(" Offline"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txNama.text = nn
                }
                else -> {
                    holder.txNama.text = data.get("username")
                }
            }
            Picasso.get().load(data.get("uri")).into(holder.image)
            holder.txLevel.text = data.get("level")
            holder.txEmail.text = data.get("email")
            holder.txPassword.text = data.get("password")
            if (position != 0) {
                holder.card.setOnClickListener {
                    val a = ArrayList<Any>()
                    a.add(data["id"].toString())
                    this@SuperUserFragment.context?.let { it1 -> a.add(it1) }
                    activity?.let { it1 -> a.add(it1) }
                    a.add(data["email"].toString())
                    a.add(data["password"].toString())
                    a.add(data["email1"].toString())
                    a.add(data["password1"].toString())
                    ADF(a).show(requireFragmentManager(), "TAG")
                }
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

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Theme_MaterialComponents_Light_Dialog_Alert
            )
                .setTitle("Delete (${id})")
                .setMessage("are you sure you want to delete this admin?")
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
                                                            ac.supportFragmentManager.findFragmentByTag("SuperUserFragment")!!.onStart()
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
