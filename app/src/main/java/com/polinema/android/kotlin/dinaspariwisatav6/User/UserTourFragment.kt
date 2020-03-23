package com.polinema.android.kotlin.dinaspariwisatav6.User

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_tour.*
import kotlinx.android.synthetic.main.fragment_user_tour.view.*
import kotlinx.android.synthetic.main.fragment_user_upload.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class UserTourFragment : Fragment(){

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
        val view = inflater.inflate(R.layout.fragment_user_tour, container, false)
        db = FirebaseFirestore.getInstance()
        tourAdapter = TourAdapter(daftarTour)
        view.txNowU.paintFlags = view.txNowU.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        view.txNowU.text = now2()
        return view
    }

    override fun onResume() {
        super.onResume()
        showData()
    }

    private fun tgl(code: Int, tg1: String? = null, tg2: String? = null) : Int {
        var date = 1L
        when(code) {
            1 -> date = ChronoUnit.DAYS.between(LocalDate.parse(tg1, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now())
            2 -> date = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(tg2, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        return date.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    private fun showData() {
        daftarTour.clear()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnCompleteListener {
                val name = it.result?.get("username").toString()
                db.collection("tour").orderBy("nama", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener {
                        Log.e("dataeee", it.count().toString())
                        for (doc in it) {
                            db.collection("kegiatan").whereEqualTo("file_nama", name)
                                .whereEqualTo("file_tempat", doc.get("nama")).get()
                                .addOnSuccessListener { it2 ->
                                    val hm = HashMap<String, Any>()
                                    val ar = doc.get("event") as ArrayList<*>
                                    var count = 0
                                    var count2 = 0
                                    for (doc2 in it2) {
//                                        Log.e("date", tanggal(doc2.get("file_tanggal")))
//                                        Log.e("date", now())
                                        val ary = doc2.get("file_tanggal") as ArrayList<*>
                                        when(ary[0]) {
                                            "Today" -> if (SimpleDateFormat("MM").format(
                                                    SimpleDateFormat("dd - MM - yyyy").parse(ary[1].toString())) == LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))) {
                                                val arr = doc2.get("file_pendapatan") as ArrayList<*>
                                                count += doc2.get("file_pengunjung").toString().toInt()
                                                count2 += arr[2].toString().toInt()
                                                Log.e("incomeeee", count2.toString())
                                            }
                                            "Month" -> if (SimpleDateFormat("MM").format(SimpleDateFormat("MM - yyyy").parse(ary[1].toString())) == LocalDate.now().format(DateTimeFormatter.ofPattern("MM"))) {
                                                val arr = doc2.get("file_pendapatan") as ArrayList<*>
                                                count += doc2.get("file_pengunjung").toString().toInt()
                                                count2 += arr[2].toString().toInt()
                                                Log.e("incomeeee", count2.toString())
                                            }
                                        }
//                                        if (tanggal(doc2.get("file_tanggal")) == now()) {
//                                            val arr = doc2.get("file_pendapatan") as ArrayList<*>
//                                            count += doc2.get("file_pengunjung").toString().toInt()
//                                            count2 += arr[2].toString().toInt()
//                                            Log.e("incomeeee", count2.toString())
//                                        }
                                    }
                                    when {
                                        doc.get("users") == name && ar[0] ==  "false" -> {
                                            hm.put("id", doc.id)
                                            hm.put("picture", doc.get("picture").toString())
                                            hm.put("picture_name", doc.get("picture_name").toString())
                                            hm.put("nama", doc.get("nama").toString())
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
                                            hm.put("pengunjung", count.toString())
                                            hm.put("pendapatan", count2.toString().toDouble())
                                            daftarTour.add(hm)
                                            tourAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                        }
                    }
            }
        rvUserTour.layoutManager = LinearLayoutManager(this.context)
        rvUserTour.adapter = tourAdapter
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
//        Log.e(tag, date.toString())
        return date.toString()
    }

    private fun now() : String {
        val formatter = DateTimeFormatter.ofPattern("M")
        return LocalDate.now().format(formatter)
    }

    private fun now2() : String {
        val formatter = DateTimeFormatter.ofPattern("MMMM")
        return LocalDate.now().format(formatter)
    }

    inner class TourAdapter(val dataTour: List<HashMap<String, Any>>) :
        RecyclerView.Adapter<TourAdapter.HolderTour>() {
        inner class HolderTour(v: View) : RecyclerView.ViewHolder(v) {
            val image = v.findViewById<ImageView>(R.id.imageViewUT)
            val txName = v.findViewById<TextView>(R.id.txUTour)
            val txPengunjung = v.findViewById<TextView>(R.id.txUPengunjung)
            val txPendapatan = v.findViewById<TextView>(R.id.txUPendapatan)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTour {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user_tour, parent, false)
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
        }

    }
}
