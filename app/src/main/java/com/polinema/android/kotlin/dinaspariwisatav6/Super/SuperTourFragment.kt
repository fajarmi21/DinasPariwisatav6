package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_super_tour.*
import kotlinx.android.synthetic.main.fragment_super_tour.view.*
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class SuperTourFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var tourAdapter: TourAdapter
    var daftarTour = mutableListOf<HashMap<String, Any>>()

    companion object {
        val TAG = "TAG"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_tour, container, false)
        db = FirebaseFirestore.getInstance()
        tourAdapter = TourAdapter(daftarTour)
        getCountTour()
        view.btsInsertT.setOnClickListener {
            val fragment = SuperAddTourFragment()
            addFragment(fragment)
        }

        val frag = activity!!.supportFragmentManager
        frag.addOnBackStackChangedListener {
//            Log.e("TAG", "coba")
            showData()
        }
        return view
    }

//    override fun onStart() {
//        super.onStart()
//        showData()
//    }

    override fun onResume() {
        super.onResume()
//        Log.e("TAG", "resume")
        showData()
    }

    private fun showData() {
        daftarTour.clear()
        db.collection("tour").orderBy("nama", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                for (doc in it) {
                    Log.e("dataeee", doc.get("nama").toString())
                    db.collection("kegiatan").whereEqualTo("file_tempat", doc.get("nama")).get()
                        .addOnSuccessListener {it2 ->
                            Log.e("data", doc.get("nama").toString())
//                            Log.e("result", it2.isEmpty.toString())
                            val hm = HashMap<String, Any>()
                            val ar = doc.get("event") as ArrayList<*>
                            var count = 0
                            var count2 = 0
                            for (doc2 in it2) {
                                val ary = doc2.get("file_tanggal") as ArrayList<*>
                                when(ary[0]) {
                                    "Today" -> if (ary[1] == LocalDate.now().format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))) {
                                        val arr = doc2.get("file_pendapatan") as ArrayList<*>
                                        count += doc2.get("file_pengunjung").toString().toInt()
                                        count2 += arr[2].toString().toInt()
                                        Log.e("incomeeee", count2.toString())
                                    }
                                    "Month" -> if (ary[1] == LocalDate.now().format(DateTimeFormatter.ofPattern("MM - yyyy"))) {
                                        val arr = doc2.get("file_pendapatan") as ArrayList<*>
                                        count += doc2.get("file_pengunjung").toString().toInt()
                                        count2 += arr[2].toString().toInt()
                                        Log.e("incomeeee", count2.toString())
                                    }
                                }
                            }
                            when {
                                ar[0] ==  "false" -> {
                                    hm.put("id", doc.id)
                                    hm.put("picture", doc.get("picture").toString())
                                    hm.put("picture_name", doc.get("picture_name").toString())
                                    hm.put("nama", doc.get("nama").toString())
                                    hm.put("name", doc.get("nama").toString())
                                    hm.put("ev", ar[0])
                                    hm.put("pengunjung", count.toString())
                                    hm.put("pendapatan", count2.toString().toDouble())
                                    daftarTour.add(hm)
                                    tourAdapter.notifyDataSetChanged()
                                }
                                ar[0] == "true" -> {
                                    hm.put("id", doc.id)
                                    hm.put("picture", doc.get("picture").toString())
                                    hm.put("picture_name", doc.get("picture_name").toString())
                                    when {
                                        tgl(1, ar[1].toString()) <= 0 && tgl(
                                            2,
                                            null,
                                            ar[2].toString()
                                        ) >= 0 -> {
                                            val nn = doc.get("nama").toString() + " (event) active"
                                            hm.put("kode", 0)
                                            hm.put("nama", nn)
                                        }
                                        else -> {
                                            val nn = doc.get("nama").toString() + " (event) inactive"
                                            hm.put("kode", 1)
                                            hm.put("nama", nn)
                                        }
                                    }
                                    hm.put("name", doc.get("nama").toString())
                                    hm.put("ev", ar[0])
                                    hm.put("tgl1", ar[1])
                                    hm.put("tgl2", ar[2])
                                    hm.put("pengunjung", count.toString())
                                    hm.put("pendapatan", count2.toString().toDouble())
                                    daftarTour.add(hm)
                                    tourAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                }
            }
        listTour.layoutManager = LinearLayoutManager(this.context)
        listTour.adapter = tourAdapter
    }

    private fun tgl(code: Int, tg1: String? = null, tg2: String? = null) : Int {
        var date = 1L
        when(code) {
            1 -> date = ChronoUnit.DAYS.between(LocalDate.parse(tg1, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now())
            2 -> date = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(tg2, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        return date.toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun getCountTour() {
        db.collection("tour").get()
            .addOnSuccessListener {
                var x = 0
                for (doc in it.documents) {
                    x++
                }
                when(x) {
                    0 -> txsTotalT.text = "000"
                    in 1..9 -> txsTotalT.setText("00$x")
                    in 10..99 -> txsTotalT.setText("0$x")
                    else -> txsTotalT.setText("$x")
                }
//                Log.e(TAG + " sukses", x.toString())
            }
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
//            .replace(R.id.contentS, fragment, fragment.javaClass.simpleName)
            .add(R.id.contentSC, fragment, fragment.javaClass.simpleName)
            .addToBackStack("SuperTourFragment")
            .commit()
//        Log.e(TAG, fragment.javaClass.simpleName)
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

    inner class TourAdapter(val dataTour: List<HashMap<String, Any>>) :
        RecyclerView.Adapter<TourAdapter.HolderTour>() {
        inner class HolderTour(v: View) : RecyclerView.ViewHolder(v) {
            val image = v.findViewById<ImageView>(R.id.imageViewST)
            val txName = v.findViewById<TextView>(R.id.txSTour)
            val txPengunjung = v.findViewById<TextView>(R.id.txTPengunjung)
            val txPendapatan = v.findViewById<TextView>(R.id.txTPendapatan)
            val btUpdate = v.findViewById<CardView>(R.id.cvST)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTour {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_super_tour, parent, false)
            return HolderTour(v)
        }

        override fun getItemCount(): Int {
            return dataTour.size
        }

        override fun onBindViewHolder(holder: HolderTour, position: Int) {
            val data = dataTour.get(position)
            Picasso.get().load(data.get("picture").toString()).into(holder.image)
            when(data.get("kode")) {
                0 -> {
                    val nn = SpannableString(data.get("nama").toString())
                    nn.setSpan(ForegroundColorSpan(Color.GREEN), nn.indexOf(" active"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txName.text = nn
                }
                1 -> {
                    val nn = SpannableString(data.get("nama").toString())
                    nn.setSpan(ForegroundColorSpan(Color.RED), nn.indexOf(" inactive"), nn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    holder.txName.text = nn
                }
                else -> {
                    holder.txName.text = data.get("nama").toString()
                }
            }
            holder.txPengunjung.text = ribuan(data.get("pengunjung").toString().toInt())
            holder.txPendapatan.text = rupiah(data.get("pendapatan"))
            holder.btUpdate.setOnClickListener {
                val fragment = SuperDetailTourFragment()
                val bundle = Bundle()
                bundle.putString("coba", (position + 1).toString())
                bundle.putString("id", data.get("id").toString())
                bundle.putString("coba2", data.get("name").toString())
                bundle.putString("coba3", data.get("picture").toString())
                bundle.putString("coba4", data.get("picture_name").toString())
                bundle.putString("coba5", data.get("ev").toString())
                bundle.putString("coba6", data.get("tgl1").toString())
                bundle.putString("coba7", data.get("tgl2").toString())
                fragment.arguments = bundle
                addFragment(fragment)
            }
        }

    }
}
