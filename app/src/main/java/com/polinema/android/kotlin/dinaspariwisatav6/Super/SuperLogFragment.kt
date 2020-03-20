package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.os.Bundle
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
class SuperLogFragment : Fragment() {

    lateinit var mExpandingList : ExpandingList
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_super_log, container, false)
        db = FirebaseFirestore.getInstance()
        mExpandingList = view.findViewById(R.id.expanding_list_log)
        return view
    }

    override fun onStart() {
        super.onStart()
        createItems()
    }

    private fun createItems() {
        db.collection("logs").get()
            .addOnCompleteListener {
                for (doc in it.result!!.documents) {
                    var y = doc["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                    var get = arrayOf<Array<String>>()
                    for (i in 0 until y[0].size) {
                        var x = arrayOf(y[0][(i + 1).toString()]!![0], y[0][(i + 1).toString()]!![1])
                        get += x
                    }
                    addItem(doc["email"].toString(), get, R.color.orange, R.drawable.ic_user)
                }
            }
    }

    private fun addItem(title : String, subItems : Array<Array<String>>, colorRes : Int, iconRes : Int) {
        val item = mExpandingList.createNewItem(R.layout.super_upload_layout)
        if (item != null) {
            item.setIndicatorColorRes(colorRes)
            item.setIndicatorIconRes(iconRes)
            item.createSubItems(subItems.size)
            (item.findViewById<View>(R.id.userLog) as TextView).text = title
            for (i in 0..subItems.size - 1) {
                var view = item.getSubItemView(i)
                configureSubItem(view, subItems[i][0], subItems[i][1])
            }
        }
    }

    private fun configureSubItem(view: View, ket : String, tgl : String) {
        (view.findViewById<View>(R.id.txKet) as TextView).text = ket
        (view.findViewById<View>(R.id.txTgl) as TextView).text = tgl
    }

}
