package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_super_chart_list.*
import kotlinx.android.synthetic.main.fragment_super_chart_list.view.*
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class SuperChartListFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var scAdapter: SCAdapter
    var daftarSC = mutableListOf<HashMap<String, Any>>()
    var query = Query.Direction.ASCENDING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_super_chart_list, container, false)
        db = FirebaseFirestore.getInstance()
        scAdapter = SCAdapter(daftarSC)

        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbarSC)
        activity!!.setActionBar(toolbar)
        v.collapsingSC.title = "Find some here..!!"
        v.collapsingSC.setExpandedTitleColor(android.R.drawable.screen_background_light_transparent)
        v.imageView4.setOnClickListener {
            query = Query.Direction.DESCENDING
            v.imageView5.visibility = View.VISIBLE
            v.imageView4.visibility = View.GONE
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
            showData(query)
        }
        v.imageView5.setOnClickListener {
            query = Query.Direction.ASCENDING
            v.imageView4.visibility = View.VISIBLE
            v.imageView5.visibility = View.GONE
            Toast.makeText(context, "click2", Toast.LENGTH_SHORT).show()
            showData(query)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        showData(query)
    }

    private fun showData(q: Query.Direction) {
        daftarSC.clear()
        db.collection("tour").orderBy("nama", q).get()
            .addOnSuccessListener {
                for (doc in it) {
//                    Log.e("dataeee", doc.get("nama").toString())
                    db.collection("kegiatan").whereEqualTo("file_tempat", doc.get("nama")).get()
                        .addOnSuccessListener {it2 ->
                            //                            Log.e("data", doc.get("nama").toString())
//                            Log.e("result", it2.isEmpty.toString())
                            var count = 0
                            var count2 = 0
                            for (doc2 in it2) {
//                                if (tanggal(doc2.get("file_tanggal")) == now()) {
                                    count += doc2.get("file_pengunjung").toString().toInt()
                                    count2 += doc2.get("file_pendapatan").toString().toInt()
//                                }
                            }
//                            Log.e("count", count.toString())
                            val hm = HashMap<String, Any>()
                            hm.put("id", doc.id)
                            hm.put("picture", doc.get("picture").toString())
                            hm.put("picture_name", doc.get("picture_name").toString())
                            hm.put("nama", doc.get("nama").toString())
                            hm.put("pengunjung", count.toString())
                            hm.put("pendapatan", count2.toString().toDouble())
                            daftarSC.add(hm)
                            scAdapter.notifyDataSetChanged()
//                            Log.e("daftar", daftarTour.count().toString())
                        }
                }
            }
        rvSuperChart.layoutManager = LinearLayoutManager(this.context)
        rvSuperChart.adapter = scAdapter
    }

    private fun rupiah(number : Any?): String{
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).toString() + ",00"
    }

    private fun ribuan(number : Int): String{
        val numberFormat = NumberFormat.getInstance()
        return numberFormat.format(number).toString() + " Orang"
    }

    private fun tanggal(tgl : Any?) : String {
        val formatter = DateTimeFormatter.ofPattern("dd - MM - yyyy")
        val date = LocalDate.parse(tgl.toString(), formatter).monthValue
        return date.toString()
    }

    private fun now() : String {
        val formatter = DateTimeFormatter.ofPattern("MM")
        return LocalDate.now().format(formatter)
    }

    inner class SCAdapter(val daftarSC: List<HashMap<String, Any>>) :
        RecyclerView.Adapter<SCAdapter.HolderSC>() {
        inner class HolderSC(v: View) : RecyclerView.ViewHolder(v) {
            val image = v.findViewById<ImageView>(R.id.imageViewST)
            val txName = v.findViewById<TextView>(R.id.txSTour)
            val txPengunjung = v.findViewById<TextView>(R.id.txTPengunjung)
            val txPendapatan = v.findViewById<TextView>(R.id.txTPendapatan)
            val btUpdate = v.findViewById<CardView>(R.id.cvST)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSC {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_super_tour, parent, false)
            return HolderSC(v)
        }

        override fun getItemCount(): Int {
            return daftarSC.size
        }

        override fun onBindViewHolder(holder: HolderSC, position: Int) {
            val data = daftarSC.get(position)
            Picasso.get().load(data.get("picture").toString()).into(holder.image)
            holder.txName.text = data.get("nama").toString()
            holder.txPengunjung.text = ribuan(data.get("pengunjung").toString().toInt())
            holder.txPendapatan.text = rupiah(data.get("pendapatan"))
        }

    }
}
