package com.polinema.android.kotlin.dinaspariwisatav6.Admin


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_admin_search.*
import kotlinx.android.synthetic.main.fragment_admin_search.view.*
import java.text.NumberFormat

/**
 * A simple [Fragment] subclass.
 */
class AdminSearchFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var storage: StorageReference
    lateinit var searchAdapter: AdapterSearch
    var daftarSearch = mutableListOf<HashMap<String, String>>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_search, container, false)
        storage = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        searchAdapter = AdapterSearch(daftarSearch)
        view.search.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("onQueryTextChange", newText)
                showData(newText.toString())
                return false
            }

        })
        return view
    }

    override fun onResume() {
        super.onResume()
        showData()
    }

    fun showData(search : String = "") {
        daftarSearch.clear()
        if (search.equals("")) {
            db.collection("kegiatan").orderBy("file_tanggal", Query.Direction.ASCENDING).get()
                .addOnSuccessListener {
                    for (doc in it) {
                        val hm = HashMap<String, String>()
                        val ar = doc.get("file_url") as ArrayList<String>
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
                        hm.put("im1", ar[0])
                        hm.put("im2", ar[1])
                        hm.put("im3", ar[2])
                        hm.put("im4", ar[3])
                        hm.put("viv", ar[4])
                        daftarSearch.add(hm)
                    }
                    searchAdapter.notifyDataSetChanged()
                }
        } else {
            db.collection("kegiatan").orderBy("file_tempat").startAt(search).endAt(search+"\uf8ff").get()
                .addOnCompleteListener {
                    for (doc in it.result!!.documents) {
                        val hm = HashMap<String, String>()
                        val ar = doc.get("file_url") as ArrayList<String>
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
                        hm.put("im1", ar[0])
                        hm.put("im2", ar[1])
                        hm.put("im3", ar[2])
                        hm.put("im4", ar[3])
                        hm.put("viv", ar[4])
                        daftarSearch.add(hm)
                    }
                    searchAdapter.notifyDataSetChanged()
                }
        }
        rvUserSearch.layoutManager = LinearLayoutManager(this.context)
        rvUserSearch.adapter = searchAdapter
    }

    private fun ribuan(number: Int): String{
        val numberFormat = NumberFormat.getInstance()
        return numberFormat.format(number).toString() + " Orang"
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
//            .replace(R.id.contentS, fragment, fragment.javaClass.simpleName)
            .add(R.id.content, fragment, fragment.javaClass.simpleName)
            .addToBackStack("AdminSearchFragment")
            .commit()
        Log.e("TAG", fragment.javaClass.simpleName)
    }

    inner class AdapterSearch(val dataSearch: List<HashMap<String, String>>) :
        RecyclerView.Adapter<AdapterSearch.HolderSearch>() {
        inner class HolderSearch(v: View) : RecyclerView.ViewHolder(v) {
            val txNumber = v.findViewById<TextView>(R.id.txSearchNumber)
            val txTempat = v.findViewById<TextView>(R.id.txSearchTempat)
            val txDate = v.findViewById<TextView>(R.id.txSearchDate)
            val txPengunjung = v.findViewById<TextView>(R.id.txSearchPengunjung)
            val cv = v.findViewById<CardView>(R.id.cvAS)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearch {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_search, parent, false)
            return HolderSearch(v)
        }

        override fun getItemCount(): Int {
            return dataSearch.size
        }

        override fun onBindViewHolder(holder: HolderSearch, position: Int) {
            val data = dataSearch.get(position)
            holder.txNumber.text = (position + 1).toString()
            holder.txTempat.text = "${data.get("file_tempat")} (${data.get("file_nama")})"
            holder.txDate.text = data.get("file_tanggal")
            holder.txPengunjung.text = "jumlah pengunjung : ${ribuan(data.get("income3").toString().toInt())}"
            holder.cv.setOnClickListener {
                val fragment = AdminDetailSearchFragment()
                val bundle = Bundle()
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
