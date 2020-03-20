package com.polinema.android.kotlin.dinaspariwisatav6.Admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_admin_log.*
import kotlinx.android.synthetic.main.fragment_user_log.*

/**
 * A simple [Fragment] subclass.
 */
class AdminLogFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var logAdapter: AdapterLog
    var daftarLog = mutableListOf<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_log, container, false)
        db = FirebaseFirestore.getInstance()
        logAdapter = AdapterLog(daftarLog)
        return view
    }

    override fun onStart() {
        super.onStart()
        spin()
    }

    private fun spin() {
        val hm = ArrayList<String>()
        hm.add("ASC")
        hm.add("DESC")
        val adapter = context?.let { it1 ->
            ArrayAdapter(
                it1,
                android.R.layout.simple_spinner_dropdown_item,
                hm
            )
        }
        spinnerAscA.adapter = adapter
        spinnerAscA.setSelection(1)
        spinnerAscA.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> showData2()
                    1 -> showData()
                }
            }

        }
    }

    private fun showData() {
        daftarLog.clear()
        val email = FirebaseAuth.getInstance().currentUser!!.email
        if (email != null) {
            db.collection("logs").whereEqualTo("email", email).get()
                .addOnSuccessListener {
                    if (it.count() != 0) {
                        for (d in it) {
                            var y = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                            Log.e("y", y[0]["1"]!![0])
                            Log.e("ar", y[0].size.toString())
                            for (i in y[0].size downTo 1) {
                                val hm = HashMap<String, String>()
                                hm.put("id", i.toString())
                                hm.put("log", y[0][i.toString()]!![0])
                                hm.put("date", y[0][i.toString()]!![1])
                                daftarLog.add(hm)
                            }
                            logAdapter.notifyDataSetChanged()
                        }
                    }
                }

            rvUserLogA.layoutManager = LinearLayoutManager(this.context)
            rvUserLogA.adapter = logAdapter
        }
    }

    private fun showData2() {
        daftarLog.clear()
        val email = FirebaseAuth.getInstance().currentUser!!.email
        if (email != null) {
            db.collection("logs").whereEqualTo("email", email).get()
                .addOnSuccessListener {
                    if (it.count() != 0) {
                        for (d in it) {
                            var y = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                            Log.e("y", y[0]["1"]!![0])
                            Log.e("ar", y[0].size.toString())
                            for (i in 0 until y[0].size) {
                                val hm = HashMap<String, String>()
                                hm.put("id", (i + 1).toString())
                                hm.put("log", y[0][(i + 1).toString()]!![0])
                                hm.put("date", y[0][(i + 1).toString()]!![1])
                                daftarLog.add(hm)
                            }
                            logAdapter.notifyDataSetChanged()
                        }
                    }
                }

            rvUserLogA.layoutManager = LinearLayoutManager(this.context)
            rvUserLogA.adapter = logAdapter
        }
    }

    inner class AdapterLog(val dataLog : List<HashMap<String, String>>) :
        RecyclerView.Adapter<AdapterLog.HolderLog>() {

        inner class HolderLog(v: View) : RecyclerView.ViewHolder(v) {
            val txNumber = v.findViewById<TextView>(R.id.txUserNumberA)
            val txLog = v.findViewById<TextView>(R.id.txUserLogA)
            val txDate = v.findViewById<TextView>(R.id.txUserDateA)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderLog {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_log, parent, false)
            return HolderLog(v)
        }

        override fun getItemCount(): Int {
            return dataLog.size
        }

        override fun onBindViewHolder(holder: HolderLog, position: Int) {
            val data = dataLog.get(position)
            holder.txNumber.text = data.get("id")
            holder.txLog.text = data.get("log")
            holder.txDate.text = data.get("date")
        }

    }
}