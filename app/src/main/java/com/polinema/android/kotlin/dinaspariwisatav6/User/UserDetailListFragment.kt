package com.polinema.android.kotlin.dinaspariwisatav6.User


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_detail_list.*
import kotlinx.android.synthetic.main.fragment_user_detail_list.view.*
import kotlinx.android.synthetic.main.fragment_user_upload.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class UserDetailListFragment : Fragment() {

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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_detail_list, container, false)
        mediaController = MediaController(this.context)
        mediaController.setAnchorView(view.vivDetailDU)
        db = FirebaseFirestore.getInstance()

        val pos = arguments?.get("pos")
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

        var txvisitor = arguments?.get("visitor").toString()
        var txincome1 = arguments?.get("income1").toString()
        var txincome2 = arguments?.get("income2").toString()
        var txincome3 = arguments?.get("income3").toString()

        view.idDetailDU.text = "(${id.toString()})"
        view.txuserDetailDU.setText("$user")
        view.txdateDetailDU.setText("$date")
        view.txdestineDetailDU.setText("$destine")
        view.txvisitorDetailDU.setText(visitor)
        view.txincomeDetailDU.setText(txincome3)

        Picasso.get().load(im1).into(view.imvDet1DU)
        Picasso.get().load(im2).into(view.imvDet2DU)
        Picasso.get().load(im3).into(view.imvDet3DU)
        Picasso.get().load(im4).into(view.imvDet4DU)

        view.vivDetailDU.setMediaController(mediaController)
        view.vivDetailDU.setVideoURI(viv.toUri())

        view.imv1DU.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        view.imv2DU.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        view.imv3DU.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 2)
        }

        view.imv4DU.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 3)
        }

        view.PilihVidDU.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, 4)
        }

        view.btTUpdateADU.setOnClickListener {
            view.txDetailDU.text = "Update"

            view.txvisitorDetailDU.setText(txvisitor)
            view.txincomeDDetailDU.setText(txincome1)
            view.txincomeLDetailDU.setText(txincome2)

            view.txvisitorDetailDU.isEnabled = true
            view.llsecondDU.visibility = View.VISIBLE
            view.spinner1DU.visibility = View.VISIBLE
            view.btTSubmitADU.visibility = View.VISIBLE
            view.cancel_button_detailADU.visibility = View.VISIBLE

            view.imv1DU.visibility = View.VISIBLE
            view.imv2DU.visibility = View.VISIBLE
            view.imv3DU.visibility = View.VISIBLE
            view.imv4DU.visibility = View.VISIBLE
            view.PilihVidDU.visibility = View.VISIBLE

            view.txincomeDetailDU.visibility = View.INVISIBLE
            view.destineDU.visibility = View.INVISIBLE
            view.btTUpdateADU.visibility = View.GONE
            view.btTDeleteADU.visibility = View.GONE
            spinner(destine.toString())
        }

        view.btTDeleteADU.setOnClickListener {
            db.collection("kegiatan").document(doc.toString()).get()
                .addOnSuccessListener {
                    val ar = it.get("file_storage") as ArrayList<String>
                    activity?.let { it1 ->
                        context?.let { it2 ->
                            ADF(
                                it1,
                                it2,
                                user.toString(),
                                pos.toString(),
                                it.get("file_tanggal") as ArrayList<String>,
                                it.id,
                                it.get("file_id").toString(),
                                ar
                            ).show(requireFragmentManager(), "TAG")
                        }
                    }
                }
        }

        view.btTSubmitADU.setOnClickListener {
            txvisitor = txvisitorDetailDU.value.toInt().toString()
            txincome1 = txincomeDDetailDU.value.toInt().toString()
            txincome2 = txincomeLDetailDU.value.toInt().toString()
            txincome3 = (txincome1.toInt() + txincome2.toInt()).toString()
            val txspin = spinner1DU.selectedItem.toString().replace(" (event)", "")

            val progressDialog = ProgressDialog(this.context)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("Updating....")
            progressDialog.show()
            db.collection("kegiatan").document(doc.toString()).update(
                destin, txspin,
                pengunjung, txvisitor,
                pendapatan, arrayListOf(txincome1, txincome2, txincome3)
            )
                .addOnSuccessListener {
                    db.collection("kegiatan").document(doc.toString()).get()
                        .addOnSuccessListener {
                            val users = it.get("file_nama").toString()
                            val tgl = it.get("file_tanggal") as ArrayList<*>
                            val ar = it.get("file_storage") as ArrayList<*>
                            val ae = it.get("file_url") as ArrayList<String>
                            Log.e("ae1", ae[0])
                            var pack = ""
                            when(tgl[0]) {
                                "Today" -> {
                                    val day = SimpleDateFormat("dd").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1].toString()))
                                    val month = SimpleDateFormat("MM").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1].toString()))
                                    val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1].toString()))
                                    pack = "$year/$month/$day/$users"
                                    Log.e("ddtttt", pack)
                                }
                                "Month" -> {
                                    val month = SimpleDateFormat("MM").format(SimpleDateFormat("MM - yyyy").parse(tgl[1].toString()))
                                    val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("MM - yyyy").parse(tgl[1].toString()))
                                    pack = "$year/$month/$users"
                                    Log.e("ddtttt", pack)
                                }
                            }
                            if (uri0 != Uri.EMPTY) {
                                val fx = storage.child("upload/${pack}/${ar[0]}")
                                fx.putFile(uri0)
                                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        return@Continuation fx.downloadUrl
                                    })
                                    .addOnCompleteListener {
                                        ae[0] = it.result.toString()

                                        Picasso.get().load(it.result.toString()).into(view.imvDet1DU)
                                    }
                                Log.e("ae3", ae[0])
                            }

                            if (uri1 != Uri.EMPTY) {
                                val fx = storage.child("upload/${pack}/${ar[1]}")
                                fx.putFile(uri1)
                                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        return@Continuation fx.downloadUrl
                                    })
                                    .addOnCompleteListener {
                                        ae[1] = it.result.toString()

                                        Picasso.get().load(it.result.toString()).into(view.imvDet2DU)
                                    }
                                Log.e("ae1", ae[1])
                            }

                            if (uri2 != Uri.EMPTY) {
                                val fx = storage.child("upload/${pack}/${ar[2]}")
                                fx.putFile(uri2)
                                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        return@Continuation fx.downloadUrl
                                    })
                                    .addOnCompleteListener {
                                        ae[2] = it.result.toString()

                                        Picasso.get().load(it.result.toString()).into(view.imvDet3DU)
                                    }
                                Log.e("ae2", ae[2])
                            }

                            if (uri3 != Uri.EMPTY) {
                                val fx = storage.child("upload/${pack}/${ar[3]}")
                                fx.putFile(uri3)
                                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        return@Continuation fx.downloadUrl
                                    })
                                    .addOnCompleteListener {
                                        ae[3] = it.result.toString()

                                        Picasso.get().load(it.result.toString()).into(view.imvDet4DU)
                                    }
                                Log.e("ae3", ae[3])
                            }

                            if (uri4 != Uri.EMPTY) {
                                val fx = storage.child("upload/${pack}/${ar[4]}")
                                fx.putFile(uri4)
                                    .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                        return@Continuation fx.downloadUrl
                                    })
                                    .addOnCompleteListener {
                                        ae[4] = it.result.toString()

                                        view.vivDetailDU.setVideoURI(it.result)
                                    }
                                Log.e("ae4", ae[4])
                            }

                            db.collection("kegiatan").document(doc.toString()).update(
                                "file_url", ae
                            )
                                .addOnSuccessListener {
                                    Log.e("sukses1", "update")
                                }
                                .addOnFailureListener {
                                    Log.e("sukses1", it.message)
                                }

                            val e = FirebaseAuth.getInstance().currentUser!!.email
                            val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))
                            db.collection("logs").whereEqualTo("email", e).get()
                                .addOnSuccessListener { it5 ->
                                    var id = ""
                                    var x = ArrayList<HashMap<String, ArrayList<String>>>()
                                    var y = 0
                                    for (d in it5) {
                                        id = d.id
                                        x = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                                        y = x[0].size
                                        Log.e("id", y.toString())
                                    }
                                    if (it5.count() != 0) {
                                        x[0].put((y + 1).toString(), arrayListOf("update document(${it.get("file_id").toString()})", date))
                                        db.collection("logs").document(id).update("log", x)
                                    } else {
                                        val hm = hashMapOf<String, Any>()
                                        val hm2 = hashMapOf<String, Any>()
                                        val email = FirebaseAuth.getInstance().currentUser!!.email
                                        hm2.put("1", arrayListOf("update document(${it.get("file_id").toString()})", date))
                                        hm.put("email", email.toString())
                                        hm.put("log", arrayListOf(hm2))
                                        db.collection("logs").document().set(hm)
                                    }
                                }
                        }
                        .addOnFailureListener {
                            Log.e("sukses2", it.message)
                        }

                    view.txDetailDU.text = "Detail"

                    view.txvisitorDetailDU.setText(txvisitor)
                    view.txincomeDetailDU.setText(txincome3)
                    view.txdestineDetailDU.setText(txspin)

                    view.txvisitorDetailDU.isEnabled = false
                    view.txincomeDetailDU.visibility = View.VISIBLE
                    view.btTUpdateADU.visibility = View.VISIBLE
                    view.btTDeleteADU.visibility = View.VISIBLE
                    view.destineDU.visibility = View.VISIBLE

                    view.llsecondDU.visibility = View.INVISIBLE
                    view.imv1DU.visibility = View.INVISIBLE
                    view.imv2DU.visibility = View.INVISIBLE
                    view.imv3DU.visibility = View.INVISIBLE
                    view.imv4DU.visibility = View.INVISIBLE
                    view.PilihVidDU.visibility = View.INVISIBLE

                    view.spinner1DU.visibility = View.INVISIBLE
                    view.btTSubmitADU.visibility = View.GONE
                    view.cancel_button_detailADU.visibility = View.GONE
                    progressDialog.hide()
                    Toast.makeText( context, "Update document(${id.toString()}) is successfully", Toast.LENGTH_SHORT).show()
                    val test = activity!!.supportFragmentManager.findFragmentByTag("UserListFragment")
                    test!!.onResume()
                }
                .addOnFailureListener {
                    progressDialog.hide()
                    Log.e("$tg error", it.message)
                    Toast.makeText( context, "Update document(${id.toString()}) is unsuccessfully", Toast.LENGTH_SHORT).show()
                }
        }

        view.cancel_button_detailADU.setOnClickListener {
            view.txDetailDU.text = "Detail"

            view.txvisitorDetailDU.setText(visitor)
            view.txincomeDetailDU.setText(txincome3)

            Picasso.get().load(im1).into(view.imvDet1DU)
            Picasso.get().load(im2).into(view.imvDet2DU)
            Picasso.get().load(im3).into(view.imvDet3DU)
            Picasso.get().load(im4).into(view.imvDet4DU)
            view.vivDetailDU.setVideoURI(viv.toUri())

            view.txvisitorDetailDU.isEnabled = false
            view.txincomeDetailDU.visibility = View.VISIBLE
            view.btTUpdateADU.visibility = View.VISIBLE
            view.btTDeleteADU.visibility = View.VISIBLE
            view.destineDU.visibility = View.VISIBLE

            view.llsecondDU.visibility = View.INVISIBLE
            view.imv1DU.visibility = View.INVISIBLE
            view.imv2DU.visibility = View.INVISIBLE
            view.imv3DU.visibility = View.INVISIBLE
            view.imv4DU.visibility = View.INVISIBLE
            view.PilihVidDU.visibility = View.INVISIBLE

            view.spinner1DU.visibility = View.INVISIBLE
            view.btTSubmitADU.visibility = View.GONE
            view.cancel_button_detailADU.visibility = View.GONE
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

    private fun spinner(sp: String) {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {it2 ->
                db.collection("tour").orderBy("nama", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener {
                        var spin = ""
                        val hm = ArrayList<String>()
                        for (doc in it) {
                            val ar = doc.get("event") as ArrayList<*>
                            when {
                                ar[0] == "false" && doc.get("users").toString() == it2.get("username").toString() -> {
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
                        spinner1DU.adapter = adapter
                        val pos = adapter!!.getPosition(spin)
                        spinner1DU.setSelection(pos)
                    }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(tg, "Photo1 was selected")
            uri0 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri0)
            imvDet1DU.setImageBitmap(bitmap)
            imv1DU.alpha = 0f
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(tg, "Photo2 was selected")
            uri1 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri1)
            imvDet2DU.setImageBitmap(bitmap)
            imv2DU.alpha = 0f
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(tg, "Photo3 was selected")
            uri2 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri2)
            imvDet3DU.setImageBitmap(bitmap)
            imv3DU.alpha = 0f
        }

        if (requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(tg, "Photo4 was selected")
            uri3 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri3)
            imvDet4DU.setImageBitmap(bitmap)
            imv4DU.alpha = 0f
        }

        if (requestCode == 4 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(tg, "Video was selected")

            uri4 = data.data
            vivDetailDU.setVideoURI(data.data)
        }
    }

    class ADF(
        activ: FragmentActivity,
        cont: Context,
        user: String,
        pos: String,
        tgl: ArrayList<String>,
        id: String,
        dc: String,
        al: ArrayList<String>
    ) :
        DialogFragment() {

        private val db = FirebaseFirestore.getInstance()
        private val storage = FirebaseStorage.getInstance().reference
        private var activ = activ
        private val contex = cont
        private val user = user
        private val pos = pos
        private val tgl = tgl
        private val id = id
        private val dc = dc
        private val a = al
        private fun tgl(code: Int, tg1: String? = null, tg2: String? = null) : Int {
            var date = 1L
            when(code) {
                1 -> date = ChronoUnit.DAYS.between(LocalDate.parse(tg1, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now())
                2 -> date = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(tg2, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }
            return date.toInt()
        }
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Theme_MaterialComponents_Light_Dialog_Alert
            )
                // Add customization options here
                // Title
                .setTitle("Delete (${dc})")
                // Supporting text
                .setMessage("are you sure you want to delete this document?")
                // Confirming action
                .setPositiveButton("Yes") { dialog, which ->
                    // Do something for button click
                    Log.e(tg + 1, a[0])
                    Log.e(tg + 2, a[1])
                    Log.e(tg + 3, a[2])
                    Log.e(tg + 4, a[3])
                    Log.e(tg + 5, a[4])
                    var pack = ""
                    when(tgl[0]) {
                        "Today" -> {
                            val day = SimpleDateFormat("dd").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1]))
                            val month = SimpleDateFormat("MM").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1]))
                            val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("dd - MM - yyyy").parse(tgl[1]))
                            pack = "$year/$month/$day/$user"
                            Log.e("ddtttt", pack)
                        }
                        "Month" -> {
                            val month = SimpleDateFormat("MM").format(SimpleDateFormat("MM - yyyy").parse(tgl[1]))
                            val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("MM - yyyy").parse(tgl[1]))
                            pack = "$year/$month/$user"
                            Log.e("ddtttt", pack)
                        }
                    }
                    storage.child("upload/${pack}/${a[0]}").delete()
                    storage.child("upload/${pack}/${a[1]}").delete()
                    storage.child("upload/${pack}/${a[2]}").delete()
                    storage.child("upload/${pack}/${a[3]}").delete()
                    storage.child("upload/${pack}/${a[4]}").delete()
                    db.collection("kegiatan").document(id).delete()

                    db.collection("kegiatan").whereEqualTo("file_nama", user).get()
                        .addOnSuccessListener {
                            var number = 1
                            for (doc in it.documents) {
                                val file = doc["file_id"].toString().replaceRange(7,doc["file_id"].toString().length,"").replaceRange(0,4,"").toInt()
                                val first = doc["file_id"].toString().replaceRange(5,doc["file_id"].toString().length,"")
                                val second = doc["file_id"].toString().replaceRange(0,6,"")
                                if (number != file) {
                                    var id = ""
                                    when {
                                        number < 10 -> id = "00$number"
                                        number < 100 -> id = "0$number"
                                        number > 100 -> id = number.toString()
                                    }
                                    db.collection("kegiatan").document()
                                        .update("file_id", first + id + second)
                                }
                                number++
                            }
                        }

                    val e = FirebaseAuth.getInstance().currentUser!!.email
                    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))
                    db.collection("logs").whereEqualTo("email", e).get()
                        .addOnSuccessListener { it5 ->
                            var id = ""
                            var x = ArrayList<HashMap<String, ArrayList<String>>>()
                            var y = 0
                            for (d in it5) {
                                id = d.id
                                x = d["log"] as ArrayList<HashMap<String, ArrayList<String>>>
                                y = x[0].size
                                Log.e("id", y.toString())
                            }
                            if (it5.count() != 0) {
                                x[0].put((y + 1).toString(), arrayListOf("delete document(${dc})", date))
                                db.collection("logs").document(id).update(
                                    "log", x
                                )
                            } else {
                                val hm = hashMapOf<String, Any>()
                                val hm2 = hashMapOf<String, Any>()
                                val email = FirebaseAuth.getInstance().currentUser!!.email
                                hm2.put("1", arrayListOf("delete document(${dc})", date))
                                hm.put("email", email.toString())
                                hm.put("log", arrayListOf(hm2))
                                db.collection("logs").document().set(hm)
                            }
                        }

                    val fragment = UserListFragment()
                    activ.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.contentU, fragment, fragment.javaClass.simpleName)
                        .commit()
                    Toast.makeText(contex, "Delete document(${dc}) is successfully", Toast.LENGTH_SHORT).show()
//                    val test = activity!!.supportFragmentManager.findFragmentByTag("UserListFragment")
//                    test!!.onResume()
                }
                // Dismissive action
                .setNegativeButton("No") { dialog, which ->
                    null
                }
                .create()
        }
    }
}
