package com.polinema.android.kotlin.dinaspariwisatav6.User


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_user_list.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var storage: StorageReference
    lateinit var listAdapter: AdapterList
    var daftarList = mutableListOf<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_list, container, false)
        storage = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        listAdapter = AdapterList(daftarList)
        return view
    }

//    override fun onStart() {
//        super.onStart()
//        showData()
//    }

    override fun onResume() {
        super.onResume()
        showData()
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
//            .replace(R.id.contentS, fragment, fragment.javaClass.simpleName)
            .add(R.id.contentU, fragment, fragment.javaClass.simpleName)
            .addToBackStack("UserListFragment")
            .commit()
//        Log.e(SuperTourFragment.TAG, fragment.javaClass.simpleName)
    }

    fun showData() {
        daftarList.clear()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {it2 ->
                val username = it2.get("username").toString()
                db.collection("kegiatan").whereEqualTo("file_nama", username).get()
                    .addOnCompleteListener {
                        for (doc in it.result!!.documents) {
                            val hm = HashMap<String, String>()
                            val ar = doc.get("file_url") as ArrayList<*>
                            val arr = doc.get("file_pendapatan") as ArrayList<*>
                            val ary = doc.get("file_tanggal") as ArrayList<*>
                            hm.put("doc", doc.id)
                            hm.put("file_id", doc.get("file_id").toString())
                            hm.put("file_nama", doc.get("file_nama").toString())
                            hm.put("file_tempat", doc.get("file_tempat").toString())
                            hm.put("file_tanggal", ary[1].toString())
                            hm.put("file_pengunjung", doc.get("file_pengunjung").toString())
                            hm.put("income1", arr[0].toString())
                            hm.put("income2", arr[1].toString())
                            hm.put("income3", arr[2].toString())
                            hm.put("im1", ar[0].toString())
                            hm.put("im2", ar[1].toString())
                            hm.put("im3", ar[2].toString())
                            hm.put("im4", ar[3].toString())
                            hm.put("viv", ar[4].toString())
                            daftarList.add(hm)
                        }
                        listAdapter.notifyDataSetChanged()
                    }

                rcList.layoutManager = LinearLayoutManager(this.context)
                rcList.adapter = listAdapter
            }
    }

    inner class AdapterList(val dataList: List<HashMap<String, String>>) :
        RecyclerView.Adapter<AdapterList.HolderList>() {
        inner class HolderList(v: View) : RecyclerView.ViewHolder(v) {
            val txNumber = v.findViewById<TextView>(R.id.txListNumber)
            val txTempat = v.findViewById<TextView>(R.id.txListTempat)
            val txDate = v.findViewById<TextView>(R.id.txListDate)
            val txPengunjung = v.findViewById<TextView>(R.id.txListPengunjung)
            val cv = v.findViewById<CardView>(R.id.cvUL)
//            val btDelete = v.findViewById<ImageButton>(R.id.btList_delete)
        }

        private fun ribuan(number: Int): String {
            val numberFormat = NumberFormat.getInstance()
            return numberFormat.format(number).toString()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderList {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false)
            return HolderList(v)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: HolderList, position: Int) {
            val data = dataList.get(position)
            holder.txNumber.text = (position + 1).toString()
            holder.txTempat.text = "${data.get("file_tempat")}"
            holder.txDate.text = data.get("file_tanggal")
            holder.txPengunjung.text = "jumlah pengunjung : ${ribuan(data.get("income3")!!.toInt())}"
            holder.cv.setOnClickListener {
                val fragment = UserDetailListFragment()
                val bundle = Bundle()
                bundle.putString("pos", (position + 1).toString())
                bundle.putString("doc", data.get("doc").toString())
                bundle.putString("id", data.get("file_id").toString())
                bundle.putString("user", data.get("file_nama").toString())
                bundle.putString("date", data.get("file_tanggal").toString())
                bundle.putString("destine", data.get("file_tempat").toString())
                bundle.putString("visitor", data.get("file_pengunjung").toString())
                bundle.putString("income1", data.get("income1").toString())
                bundle.putString("income2", data.get("income2").toString())
                bundle.putString("income3", data.get("income3").toString())
                bundle.putString("im1", data.get("im1").toString())
                bundle.putString("im2", data.get("im2").toString())
                bundle.putString("im3", data.get("im3").toString())
                bundle.putString("im4", data.get("im4").toString())
                bundle.putString("viv", data.get("viv").toString())
                fragment.arguments = bundle
                addFragment(fragment)
            }
        }

    }
}
