package com.polinema.android.kotlin.dinaspariwisatav6.Admin


import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.User.UserDetailListFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_admin_detail_search.*
import kotlinx.android.synthetic.main.fragment_admin_detail_search.view.*
import kotlinx.android.synthetic.main.fragment_user_detail_list.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class AdminDetailSearchFragment : Fragment() {

    companion object {
        val tg = "TAG"
        val pendapatan = "file_pendapatan"
        val pengunjung = "file_pengunjung"
        val destin = "file_tempat"

        var arrD = ArrayList<String>()

        var uri0 = Uri.EMPTY
        var uri1 = Uri.EMPTY
        var uri2 = Uri.EMPTY
        var uri3 = Uri.EMPTY
        var uri4 = Uri.EMPTY
    }

    lateinit var mediaController: MediaController
    lateinit var db: FirebaseFirestore
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_detail_search, container, false)
        mediaController = MediaController(this.context)
        mediaController.setAnchorView(view.vivDetailDS)
        db = FirebaseFirestore.getInstance()

        val doc = arguments?.get("doc")
        val id = arguments?.get("id")
        val user = arguments?.get("user")
        val date = arguments?.get("date")
        val destine = arguments?.get("destine")
        val visitor = arguments?.get("visitor").toString()
        val im1 = arguments?.get("im1").toString()
        val im2 = arguments?.get("im2").toString()
        val im3 = arguments?.get("im3").toString()
        val im4 = arguments?.get("im4").toString()
        val viv = arguments?.get("viv").toString()

        Log.e(tg + 2, doc.toString())

        var txvisitor = arguments?.get("visitor").toString()
        var txincome1 = arguments?.get("income1").toString()
        var txincome2 = arguments?.get("income2").toString()
        var txincome3 = arguments?.get("income3").toString()

        view.idDetailDS.text = id.toString()
        view.txuserDetailDS.setText("$user")
        view.txdateDetailDS.setText("$date")
        view.txdestineDetailDS.setText("$destine")
        view.txvisitorDetailDS.setText(visitor)
        view.txincomeDetailDS.setText(txincome3)

        Picasso.get().load(im1).into(view.imvDet1DS)
        Picasso.get().load(im2).into(view.imvDet2DS)
        Picasso.get().load(im3).into(view.imvDet3DS)
        Picasso.get().load(im4).into(view.imvDet4DS)

        view.vivDetailDS.setMediaController(mediaController)
        view.vivDetailDS.setVideoURI(viv.toUri())
        view.vivDetailDS.start()

        view.btTUpdateADS.setOnClickListener {
            view.txDetailDS.text = "Update"

            view.txvisitorDetailDS.setText(txvisitor)
            view.txincomeDDetailDS.setText(txincome1)
            view.txincomeLDetailDS.setText(txincome2)

            view.txvisitorDetailDS.isEnabled = true
            view.llsecondDS.visibility = View.VISIBLE
            view.spinner1DS.visibility = View.VISIBLE
            view.btTSubmitADS.visibility = View.VISIBLE
            view.cancel_button_detailADS.visibility = View.VISIBLE

            view.txincomeDetailDS.visibility = View.INVISIBLE
            view.destineDS.visibility = View.INVISIBLE
            view.btTUpdateADS.visibility = View.GONE
            spinner(destine.toString())
        }

        view.cancel_button_detailADS.setOnClickListener {
            view.txDetailDS.text = "Detail"

            view.txvisitorDetailDS.setText(visitor)
            view.txincomeDetailDS.setText(txincome3)

            Picasso.get().load(im1).into(view.imvDet1DS)
            Picasso.get().load(im2).into(view.imvDet2DS)
            Picasso.get().load(im3).into(view.imvDet3DS)
            Picasso.get().load(im4).into(view.imvDet4DS)
            view.vivDetailDS.setVideoURI(viv.toUri())

            view.txvisitorDetailDS.isEnabled = false
            view.txincomeDetailDS.visibility = View.VISIBLE
            view.btTUpdateADS.visibility = View.VISIBLE
            view.destineDS.visibility = View.VISIBLE

            view.llsecondDS.visibility = View.INVISIBLE
            view.spinner1DS.visibility = View.INVISIBLE
            view.btTSubmitADS.visibility = View.GONE
            view.cancel_button_detailADS.visibility = View.GONE
        }

        view.btTSubmitADS.setOnClickListener {
            txvisitor = txvisitorDetailDS.value.toInt().toString()
            txincome1 = txincomeDDetailDS.value.toInt().toString()
            txincome2 = txincomeLDetailDS.value.toInt().toString()
            txincome3 = (txincome1.toInt() + txincome2.toInt()).toString()
            val txspin = spinner1DS.selectedItem.toString().replace(" (event)", "")

            val progressDialog = ProgressDialog(this.context)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("Updating....")
            progressDialog.show()
            val d = FirebaseAuth.getInstance().currentUser!!.email

            db.collection("kegiatan").document(doc.toString()).update(
                    UserDetailListFragment.destin, txspin,
                    UserDetailListFragment.pengunjung, txvisitor,
                    UserDetailListFragment.pendapatan, arrayListOf(txincome1, txincome2, txincome3),
                    "updated", d
                )
                .addOnSuccessListener {
                    db.collection("kegiatan").document(doc.toString()).get()
                        .addOnSuccessListener {
                            val e = FirebaseAuth.getInstance().currentUser!!.email
                            val date = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))
                            db.collection("logs").whereEqualTo("email", e).get()
                                .addOnSuccessListener { it5 ->
                                    var id = ""
                                    var x = ArrayList<HashMap<String, ArrayList<String>>>()
                                    var y = 0
                                    for (d in it5) {
                                        id = d.id
                                        x =
                                            d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                                        y = x[0].size
                                        Log.e("id", y.toString())
                                    }
                                    if (it5.count() != 0) {
                                        x[0].put(
                                            (y + 1).toString(),
                                            arrayListOf(
                                                "update document(${it.get("file_id").toString()})",
                                                date
                                            )
                                        )
                                        db.collection("logs").document(id).update("log", x)
                                    } else {
                                        val hm = hashMapOf<String, Any>()
                                        val hm2 = hashMapOf<String, Any>()
                                        val email = FirebaseAuth.getInstance().currentUser!!.email
                                        hm2.put(
                                            "1",
                                            arrayListOf(
                                                "update document(${it.get("file_id").toString()})",
                                                date
                                            )
                                        )
                                        hm.put("email", email.toString())
                                        hm.put("log", arrayListOf(hm2))
                                        db.collection("logs").document().set(hm)
                                    }
                                }
                        }
                        .addOnFailureListener {
                            Log.e("sukses2", it.message)
                        }

                    view.txDetailDS.text = "Detail"

                    view.txvisitorDetailDS.setText(txvisitor)
                    view.txincomeDetailDS.setText(txincome3)
                    view.txdestineDetailDS.setText(txspin)

                    view.txvisitorDetailDS.isEnabled = false
                    view.txincomeDetailDS.visibility = View.VISIBLE
                    view.btTUpdateADS.visibility = View.VISIBLE
                    view.destineDS.visibility = View.VISIBLE

                    view.llsecondDS.visibility = View.INVISIBLE
                    view.spinner1DS.visibility = View.INVISIBLE
                    view.btTSubmitADS.visibility = View.GONE
                    view.cancel_button_detailADS.visibility = View.GONE
                    progressDialog.hide()
                    Toast.makeText(
                        context,
                        "Update document(${id.toString()}) is successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val test =
                        activity!!.supportFragmentManager.findFragmentByTag("AdminSearchFragment")
                    test!!.onResume()
//            txvisitor = txvisitorDetailDS.text.toString()
//            txincome = txincomeDetailDS.text.toString()
//            var txspin = spinner1.selectedItem.toString()
//            db.collection("kegiatan").document(doc.toString()).update(
//                destin, spinner1.selectedItem.toString(),
//                pengunjung, txvisitor,
//                pendapatan, txincome
//            )
//                .addOnSuccessListener {
//                    view.txvisitorDetailDS.setText("${ribuan(txvisitor.toInt())}")
//                    view.txincomeDetailDS.setText("${rupiah(txincome.toInt())}")
//                    view.txdestineDetailDS.setText("$txspin")
//                    view.LLspin.visibility = View.INVISIBLE
//                    view.visitorDetailDS.isEnabled = false
//                    view.incomeDetailDS.isEnabled = false
//                    view.btTUpdateADS.visibility = View.VISIBLE
//                    view.destineDS.visibility = View.VISIBLE
//                    view.btTSubmitADS.visibility = View.INVISIBLE
//                    view.cancel_button_detailADS.visibility = View.INVISIBLE
//
//                    Toast.makeText( context, "Update with id (${id.toString()}) is successfully", Toast.LENGTH_SHORT).show()
//                    val test = activity!!.supportFragmentManager.findFragmentByTag("AdminSearchFragment")
//                    test!!.onResume()
//                }
//                .addOnFailureListener {
//                    Toast.makeText( context, "Update with id (${id.toString()}) is unsuccessfully. please check again", Toast.LENGTH_SHORT).show()
//                    Log.e(tg, it.message)
//                }
                }
        }
            return view
    }

    private fun tgl(code: Int, tg1: String? = null, tg2: String? = null) : Int {
        var date = 1L
        when(code) {
            1 -> date = ChronoUnit.DAYS.between(LocalDate.parse(tg1, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now())
            2 -> date = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(tg2, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        return date.toInt()
    }

    fun spinner(sp : String) {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener { it2 ->
                db.collection("tour").orderBy("nama", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener {
                        var spin = ""
                        val hm = ArrayList<String>()
                        for (doc in it) {
                            val ar = doc.get("event") as ArrayList<*>
                            when {
                                ar[0] == "false" && doc.get("users").toString() == txuserDetailDS.text.toString() -> {
                                    hm.add(doc.get("nama").toString())
                                    if(sp == doc.get("nama").toString() ) spin = doc.get("nama").toString()
                                }
                                ar[0] == "true" -> {
                                    when {
                                        tgl(1, ar[1].toString()) >= 0 && tgl(
                                            2,
                                            null,
                                            ar[2].toString()
                                        ) >= 0 -> {
                                            if(sp == doc.get("nama").toString() ) spin = doc.get("nama").toString() + " (event)"
                                            hm.add(doc.get("nama").toString() + " (event)")
                                        }
                                    }
                                }
                            }
                        }
                        val adapter = context?.let { it1 ->
                            ArrayAdapter(
                                it1,
                                android.R.layout.simple_spinner_dropdown_item,
                                hm
                            )
                        }
                        spinner1DS.adapter = adapter
                        val pos = adapter!!.getPosition(spin)
                        spinner1DS.setSelection(pos)
                    }
            }
    }
}
