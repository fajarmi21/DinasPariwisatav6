package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_super_upload.*
import kotlinx.android.synthetic.main.item_super_upload.view.*

/**
 * A simple [Fragment] subclass.
 */
class SuperUploadFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var uploadAdapter: UploadAdapter
    var daftarUpload = mutableListOf<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_upload, container, false)
        db = FirebaseFirestore.getInstance()
        uploadAdapter = UploadAdapter(daftarUpload)
        return view
    }

    override fun onStart() {
        super.onStart()
        showData()
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
//            .replace(R.id.contentS, fragment, fragment.javaClass.simpleName)
            .add(R.id.contentSC, fragment, fragment.javaClass.simpleName)
            .addToBackStack("SuperUploadFragment")
            .commit()
        Log.e("TAG", fragment.javaClass.simpleName)
    }

    private fun showData() {
        daftarUpload.clear()
        db.collection("kegiatan").orderBy("file_tanggal", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                for (doc in it) {
                    val hm = HashMap<String, String>()
                    val ar = doc.get("file_url") as ArrayList<String>
                    val arr = doc.get("file_pendapatan") as ArrayList<*>
                    val ary = doc.get("file_tanggal") as ArrayList<*>
                    hm.put("file_id", doc.get("file_id").toString())
                    hm.put("upload", doc.get("file_nama").toString())
                    hm.put("tanggal", ary[1].toString())
                    hm.put("visitor", doc.get("file_pengunjung").toString())
                    hm.put("destine", doc.get("file_tempat").toString())
                    hm.put("income1", arr[0].toString())
                    hm.put("income2", arr[1].toString())
                    hm.put("income3", arr[2].toString())
                    hm.put("im1", ar[0])
                    hm.put("im2", ar[1])
                    hm.put("im3", ar[2])
                    hm.put("im4", ar[3])
                    hm.put("viv", ar[4])
                    daftarUpload.add(hm)
                }
                uploadAdapter.notifyDataSetChanged()
            }
        listUploadS.layoutManager = LinearLayoutManager(this.context)
        listUploadS.adapter = uploadAdapter
    }

    inner class UploadAdapter(val dataUpload: List<HashMap<String, String>>) :
        RecyclerView.Adapter<UploadAdapter.HolderUpload>() {
        inner class HolderUpload(v: View) : RecyclerView.ViewHolder(v) {
            val txUpload = v.findViewById<TextView>(R.id.txUserUpload)
            val txTanggal = v.findViewById<TextView>(R.id.txTanggal)
            val txPg = v.findViewById<TextView>(R.id.txTPengunjungS)
            val txPn = v.findViewById<TextView>(R.id.txTPendapatanS)
            val card = v.findViewById<CardView>(R.id.cvSU)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HolderUpload {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_super_upload, parent, false)
            return HolderUpload(v)
        }

        override fun getItemCount(): Int {
            return dataUpload.size
        }

        override fun onBindViewHolder(holder: HolderUpload, position: Int) {
            val data = dataUpload.get(position)
            holder.txUpload.text = data.get("upload")
            holder.txTanggal.text = data.get("tanggal")
            holder.txPg.text = data.get("income3")
            holder.txPn.text = data.get("visitor")
            holder.card.setOnClickListener {
                val fragment = SuperDetailUploadFragment()
                val bundle = Bundle()
                bundle.putString("id", data.get("file_id").toString())
                bundle.putString("user", data.get("upload").toString())
                bundle.putString("date", data.get("tanggal").toString())
                bundle.putString("destine", data.get("destine").toString())
                bundle.putString("visitor", data.get("visitor").toString())
                bundle.putString("income1", data.get("income1").toString())
                bundle.putString("income2", data.get("income2").toString())
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
