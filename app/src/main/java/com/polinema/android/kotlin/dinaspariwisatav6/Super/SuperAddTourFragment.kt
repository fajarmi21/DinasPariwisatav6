package com.polinema.android.kotlin.dinaspariwisatav6.Super


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
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.github.okdroid.checkablechipview.CheckableChipView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_super_add_tour.*
import kotlinx.android.synthetic.main.fragment_super_add_tour.view.*
import java.text.SimpleDateFormat
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class SuperAddTourFragment : Fragment(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    lateinit var db: FirebaseFirestore
    lateinit var storage: StorageReference
    lateinit var uri: Uri
    private var datePicker: PrimeDatePickerBottomSheet? = null
    private var startDate = ""
    private var endDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_add_tour, container, false)
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
        uri = Uri.EMPTY

        view.chip.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            Toast
                .makeText(context, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT)
                .show()

            if (isChecked) {
                txEvent.visibility = View.VISIBLE
                val today = CalendarFactory.newInstance(CalendarType.CIVIL)
                val pickType = PickType.RANGE_START
                datePicker = PrimeDatePickerBottomSheet.newInstance(
                    currentDateCalendar = today,
                    pickType = pickType
                )
                datePicker?.setOnDateSetListener(this)
                datePicker?.show(activity!!.supportFragmentManager, PICKER_TAG)
            } else {
                txEvent.visibility = View.INVISIBLE
            }
        }
        view.imbAddTour.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        view.btTCancel.setOnClickListener {
            val fragment = this
            addFragment(fragment)
        }
        view.btTAdd.setOnClickListener {
            val pictureName = "${view.txTourAddName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"
            var fx =
                storage.child(
                    """tour/${view.txTourAddName.text.toString()}.${MimeTypeMap.getFileExtensionFromUrl(uri.toString())}"""
                )
//            Log.e("add", picture_name)
            fx.putFile(uri)
                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    return@Continuation fx.downloadUrl
                })
                .addOnCompleteListener {it0 ->
                    val hm = HashMap<String,Any>()
                    hm["nama"] = txTourAddName.text.toString()
                    hm["picture"] = it0.result.toString()
                    hm["picture_name"] = pictureName
                    if (chip.isChecked) {
                        hm["event"] = arrayListOf(chip.isChecked.toString(), startDate, endDate)
                    } else {
                        hm["event"] = arrayListOf(chip.isChecked.toString())
                    }
                    db.collection("tour").document().set(hm)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Add tour successfully", Toast.LENGTH_SHORT).show()
                            view.imvAddTour.setImageResource(R.drawable.ic_user)
                            view.txTourAddName.text!!.clear()
                            view.txTourAddName.clearFocus()
                            view.chip.isChecked = false
                            view.txEvent.text = " "
                            val test = activity!!.supportFragmentManager.findFragmentByTag("SuperTourFragment")
                            test!!.onResume()
                        }
                        .addOnFailureListener {
                            Log.e("gagal", it.message)
                            Toast.makeText(context, "Add tour unsuccessfully, please check again", Toast.LENGTH_SHORT).show()
                        }
                }
        }
        return view
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .remove(fragment)
            .commit()
        Log.e("TAG", fragment.javaClass.simpleName)
    }

    override fun onMultipleDaysPicked(multipleDays: List<PrimeCalendar>) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onRangeDaysPicked(startDay: PrimeCalendar, endDay: PrimeCalendar) {
        longToast("From: ${startDay.longDateString}\nTo: ${endDay.longDateString}")
        startDate = SimpleDateFormat("dd/MM/yyyy").format(SimpleDateFormat("yyyy/MM/dd").parse(startDay.shortDateString))
        endDate = SimpleDateFormat("dd/MM/yyyy").format(SimpleDateFormat("yyyy/MM/dd").parse(endDay.shortDateString))
        txEvent.text = "$startDate - $endDate"

    }

    override fun onSingleDayPicked(singleDay: PrimeCalendar) {
        TODO("Not yet implemented")
    }

    private fun longToast(text: String) =
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()

    companion object {
        const val PICKER_TAG = "PrimeDatePickerBottomSheet"
    }

    override fun onStart() {
        super.onStart()
        txTourAddName.requestFocus()
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(txTourAddName, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        datePicker = activity!!.supportFragmentManager.findFragmentByTag(PICKER_TAG) as? PrimeDatePickerBottomSheet
        datePicker?.setOnDateSetListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.e("uri", data.data.toString())
            uri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
            imvAddTour.setImageBitmap(bitmap)
            imbAddTour.alpha = 0f
        }
    }

}
