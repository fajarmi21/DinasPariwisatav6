package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegodobelo.expandingview.ExpandingList
import com.google.firebase.firestore.FirebaseFirestore
import com.polinema.android.kotlin.dinaspariwisatav6.R

/**
 * A simple [Fragment] subclass.
 */
class SuperExcelFragment : Fragment() {

    lateinit var mExpandingList : ExpandingList
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_super_excel, container, false)
        db = FirebaseFirestore.getInstance()
        mExpandingList = v.findViewById(R.id.expanding_excell_log)
        return v
    }

    override fun onStart() {
        super.onStart()
        createItems()
    }

    private fun createItems() {
        db.collection("kegiatan").get()
            .addOnCompleteListener {
                var get = arrayOf<Array<String>>()
                var dc = ""
                for (doc in it.result!!.documents) {
                    var x = doc["file_pendapatan"] as ArrayList<*>
                    var y = doc["file_pengunjung"]
                    Log.e("pn", y.toString())
                    Log.e("pn", x[0].toString())
                    Log.e("pn", x[1].toString())
                    Log.e("pn", x[2].toString())
                    dc = doc["file_nama"].toString()
                    get += arrayOf(y.toString(), x[0].toString(), x[1].toString(), x[2].toString())
//                    var get = arrayOf(y.toString(), x[0].toString(), x[1].toString(), x[2].toString())
//                    for (i in 0 until y[0].size) {
//                        var x = arrayOf(y[0][(i + 1).toString()]!![0], y[0][(i + 1).toString()]!![1])
//                        get += x
//                    }
                }
                addItem(dc, get, R.color.orange, R.drawable.ic_user)
            }
    }

    private fun addItem(title : String, subItems : Array<Array<String>>, colorRes : Int, iconRes : Int) {
        val item = mExpandingList.createNewItem(R.layout.super_excell_layout)
        if (item != null) {
            item.setIndicatorColorRes(colorRes)
            item.setIndicatorIconRes(iconRes)
            item.createSubItems(subItems.size)
            (item.findViewById<View>(R.id.userLog) as TextView).text = title
            Log.e("sb", subItems[0][1])
            for (i in subItems.indices) {
                var view = item.getSubItemView(i)
                configureSubItem(view, subItems[i][0], subItems[i][1], subItems[i][1], subItems[i][2])
            }
        }
    }

    private fun configureSubItem(view: View, Pn : String, Pg1 : String, Pg2 : String, Pg3 : String) {
        (view.findViewById<View>(R.id.txEPengunjung) as TextView).text = Pn
        (view.findViewById<View>(R.id.txEDPendapatan) as TextView).text = Pg1
        (view.findViewById<View>(R.id.txELPendapatan) as TextView).text = Pg2
        (view.findViewById<View>(R.id.txETPendapatan) as TextView).text = Pg3
    }
}
