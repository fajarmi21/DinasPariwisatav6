package com.polinema.android.kotlin.dinaspariwisatav6.Admin


import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_admin_detail_tour.*
import kotlinx.android.synthetic.main.fragment_admin_detail_tour.view.*
import kotlinx.android.synthetic.main.fragment_super_detail_tour.view.*

/**
 * A simple [Fragment] subclass.
 */
class AdminDetailTourFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var storage: StorageReference
    lateinit var uri: Uri

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_detail_tour, container, false)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
        uri = Uri.EMPTY

        val id = arguments?.get("id")
        val tx = arguments?.get("coba")
        val txa = arguments?.get("coba2")
        val imv = arguments?.get("coba3")
        val ev = arguments?.get("coba5")
        val tgl1 = arguments?.get("coba6")
        val tgl2 = arguments?.get("coba7")
        var name = ""
        if (ev.toString() == "true") {
            view.txEventAD.text = "${tgl1.toString()} - ${tgl2.toString()}"
        }
        view.idDetailA.text = tx.toString()
        view.txTourNameA.setText(txa.toString())
        Picasso.get().load(imv.toString()).into(view.imvDetailTourA)

        view.btTUpdateA.setOnClickListener {
            name = view.txTourNameA.text.toString()
            view.txDetailA.text = "Update"
            view.TourNameA.isEnabled = true
            view.txTourNameA.requestFocus()
            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view.txTourNameA, InputMethodManager.SHOW_IMPLICIT)
            view.btTUpdateA.visibility = View.GONE
            view.cancel_button_detailA.visibility = View.VISIBLE
            view.btTSubmitA.visibility = View.VISIBLE
            view.imv_tourA.visibility = View.VISIBLE
        }

        view.btTSubmitA.setOnClickListener {
            Log.e("TAG", txTourNameA.text.toString())
            Log.e("TAG2", name)
            Log.e("urriiii", uri.toString())
            if (uri != Uri.EMPTY) {
                Log.e("TAG3", "isi")
                Log.e("urriiii", uri.toString())
//                Log.e(
//                    "TAG2",
//                    "${txTourName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"
//                )
                val picture_name = "${txTourNameA.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"
                var fx =
                    storage.child(
                        """tour/${txTourNameA.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"""
                    )
//                Log.e(
//                    "TAG3",
//                    "${txTourName.text.toString()}"
//                )
//                Log.e(
//                    "TAG4",
//                    "${txa.toString()}"
//                )
                if (txTourNameA.text.toString() == name) {
                    Log.e("TAG2", "sukses")
                    Log.e("TAG3", picture_name)
                    db.collection("tour").document(id.toString()).get()
                        .addOnSuccessListener {
                            storage.child("""tour/${it.get("picture_name").toString()}""").delete()
                        }
                    fx.putFile(uri)
                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            return@Continuation fx.downloadUrl
                        })
                        .addOnCompleteListener {it0 ->
                            val ii = it0.result.toString()
                            db.collection("tour").document(id.toString()).update(
                                "picture", it0.result.toString(),
                                "picture_name", picture_name
                            )
                                .addOnSuccessListener {at ->
                                    Picasso.get().load(ii).into(view.imvDetailTourA)
                                    Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()


                                    view.imv_tourA.visibility = View.INVISIBLE
                                    view.txDetailA.text = "Detail"
                                    view.TourNameA.isEnabled = false
                                    view.btTUpdateA.visibility = View.VISIBLE
                                    view.cancel_button_detailA.visibility = View.GONE
                                    view.btTSubmitA.visibility = View.GONE
                                    val test = activity!!.supportFragmentManager.findFragmentByTag("AdminUploadFragment")
                                    test!!.onResume()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Update record failed. please check again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener {it1 ->
                            Toast.makeText(
                                context,
                                "Update record failed. please check again",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("TAG3", it1.message)
                        }
                } else {
                    Log.e("urriiii", uri.toString())
                    Log.e("TAG2", "gagal")
                    val i = uri
                    db.collection("tour").document(id.toString()).get()
                        .addOnSuccessListener {
                            Log.e("TAG2", it.get("picture_name").toString())
                            storage.child("""tour/${it.get("picture_name").toString()}""").delete()
                                .addOnSuccessListener {
                                    Log.e("log", "sukses")
                                    Log.e("uri gagal", i.toString())
                                    Log.e("uri gagal", "uri")
                                    fx.putFile(i)
                                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                            return@Continuation fx.downloadUrl
                                        })
                                        .addOnCompleteListener {it0 ->
                                            val ii = it0.result.toString()
                                            db.collection("tour").document(id.toString()).update(
                                                "nama", view.txTourNameA.text.toString(),
                                                "picture", it0.result.toString(),
                                                "picture_name", picture_name
                                            )
                                                .addOnSuccessListener {at ->
                                                    Picasso.get().load(ii).into(view.imvDetailTourA)
                                                    Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()


                                                    view.imv_tourA.visibility = View.INVISIBLE
                                                    view.txDetailA.text = "Detail"
                                                    view.TourNameA.isEnabled = false
                                                    view.btTUpdateA.visibility = View.VISIBLE
                                                    view.cancel_button_detailA.visibility = View.GONE
                                                    view.btTSubmitA.visibility = View.GONE
                                                    val test = activity!!.supportFragmentManager.findFragmentByTag("AdminUploadFragment")
                                                    test!!.onResume()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Update record failed. please check again",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                        .addOnFailureListener {it1 ->
                                            Toast.makeText(
                                                context,
                                                "Update record failed. please check again",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.e("TAG3", it1.message)
                                        }
                                }
                                .addOnFailureListener {
                                    Log.e("log", "gagal")
                                }
                        }
                        .addOnFailureListener {
                            Log.e("TAG2", it.message)
                        }
                }
            } else {
                Log.e("TAG3", "kosong")
                name = view.txTourNameA.text.toString()
                Log.e("TAG3", name)
                db.collection("tour").document(id.toString()).update(
                    "nama", txTourNameA.text.toString()
                )
                    .addOnSuccessListener {
                        Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()
                        view.cancel_button_detailA.performClick()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Update record failed. please check again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            uri = Uri.EMPTY
        }

        view.cancel_button_detailA.setOnClickListener {
            view.imv_tourA.visibility = View.INVISIBLE
            view.txDetailA.text = "Detail"
            view.TourNameA.isEnabled = false
            view.btTUpdateA.visibility = View.VISIBLE
            view.cancel_button_detailA.visibility = View.GONE
            view.btTSubmitA.visibility = View.GONE
            Picasso.get().load(imv.toString()).into(view.imvDetailTourA)
            val test = activity!!.supportFragmentManager.findFragmentByTag("AdminUploadFragment")
            test!!.onResume()
        }

        view.imv_tourA.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.e("uri", data.data.toString())
            uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
            imvDetailTourA.setImageBitmap(bitmap)
            imv_tourA.alpha = 0f
        }
    }

}
