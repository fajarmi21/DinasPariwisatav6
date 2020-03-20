package com.polinema.android.kotlin.dinaspariwisatav6.User


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.Super.SuperUser
import kotlinx.android.synthetic.main.fragment_user_upload.*
import kotlinx.android.synthetic.main.fragment_user_upload.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class UserUploadFragment : Fragment(), View.OnClickListener {

    lateinit var storage: StorageReference
    lateinit var mediaController: MediaController
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    companion object {
        val TAG = "MainActivity"
        val cal: Calendar = Calendar.getInstance()
        val F_ID = "file_id"
        val F_NAMA = "file_nama"
        val F_TANGGAL = "file_tanggal"
        val F_TEMPAT = "file_tempat"
        val F_PENGUNJUNG = "file_pengunjung"
        val F_PENDAPATAN = "file_pendapatan"
        val F_KET = "file_ket"
        val F_STORAGE = "file_storage"
        val F_URL = "file_url"
        val F_TYPE = "file_type"
        val F_VIDEO = "file_video"

        var spinDate = 0
        var spinPlace = 0

        var fileName = ""
        var fileID = ""
        var filetgl = ""
        var fileTempat = ""
        var filePengunjung = ""
        var fileKet = ""
        var fileType = ""
        var fileVideo = ""
        var arrIncome = ArrayList<String>()
        var arrDate = ArrayList<String>()
        var arrD = ArrayList<String>()

        var uri0 = Uri.EMPTY
        var uri1 = Uri.EMPTY
        var uri2 = Uri.EMPTY
        var uri3 = Uri.EMPTY
        var uri4 = Uri.EMPTY
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_upload, container, false)
        storage = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        mediaController = MediaController(this.context)
        mediaController.setAnchorView(vivew)

        view.imv1.setOnClickListener(this)
        view.imv2.setOnClickListener(this)
        view.imv3.setOnClickListener(this)
        view.imv4.setOnClickListener(this)
        view.PilihVid.setOnClickListener(this)
        view.btSubmit.setOnClickListener(this)
        view.vivew.setMediaController(mediaController)

        view.spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> txDate.text = now()
                    1 -> txDate.text = LocalDate.now().format(DateTimeFormatter.ofPattern("MM - yyyy"))
                }
            }

        }
        return view
    }

    override fun onStart() {
        super.onStart()
        getUser()
        spinner()
        spinner2()
        arrD.clear()
    }

    private fun now() : String {
        val formatter = DateTimeFormatter.ofPattern("dd - MM - yyyy")
        return LocalDate.now().format(formatter)
    }

    private fun tgl(code: Int, tg1: String? = null, tg2: String? = null) : Int {
        var date = 1L
        when(code) {
            1 -> date = ChronoUnit.DAYS.between(LocalDate.parse(tg1, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now())
            2 -> date = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(tg2, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        }
        return date.toInt()
    }

    private fun getUser() {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                val username = it.get("username").toString()
                txaUser_upload.setText(username)
                getCount(username)
//                Log.e("user", username)
            }
        txDate.text = "${now()}"
    }

    @SuppressLint("SetTextI18n")
    private fun getCount(username: String) {
        var ui = "aa"
        db.collection("users").whereEqualTo("username", username).get()
            .addOnCompleteListener {
                for (dc in it.result!!.documents) {
                    ui = dc.get("id").toString()
                }
                db.collection("kegiatan").whereEqualTo("file_nama", username).get()
                    .addOnSuccessListener {
                        var number = ""
                        when {
                            it.count() < 10 -> number = "00" + (it.count() + 1)
                            it.count() < 100 -> number = "0" + (it.count() + 1)
                            it.count() > 99 -> number = it.count().toString()
                        }
                        txaId_user.setText(
                            """${ui}03$number${LocalDate.now().format(
                                DateTimeFormatter.ofPattern("ddMMyy")
                            )}"""
                        )
//                        Log.e("TAG", """$ui$number""")
                    }
            }
    }

    fun spinner() {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {it2 ->
                db.collection("tour").orderBy("nama", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener {
                        val hm = ArrayList<String>()
                        for (doc in it) {
                            val ar = doc.get("event") as ArrayList<*>
                            when {
                                ar[0] == "false" && doc.get("users")
                                    .toString() == it2.get("username").toString() -> hm.add(
                                    doc.get(
                                        "nama"
                                    ).toString()
                                )
                                ar[0] == "true" -> {
                                    Log.e(doc.get("nama").toString(), tgl(1, ar[1].toString(),null).toString() + " " + tgl(2,null,ar[2].toString()))
                                    when {
                                        tgl(1, ar[1].toString()) >= 0 && tgl(2,null,ar[2].toString()) >= 0 -> {
                                            val data = doc.get("nama").toString() + " (event)"
                                            hm.add(doc.get("nama").toString() + " (event)")
//                                            Log.e("tgl", doc.get("nama").toString() + " (event)")
//                                            Log.e("replace", data.replace(" (event)", ""))
//                                            Log.e("users", txaUser_upload.text.toString())
                                        }
                                    }
                                }
//                        else -> {
//                            hm.add(doc.get("nama").toString())
//                        }
                            }
                        }
                        val adapter = context?.let { it1 ->
                            ArrayAdapter(
                                it1,
                                android.R.layout.simple_spinner_dropdown_item,
                                hm
                            )
                        }
                        spinnerUser.adapter = adapter
                        if (it.size() == 0) txNote.text = "(tempat wisata kosong)"
                        if (spinnerUser.selectedItemPosition != spinPlace) spinnerUser.setSelection(spinPlace)
                    }
            }
    }

    @SuppressLint("SimpleDateFormat")
    fun spinner2() {
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                db.collection("kegiatan").whereEqualTo("file_nama", it.get("username").toString())
                    .get()
                    .addOnSuccessListener { it2 ->
                        for (d in it2) {
//                            Log.e("tot", it2.count().toString())
                            val ary = d["file_tanggal"] as ArrayList<*>
                            when {
                                ary[0] == "Today" -> {
                                    if (ary[1] == LocalDate.now().format(DateTimeFormatter.ofPattern("dd - MM - yyyy"))) spinnerDate.isEnabled = false
                                }
                                ary[0] == "Month" -> {
                                    if (ary[1] == LocalDate.now().format(DateTimeFormatter.ofPattern("MM - yyyy"))) spinnerDate.isEnabled = false
                                }
                            }
                        }
                    }
            }
        var spinnerArray = arrayOf("Today", "Month")
        val adapter = context?.let { it1 ->
            ArrayAdapter(
                it1,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray
            )
        }
        spinnerDate.adapter = adapter
        if (spinnerDate.selectedItemPosition != spinDate) spinnerDate.setSelection(spinDate)
    }

    private fun addFragment(fragment: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentU, fragment, fragment.javaClass.simpleName)
//            .add(R.id.contentU, fragment, fragment.javaClass.simpleName)
            .addToBackStack(null)
            .commit()
//        Log.e(SuperTourFragment.TAG, fragment.javaClass.simpleName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d(TAG, "Photo was selected")
            uri0 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri0)
            view1.setImageBitmap(bitmap)
            imv1.alpha = 0f
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d(TAG, "Photo was selected")
            uri1 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri1)
            view2.setImageBitmap(bitmap)
            imv2.alpha = 0f
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d(TAG, "Photo was selected")
            uri2 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri2)
            view3.setImageBitmap(bitmap)
            imv3.alpha = 0f
        }

        if (requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d(TAG, "Photo was selected")
            uri3 = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri3)
            view4.setImageBitmap(bitmap)
            imv4.alpha = 0f
        }

        if (requestCode == 4 && resultCode == Activity.RESULT_OK && data != null) {
//            Log.d(TAG, "Video was selected")
            uri4 = data.data
            vivew.setVideoURI(data.data)
            vivew.pause()
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imv1 -> {
                spinDate = spinnerDate.selectedItemPosition
                spinPlace = spinnerUser.selectedItemPosition
                val intent = Intent(Intent.ACTION_PICK)
                fileType = ".jpg"
                arrD.add(".jpg")
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
            R.id.imv2 -> {
                spinDate = spinnerDate.selectedItemPosition
                spinPlace = spinnerUser.selectedItemPosition
                val intent = Intent(Intent.ACTION_PICK)
                fileType = ".jpg"
                arrD.add(".jpg")
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
            R.id.imv3 -> {
                spinDate = spinnerDate.selectedItemPosition
                spinPlace = spinnerUser.selectedItemPosition
                val intent = Intent(Intent.ACTION_PICK)
                fileType = ".jpg"
                arrD.add(".jpg")
                intent.type = "image/*"
                startActivityForResult(intent, 2)
            }
            R.id.imv4 -> {
                spinDate = spinnerDate.selectedItemPosition
                spinPlace = spinnerUser.selectedItemPosition
                val intent = Intent(Intent.ACTION_PICK)
                fileType = ".jpg"
                arrD.add(".jpg")
                intent.type = "image/*"
                startActivityForResult(intent, 3)
            }
            R.id.PilihVid -> {
                spinDate = spinnerDate.selectedItemPosition
                spinPlace = spinnerUser.selectedItemPosition
                val intent = Intent(Intent.ACTION_PICK)
                fileVideo = ".mp4"
                arrD.add(".mp4")
                intent.type = "video/*"
                if (intent.resolveActivity(activity!!.packageManager) != null) {
                    startActivityForResult(intent, 4)
                }
            }
            R.id.btSubmit -> {
//                Log.e(tag, txPendapatanD.value.toInt().toString() + " && " + txPendapatanL.value.toInt().toString() + " && " + (txPendapatanD.value.toInt() + txPendapatanL.value.toInt()).toString())
                if (btSubmit.isClickable) {
                    if (!spinnerDate.isEnabled) Toast.makeText(context, "Hari ini / Bulan ini anda telah upload. Mohon dicek ulang!!!", Toast.LENGTH_LONG).show()
                    else {
                        if (txPengunjung.text.toString() == "0" || txPendapatanD.text.toString() == "0" || txPendapatanL.toString() == "0" || txKeterangan.text.isNullOrEmpty()) {
                            Toast.makeText(context, "Data tidak boleh kosong. Mohon dicek ulang!!!", Toast.LENGTH_LONG).show()
                        } else {
                            if (uri0 == Uri.EMPTY && uri1 == Uri.EMPTY && uri2 == Uri.EMPTY && uri3 == Uri.EMPTY && uri4 == Uri.EMPTY) {
                                Toast.makeText(context, "Gambar dan Video tidak boleh kosong. Mohon dicek ulang!!!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                if (uri0 != Uri.EMPTY && uri1 != Uri.EMPTY && uri2 != Uri.EMPTY && uri3 != Uri.EMPTY && uri4 != Uri.EMPTY) {
                    val fileUri = SimpleDateFormat("yyyyMMddHHmmssSSS").format(Date())
                    val date = txDate.text.toString()
                    fileID = txaId_user.text.toString()
                    fileName = txaUser_upload.text.toString()
                    arrDate = arrayListOf(
                        spinnerDate.selectedItem.toString(),
                        txDate.text.toString()
                    )
                    fileTempat = spinnerUser.selectedItem.toString().replace(" (event)", "")
                    filePengunjung = txPengunjung.value.toInt().toString()
                    arrIncome = arrayListOf(
                        txPendapatanD.value.toInt().toString(),
                        txPendapatanL.value.toInt().toString(),
                        (txPendapatanD.value.toInt() + txPendapatanL.value.toInt()).toString()
                    )
                    fileKet = txKeterangan.text.toString()
                    var pack = ""
                    when(spinnerDate.selectedItemPosition) {
                        0 -> {
                            val day = SimpleDateFormat("dd").format(SimpleDateFormat("dd - MM - yyyy").parse(txDate.text.toString()))
                            val month = SimpleDateFormat("MM").format(SimpleDateFormat("dd - MM - yyyy").parse(txDate.text.toString()))
                            val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("dd - MM - yyyy").parse(txDate.text.toString()))
                            pack = year + "/" + month + "/" + day
                            Log.e("ddtttt", pack)
                        }
                        1 -> {
                            val month = SimpleDateFormat("MM").format(SimpleDateFormat("MM - yyyy").parse(txDate.text.toString()))
                            val year = SimpleDateFormat("yyyy").format(SimpleDateFormat("MM - yyyy").parse(txDate.text.toString()))
                            pack = year + "/" + month
                            Log.e("ddtttt", pack)
                        }
                    }
                    val fr0 = storage.child(
                        """upload/$pack/$fileName/Uri0$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri0.toString())}"""
                    )
                    val fr1 = storage.child(
                        """upload/$pack/$fileName/Uri1$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri1.toString())}"""
                    )
                    val fr2 = storage.child(
                        """upload/$pack/$fileName/Uri2$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri2.toString())}"""
                    )
                    val fr3 = storage.child(
                        """upload/$pack/$fileName/Uri3$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri3.toString())}"""
                    )
                    val fr4 = storage.child(
                        """upload/$pack/$fileName/Uri4$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri4.toString())}"""
                    )

                    val progressDialog = ProgressDialog(this.context)
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage("Uploading....")
                    progressDialog.show()

                    val arr = arrayListOf(
                        """Uri0$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri0.toString())}""",
                        """Uri1$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri1.toString())}""",
                        """Uri2$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri2.toString())}""",
                        """Uri3$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri3.toString())}""",
                        """Uri4$fileUri.${MimeTypeMap.getFileExtensionFromUrl(uri4.toString())}"""
                    )
                    var ary = ArrayList<String>()

                    fr0.putFile(uri0)
                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            return@Continuation fr0.downloadUrl
                        })
                        .addOnCompleteListener { it0 ->
                            ary.add(it0.result.toString())
                            fr1.putFile(uri1)
                                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                    return@Continuation fr1.downloadUrl
                                })
                                .addOnCompleteListener { it1 ->
                                    ary.add(it1.result.toString())
                                    fr2.putFile(uri2)
                                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                            return@Continuation fr2.downloadUrl
                                        })
                                        .addOnCompleteListener { it2 ->
                                            ary.add(it2.result.toString())
                                            fr3.putFile(uri3)
                                                .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                                    return@Continuation fr3.downloadUrl
                                                })
                                                .addOnCompleteListener { it3 ->
                                                    ary.add(it3.result.toString())
                                                    fr4.putFile(uri4)
                                                        .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                                            return@Continuation fr4.downloadUrl
                                                        })
                                                        .addOnCompleteListener { it4 ->
                                                            ary.add(it4.result.toString())
                                                            val hm = HashMap<String, Any>()
                                                            hm.put(F_ID, fileID)
                                                            hm.put(F_NAMA, fileName)
                                                            hm.put(F_TANGGAL, arrDate)
                                                            hm.put(F_TEMPAT, fileTempat)
                                                            hm.put(F_PENGUNJUNG, filePengunjung)
                                                            hm.put(F_PENDAPATAN, arrIncome)
                                                            hm.put(F_KET, fileKet)
                                                            hm.put(F_STORAGE, arr)
                                                            hm.put(F_URL, ary)
                                                            db.collection("kegiatan")
                                                                .document(fileUri).set(hm)
                                                                .addOnSuccessListener {
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
                                                                                Log.e("id", id)
                                                                            }
                                                                            if (it5.count() != 0) {
                                                                                x[0].put((y + 1).toString(), arrayListOf("add document(${fileID})", date))
                                                                                db.collection("logs").document(id).update("log", x)
                                                                            } else {
                                                                                val hm = hashMapOf<String, Any>()
                                                                                val hm2 = hashMapOf<String, Any>()
                                                                                val email = FirebaseAuth.getInstance().currentUser!!.email
                                                                                hm2.put("1", arrayListOf("add document(${fileID})", date))
                                                                                hm.put("email", email.toString())
                                                                                hm.put("log", arrayListOf(hm2))
                                                                                db.collection("logs").document().set(hm)
                                                                            }
                                                                        }
                                                                    progressDialog.hide()
                                                                    val fragment = UserUploadFragment()
                                                                    addFragment(fragment)
                                                                    Toast.makeText(
                                                                        this.context,
                                                                        "File succesfully uploaded",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                                .addOnFailureListener {
                                                                    Log.e("error", it.toString())
                                                                    progressDialog.hide()
                                                                    Toast.makeText(
                                                                        this.context,
                                                                        "File unsuccesfully uploaded",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
            }
        }
    }
}