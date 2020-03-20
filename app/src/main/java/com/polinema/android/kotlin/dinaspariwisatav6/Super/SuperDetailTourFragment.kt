package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.github.okdroid.checkablechipview.CheckableChipView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_super_add_tour.*
import kotlinx.android.synthetic.main.fragment_super_add_tour.view.*
import kotlinx.android.synthetic.main.fragment_super_add_tour.view.txEvent
import kotlinx.android.synthetic.main.fragment_super_detail_tour.*
import kotlinx.android.synthetic.main.fragment_super_detail_tour.view.*

/**
 * A simple [Fragment] subclass.
 */
class SuperDetailTourFragment : Fragment() {

    lateinit var db: FirebaseFirestore
    lateinit var storage: StorageReference
    lateinit var uri: Uri

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_detail_tour, container, false)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
        uri = Uri.EMPTY

        val id = arguments?.get("id")
        val tx = arguments?.get("coba")
        val txa = arguments?.get("coba2")
        val imv = arguments?.get("coba3")
        val imvName = arguments?.get("coba4")
        val ev = arguments?.get("coba5")
        val tgl1 = arguments?.get("coba6")
        val tgl2 = arguments?.get("coba7")
        var name = ""
        if (ev.toString() == "true") {
            view.txEventSD.text = "${tgl1.toString()} - ${tgl2.toString()}"
        }
        view.idDetail.text = tx.toString()
        view.txTourName.setText(txa.toString())
        Picasso.get().load(imv.toString()).into(view.imvDetailTour)

        view.btTUpdate.setOnClickListener {
            name = view.txTourName.text.toString()
            view.txDetailT.text = "Update"
            view.TourName.isEnabled = true
            view.txTourName.requestFocus()
            val inputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view.txTourName, InputMethodManager.SHOW_IMPLICIT)
            view.btTUpdate.visibility = View.GONE
            view.btTDelete.visibility = View.GONE
            view.cancel_button_detail.visibility = View.VISIBLE
            view.btTSubmit.visibility = View.VISIBLE
            view.imv_tour.visibility = View.VISIBLE
        }
        view.cancel_button_detail.setOnClickListener {
            view.imv_tour.visibility = View.INVISIBLE
            view.txDetailT.text = "Detail"
            view.TourName.isEnabled = false
            view.btTUpdate.visibility = View.VISIBLE
            view.btTDelete.visibility = View.VISIBLE
            view.cancel_button_detail.visibility = View.GONE
            view.btTSubmit.visibility = View.GONE
            Picasso.get().load(imv.toString()).into(view.imvDetailTour)
            val test = activity!!.supportFragmentManager.findFragmentByTag("SuperTourFragment")
            test!!.onResume()
        }
        view.btTDelete.setOnClickListener {
            context?.let { it1 ->
                activity?.let { it2 ->
                    AppDialogFragment(
                        it1,
                        it2, tx.toString(), id.toString(), imvName.toString(), txa.toString()
                    ).show(requireFragmentManager(), "TAG")
                }
            }
        }
        view.btTSubmit.setOnClickListener {
            Log.e("TAG", txTourName.text.toString())
            Log.e("TAG2", name)
            Log.e("urriiii", uri.toString())
            if (uri != Uri.EMPTY) {
                Log.e("TAG3", "isi")
                Log.e("urriiii", uri.toString())
//                Log.e(
//                    "TAG2",
//                    "${txTourName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"
//                )
                val picture_name = "${txTourName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"
                var fx =
                    storage.child(
                        """tour/${txTourName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"""
                    )
//                Log.e(
//                    "TAG3",
//                    "${txTourName.text.toString()}"
//                )
//                Log.e(
//                    "TAG4",
//                    "${txa.toString()}"
//                )
                if (txTourName.text.toString() == name) {
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
                                    Picasso.get().load(ii).into(view.imvDetailTour)
                                    Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()


                                    view.imv_tour.visibility = View.INVISIBLE
                                    view.txDetailT.text = "Detail"
                                    view.TourName.isEnabled = false
                                    view.btTUpdate.visibility = View.VISIBLE
                                    view.btTDelete.visibility = View.VISIBLE
                                    view.cancel_button_detail.visibility = View.GONE
                                    view.btTSubmit.visibility = View.GONE
                                    val test = activity!!.supportFragmentManager.findFragmentByTag("SuperTourFragment")
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
                                                "name", view.txTourName.text.toString(),
                                                "picture", it0.result.toString(),
                                                "picture_name", picture_name
                                            )
                                                .addOnSuccessListener {at ->
                                                    Picasso.get().load(ii).into(view.imvDetailTour)
                                                    Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()


                                                    view.imv_tour.visibility = View.INVISIBLE
                                                    view.txDetailT.text = "Detail"
                                                    view.TourName.isEnabled = false
                                                    view.btTUpdate.visibility = View.VISIBLE
                                                    view.btTDelete.visibility = View.VISIBLE
                                                    view.cancel_button_detail.visibility = View.GONE
                                                    view.btTSubmit.visibility = View.GONE
                                                    val test = activity!!.supportFragmentManager.findFragmentByTag("SuperTourFragment")
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
                name = view.txTourName.text.toString()
                Log.e("TAG3", name)
                db.collection("tour").document(id.toString()).update(
                    "nama", txTourName.text.toString()
                )
                    .addOnSuccessListener {
                        Toast.makeText(context, "Update record success", Toast.LENGTH_SHORT).show()
                        view.cancel_button_detail.performClick()
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

        view.imv_tour.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        val test = activity!!.supportFragmentManager.findFragmentByTag("SuperTourFragment")
        if (test!!.isVisible) {
            Log.e("TAG", "visible")
        } else if (test!!.isHidden) {
            Log.e("TAG", "hidden")
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.e("uri", data.data.toString())
            uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
            imvDetailTour.setImageBitmap(bitmap)
            imv_tour.alpha = 0f
        }
    }

    class AppDialogFragment(context: Context, activ: FragmentActivity, textID: String, id: String, name: String, txa: String) :
        DialogFragment() {
        private val db = FirebaseFirestore.getInstance()
        private val storage = FirebaseStorage.getInstance().reference
        private val contex = context
        private val a = activ
        private val txId = id
        private val tx = textID
        private val name = name
        private val txa = txa
        private fun addFragment(fragment: Fragment) {
            a.supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentSC, fragment, fragment.javaClass.simpleName)
                .commit()
            Log.e("s", fragment.javaClass.simpleName)
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(
                requireContext(),
                R.style.Theme_MaterialComponents_Light_Dialog_Alert
            )
                // Add customization options here
                // Title
                .setTitle("Delete (${tx})")
                // Supporting text
                .setMessage("are you sure you want to delete this record ($txa) ?")
                // Confirming action
                .setPositiveButton("Yes") { dialog, which ->
                    // Do something for button click
                    storage.child("tour/${name}").delete()
                        .addOnSuccessListener {
                            db.collection("tour").document(txId).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        contex,
                                        "Delete record success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val fragment = SuperTourFragment()
                                    addFragment(fragment)
                                }
                                .addOnFailureListener {
                                    Log.e("TAG", it.message)
                                    null
                                }
                        }
                        .addOnFailureListener {
                            Log.e("TAG", it.message)
                            null
                        }
                }
                // Dismissive action
                .setNegativeButton("No") { dialog, which ->
                    null
                }
                .create()
        }
    }
}
