package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.okdroid.checkablechipview.CheckableChipView
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.Super.SuperFilterExcel.excel
import kotlinx.android.synthetic.main.activity_super_filter.*
import org.apache.poi.ss.usermodel.CellStyle.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SuperFilter : AppCompatActivity(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    private var datePicker: PrimeDatePickerBottomSheet? = null
    private var startDate = ""
    private var endDate = ""
    lateinit var db: FirebaseFirestore
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_filter)
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl")

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance().reference
        val t = findViewById<Toolbar>(R.id.toolbarS)
        setSupportActionBar(t)
        var isShow = true
        var scrollRange = -1
        appbarS.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                scrollRange == -1 -> scrollRange = appBarLayout.totalScrollRange
                scrollRange + verticalOffset == 0 -> {
                    toolbar_layoutS.title = "Filter"
                    isShow = true
                }
                isShow -> {
                    toolbar_layoutS.title = " "
                    isShow = false
                }
            }
        })

//        group_multi_radiobutton.setOnCheckedChangeListener { group, checkedId, isChecked ->
//            Log.v("Log","onCheckedStateChanged(): $checkedId, isChecked = $isChecked")
//            Log.v("id", group_multi_radiobutton.checkedIds.toString())
//        }

        TodayF.isChecked = true
        tx1SFC.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        rv()
    }

    override fun onStart() {
        super.onStart()
//        check()
        spin()
        excel()
//        radio()
    }

    private fun excell() {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMYYY"))
        val time2 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("Hms"))
        val fileName = "SuperAdmin$time2.xlsx"
        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val folder = File(extStorageDirectory, "Disbudparpora/$time")
        folder.mkdirs()
        val file = File(folder, fileName)
        try {
            file.createNewFile()
        } catch (e : Exception) { Log.e("e", e.message) }
        val fileOutputStream = FileOutputStream(file)
        val wb= XSSFWorkbook()
        val sh = wb.createSheet("sheet1")
        sh.setColumnWidth(1, 4*256)
        sh.setColumnWidth(5, 10*256)

        try {
            sh.addMergedRegion(CellRangeAddress.valueOf("B2:F3"))
            val font = wb.createFont().apply {
                fontName = "Courier New"
                italic = false
                bold = true
            }
            val style = wb.createCellStyle().apply {
                alignment = ALIGN_CENTER
                wrapText = true
                setFont(font)
            }
            val kop = sh.createRow(1).createCell(1)
            kop.cellStyle = style
            kop.setCellValue("Dinas Kebudayaan, Pariwisata, Kepemudaan dan Olahraga Kota Kediri")

            try {
                sh.addMergedRegion(CellRangeAddress.valueOf("B5:B6"))
                sh.addMergedRegion(CellRangeAddress.valueOf("C5:E5"))
                sh.addMergedRegion(CellRangeAddress.valueOf("F5:F6"))
                sh.addMergedRegion(CellRangeAddress.valueOf("B9:D9"))

                val f1 = wb.createFont().apply { bold = true }
                val f2 = wb.createFont().apply { italic = true }
                val st1 = wb.createCellStyle().apply {
                    alignment = ALIGN_CENTER
                    verticalAlignment = VERTICAL_CENTER
                    borderLeft = BORDER_THIN
                    borderTop = BORDER_THIN
                    borderRight = BORDER_THIN
                    borderBottom = BORDER_THIN
                }
                val st2 = wb.createCellStyle().apply {
                    alignment = ALIGN_RIGHT
                    setFont(f2)
                    borderLeft = BORDER_THIN
                    borderTop = BORDER_THIN
                    borderRight = BORDER_THIN
                    borderBottom = BORDER_THIN
                }
                val st3 = wb.createCellStyle().apply {
                    borderLeft = BORDER_THIN
                    borderTop = BORDER_THIN
                    borderRight = BORDER_THIN
                    borderBottom = BORDER_THIN
                }

                for (x in 4..8) {
                    val z = sh.createRow(x)
                    for (y in 1..5) {
                        val v = z.createCell(y)
                        val cs = wb.createCellStyle()
                        cs.setBorderLeft(BORDER_THIN)
                        cs.setBorderTop(BORDER_THIN)
                        cs.setBorderRight(BORDER_THIN)
                        cs.setBorderBottom(BORDER_THIN)
                        v.cellStyle = cs
                        when {
                            y == 1 && x == 4 -> {
                                v.setCellValue("No")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 2 && x == 4 -> {
                                v.setCellValue("Pengunjung")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 5 && x == 4 -> {
                                v.setCellValue("Pemasukan")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 2 && x == 5 -> {
                                v.setCellValue("Local")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 3 && x == 5 -> {
                                v.setCellValue("Manca")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 4 && x == 5 -> {
                                v.setCellValue("Total")
                                cs.alignment = ALIGN_CENTER
                                cs.verticalAlignment = VERTICAL_CENTER
                                v.cellStyle = cs
                            }
                            y == 1 && x == 8 -> {
                                v.setCellValue("Total  ")
                                cs.alignment = ALIGN_CENTER
                                cs.setFont(f2)
                                v.cellStyle = cs
                            }
                        }
                    }
                }
                Log.e("sukses", "excell")
            } catch (e : Exception) { Log.e("e2", e.message) }
        } catch (e : Exception) { Log.e("e3", e.message) }

        wb.write(fileOutputStream)
        fileOutputStream.close()
    }

    @SuppressLint("ResourceType")
//    private fun radio() {
//        val t = 2.8
//        val tg = findViewById<ToggleButtonGroup>(R.id.group_multi_radiobutton)
//        val r = CustomCompoundButton(this)
//        val y = R.string.user + 1
//        r.id = y
//        r.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (40*t).toInt())
////        r.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
//        r.setPadding((16).toInt(), (8).toInt(), (16).toInt(), (8).toInt())
////        r.background = ContextCompat.getDrawable(this, R.drawable.selector_bg_radio_button)
////        r.setTextColor(resources.getColor(R.color.selector_text_radio_button))
//        r.text = "Ipsum"
////        r.setTextSize((16).toFloat())
//        tg.addView(r)
//    }

    private fun spin() {
        val hm = ArrayList<String>()
        hm.add("----- Select User -----")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hm)
        spinnerUserSF.adapter = adapter
    }

    @SuppressLint("SimpleDateFormat")
    private fun check() {
        cvDateSF.setOnClickListener {
            val today = CalendarFactory.newInstance(CalendarType.CIVIL)
            val pickType = PickType.RANGE_START
            if (txDateSF.text.equals("----- Select Date Range -----")) {
                datePicker = PrimeDatePickerBottomSheet.newInstance(
                    currentDateCalendar = today,
                    pickType = pickType
                )
            } else {
                val sd = txDateSF.text.replaceRange(9, txDateSF.text.lastIndex, "")
                val ed = txDateSF.text.replaceRange(0, 13, "")
                Log.e("date1", sd.toString())
//                Log.e("date11", SimpleDateFormat("yyyy").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())))
//                Log.e("date12", SimpleDateFormat("M").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())))
//                Log.e("date13", SimpleDateFormat("d").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())))
                Log.e("date2", ed.toString())
                val startPick = CalendarFactory.newInstance(CalendarType.CIVIL)
                startPick.set(
                    SimpleDateFormat("yyyy").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())).toInt(),
                    SimpleDateFormat("M").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())).toInt() - 1,
                    SimpleDateFormat("d").format(SimpleDateFormat("dd/MM/yyyy").parse(sd.toString())).toInt()
                )
                val endPick = CalendarFactory.newInstance(CalendarType.CIVIL)
                endPick.set(
                    SimpleDateFormat("yyyy").format(SimpleDateFormat("dd/MM/yyyy").parse(ed.toString())).toInt(),
                    SimpleDateFormat("M").format(SimpleDateFormat("dd/MM/yyyy").parse(ed.toString())).toInt() - 1,
                    SimpleDateFormat("d").format(SimpleDateFormat("dd/MM/yyyy").parse(ed.toString())).toInt()
                )
                datePicker = PrimeDatePickerBottomSheet.newInstance(
                    currentDateCalendar = today,
                    pickType = pickType,
                    pickedRangeStartCalendar = startPick,
                    pickedRangeEndCalendar = endPick
                )
            }
            datePicker?.setOnDateSetListener(this)
            datePicker?.show(this.supportFragmentManager, "PrimeDatePickerBottomSheet")
            TodayF.isChecked = false
            MonthF.isChecked = false
            YearF.isChecked = false
        }

        TodayF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                MonthF.isChecked = false
                YearF.isChecked = false
                txDateSF.text = "----- Select Date Range -----"
                rv()
            }
        }

        MonthF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                TodayF.isChecked = false
                YearF.isChecked = false
                txDateSF.text = "----- Select Date Range -----"
                rv()
            }
        }

        YearF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                TodayF.isChecked = false
                MonthF.isChecked = false
                txDateSF.text = "----- Select Date Range -----"
                rv()
            }
        }

        tx1SFC.setOnClickListener {
            tx1SFC.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            tx2SFC.paintFlags = 0
            tx3SFC.paintFlags = 0
//            rv()
        }

        tx2SFC.setOnClickListener {
            tx2SFC.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            tx1SFC.paintFlags = 0
            tx3SFC.paintFlags = 0
//            rv()
        }

        tx3SFC.setOnClickListener {
            tx3SFC.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            tx2SFC.paintFlags = 0
            tx1SFC.paintFlags = 0
//            rv()
        }
    }

    private fun rv() {
        val hm = HashMap<String, Any>()
        hm.put("yy", "xx")
        val x = ArrayList<Item>()
        x.add(
            Item(
                arrayOf(
                    arrayOf("10", ItemP("1", "100", "200"), ItemP("1", "100", "200")),
                    arrayOf("11", ItemP("1", "100", "200"), ItemP("1", "100", "200"))
                )
            )
        )
        rvFilter.layoutManager = LinearLayoutManager(this)
        rvFilter.adapter = FilterAdapter(x)
    }

    inner class FilterAdapter(val dataFilter: ArrayList<Item>) :
        RecyclerView.Adapter<FilterAdapter.HolderFilter>() {
        inner class HolderFilter(v: View) : RecyclerView.ViewHolder(v) {
            val cv1 = v.findViewById<LinearLayout>(R.id.cvItemF)!!
            val cv2 = v.findViewById<LinearLayout>(R.id.cvItem2)!!
            val llnum = v.findViewById<LinearLayout>(R.id.LLNum)
            val rvSF = v.findViewById<RecyclerView>(R.id.rvSF)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFilter {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_super_filter, parent, false)
            return HolderFilter(v)
        }

        override fun getItemCount(): Int = dataFilter.size

        override fun onBindViewHolder(holder: HolderFilter, position: Int) {
            var x = 0
            holder.cv1.setOnClickListener {
                holder.cv2.visibility = View.VISIBLE
                if (x == 0) {
                    YoYo.with(Techniques.FadeOutUp)
                        .duration(1000)
                        .playOn(holder.cv1)
                    YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(holder.cv2)
                    x = 1
                }
                else {
                    YoYo.with(Techniques.FadeInDown)
                        .duration(1000)
                        .playOn(holder.cv1)
                    YoYo.with(Techniques.FadeOutUp)
                        .duration(1000)
                        .withListener(object : Animator.AnimatorListener{
                            override fun onAnimationRepeat(animation: Animator?) {}
                            override fun onAnimationEnd(animation: Animator?) { holder.cv2.visibility = View.GONE }
                            override fun onAnimationCancel(animation: Animator?) {}
                            override fun onAnimationStart(animation: Animator?) {}
                        })
                        .playOn(holder.cv2)
                    x = 0
                }
            }
            val u = 9
            var z = 0
            for (i in u downTo 0 step 5) {
                z += 1
                val tx = TextView(this@SuperFilter)
                val l = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                l.setMargins(10,0,10,0)
                tx.apply {
                    layoutParams = l
                    setTextColor(resources.getColor(R.color.blackTextColor))
                    background = resources.getDrawable(R.drawable.circle)
                    setPadding(5,5,5,5)
                    textSize = 20.0f
                    text = z.toString()
                    id = 100 + z
                }
                holder.llnum.addView(tx)
            }
            for (item in holder.llnum.children) {
                if (item is TextView) {
                    item.setOnClickListener {
                        item.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                        for (i in 0..holder.llnum.childCount) {
                            val x = holder.llnum.getChildAt(i)
                            if (x != item) {
                                if (x is TextView) {
                                    x.paintFlags = 0
                                }
                            }
                        }
                    }
                }
            }

            val y = ArrayList<Any>()
            var w = 0
            Log.e("ar1", dataFilter[position].ar1.size.toString())
            for (ar1 in dataFilter[position].ar1) {
                Log.e("ar2", ar1.size.toString())
                for (ar2 in ar1) {
                    when(ar2) {
                        is String -> {
                            w = 0
                            y.add(ar2)
                        }
                        is ItemP -> {
                            w++
                            y.add(ItemP(w.toString(), ar2.pgD, ar2.pgL))
                        }
                    }
                }
            }
            holder.rvSF.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = CellFilterAdapter(y)
            }
        }
    }

    class CellFilterAdapter(val cellFilter: ArrayList<Any>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            private const val ITEM_HEADER = 0
            private const val ITEM_MENU = 1
        }

        class HolderCell(v: View) : RecyclerView.ViewHolder(v) {
            val no = v.findViewById(R.id.txCellNo) as TextView
            val pgD = v.findViewById(R.id.txCellPengunjungD) as TextView
            val pgL = v.findViewById(R.id.txCellPengunjungL) as TextView
            val pgT = v.findViewById(R.id.txCellPengunjungT) as TextView

            fun bindContent(item: ItemP) {
                no.text = item.no
                pgD.text = "Rp.${item.pgD}"
                pgL.text = "Rp.${item.pgL}"
                pgT.text = "Rp.${item.pgT}"
            }
        }

        class MenuHeaderHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemHeader = v.findViewById(R.id.txHeaderCell) as TextView

            fun bindContent(text: String){
                itemHeader.text = text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType) {
                ITEM_HEADER -> MenuHeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_super_filter_cell_header, parent, false))
                ITEM_MENU -> HolderCell(LayoutInflater.from(parent.context).inflate(R.layout.item_super_filter_cell, parent, false))
                else -> throw throw IllegalArgumentException("Undefined view type")
            }
        }

        override fun getItemCount(): Int = cellFilter.size

        override fun getItemViewType(position: Int): Int {
            return when (cellFilter[position]) {
                is String -> ITEM_HEADER
                is ItemP -> ITEM_MENU
                else -> throw IllegalArgumentException("Undefined view type")
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                ITEM_HEADER -> {
                    val headerHolder = holder as MenuHeaderHolder
                    headerHolder.bindContent(cellFilter[position] as String)
                }
                ITEM_MENU -> {
                    val itemHolder = holder as HolderCell
                    itemHolder.bindContent(cellFilter[position] as ItemP)
                }
                else -> throw IllegalArgumentException("Undefined view type")
            }
        }
    }

    inner class Item(val ar1: Array<Array<Any>>)
    inner class ItemP(val no: String, val pgD: String, val pgL: String, val pgT: String = (pgD.toInt() + pgL.toInt()).toString())

    override fun onMultipleDaysPicked(multipleDays: List<PrimeCalendar>) {
        TODO("Not yet implemented")
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onRangeDaysPicked(startDay: PrimeCalendar, endDay: PrimeCalendar) {
        Toast.makeText(this, "From: ${startDay.longDateString}\nTo: ${endDay.longDateString}", Toast.LENGTH_LONG).show()
        startDate = SimpleDateFormat("dd/MM/yyyy").format(SimpleDateFormat("yyyy/MM/dd").parse(startDay.shortDateString))
        endDate = SimpleDateFormat("dd/MM/yyyy").format(SimpleDateFormat("yyyy/MM/dd").parse(endDay.shortDateString))
        txDateSF.text = "$startDate - $endDate"
    }

    override fun onSingleDayPicked(singleDay: PrimeCalendar) {
        TODO("Not yet implemented")
    }
}