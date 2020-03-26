package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
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
import com.nex3z.togglebuttongroup.ToggleButtonGroup
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.Super.Button.CustomCompoundButton
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.activity_super_filter.*
import kotlinx.android.synthetic.main.fragment_super_tour.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SuperFilter : AppCompatActivity(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    private var datePicker: PrimeDatePickerBottomSheet? = null
    private var startDate = ""
    private var endDate = ""
    lateinit var db: FirebaseFirestore
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_filter)
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
//        radio()
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
        val x = ArrayList<HashMap<String, Any>>()
        x.add(hm)
        x.add(hm)
        x.add(hm)
        rvFilter.layoutManager = LinearLayoutManager(this)
        rvFilter.adapter = FilterAdapter(x)
//        // prepare elements to display
//        val items: ArrayList<Item> = Item().getTestingList() as ArrayList<Item>
//
//        // add custom btn handler to first list item
//        items[0].setRequestBtnClickListener(View.OnClickListener {
//            Toast.makeText(this, "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show()
//        })
//
//        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
//        val adapter = ListAdapter(this, items)
//
//        // add default btn handler for each request btn on each item if custom handler not found
//        adapter.setDefaultRequestBtnClickListener(View.OnClickListener {
//            Toast.makeText(this, "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show()
//        })
//
//        // set elements to adapter
//        lvFilter.adapter = adapter
//
//        // set on click event listener to list view
//        lvFilter.onItemClickListener = AdapterView.OnItemClickListener { _, view, pos, _ ->
//            // toggle clicked cell state
//            (view as FoldingCell).toggle(false)
//            // register in adapter that state for selected cell is toggled
//            adapter.registerToggle(pos)
//        }
    }

    inner class FilterAdapter(val dataFilter: List<HashMap<String, Any>>) :
        RecyclerView.Adapter<FilterAdapter.HolderFilter>() {
        inner class HolderFilter(v: View) : RecyclerView.ViewHolder(v) {
            val cv1 = v.findViewById<LinearLayout>(R.id.cvItemF)!!
            val cv2 = v.findViewById<LinearLayout>(R.id.cvItem2)!!
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
        }

    }

    inner class Item() {
        private var price: String? = null
        private var pledgePrice: String? = null
        private var fromAddress: String? = null
        private var toAddress: String? = null
        private var requestsCount = 0
        private var date: String? = null
        private var time: String? = null
        private var itemP: ArrayList<ItemP>? = null
        private var requestBtnClickListener: View.OnClickListener? = null

        constructor(price: String, pledgePrice: String, fromAddress: String, toAddress: String, requestsCount: Int, date: String, time: String, itemP: ArrayList<ItemP>) : this() {
            this.price = price
            this.pledgePrice = pledgePrice
            this.fromAddress = fromAddress
            this.toAddress = toAddress
            this.requestsCount = requestsCount
            this.date = date
            this.time = time
            this.itemP = itemP
        }

        fun getPrice(): String? { return price }

        fun getPledgePrice(): String? { return pledgePrice }

        fun getFromAddress(): String? { return fromAddress }

        fun getToAddress(): String? { return toAddress }

        fun getRequestsCount(): Int { return requestsCount }

        fun getDate(): String? { return date }

        fun getTime(): String? { return time }

        fun getItemP():  ArrayList<ItemP>? { return itemP }

        fun getRequestBtnClickListener(): View.OnClickListener? { return requestBtnClickListener }

        fun setRequestBtnClickListener(requestBtnClickListener: View.OnClickListener?) { this.requestBtnClickListener = requestBtnClickListener }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val item: Item = other as Item
            if (requestsCount != item.requestsCount) return false
            if (if (price != null) price != item.price else item.price != null) return false
            if (if (pledgePrice != null) pledgePrice != item.pledgePrice else item.pledgePrice != null) return false
            if (if (fromAddress != null) fromAddress != item.fromAddress else item.fromAddress != null) return false
            if (if (toAddress != null) toAddress != item.toAddress else item.toAddress != null) return false
            return if (if (date != null) date != item.date else item.date != null) false else !if (time != null) time != item.time else item.time != null
        }

        override fun hashCode(): Int {
            var result = if (price != null) price.hashCode() else 0
            result = 31 * result + if (pledgePrice != null) pledgePrice.hashCode() else 0
            result = 31 * result + if (fromAddress != null) fromAddress.hashCode() else 0
            result = 31 * result + if (toAddress != null) toAddress.hashCode() else 0
            result = 31 * result + requestsCount
            result = 31 * result + if (date != null) date.hashCode() else 0
            result = 31 * result + if (time != null) time.hashCode() else 0
            return result
        }

        fun getTestingList(): ArrayList<Item>? {
            val items = ArrayList<Item>()
            val itemsP = ArrayList<ItemP>()
            when {
                TodayF.isChecked == true -> {
//                    when {
//                        tx1SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 5, "TODAY", "05:10 PM", itemsP))
//                        }
//                        tx2SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
//                            itemsP.add(ItemP("1", "1", "1"))
//                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 10, "TODAY", "05:10 PM", itemsP))
//                        }
//                        tx3SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
//                            itemsP.add(ItemP("1", "1", "1"))
//                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 15, "TODAY", "05:10 PM", itemsP))
//                        }
//                    }
                }
                MonthF.isChecked == true -> {
                    when {
                        tx1SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 5, "TODAY", "05:10 PM", itemsP))
                        }
                        tx2SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 10, "TODAY", "05:10 PM", itemsP))
                        }
                        tx3SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 15, "TODAY", "05:10 PM", itemsP))
                        }
                    }
                }
                YearF.isChecked == true -> {
                    when {
                        tx1SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 5, "TODAY", "05:10 PM", itemsP))
                        }
                        tx2SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 10, "TODAY", "05:10 PM", itemsP))
                        }
                        tx3SFC.paintFlags == Paint.UNDERLINE_TEXT_FLAG -> {
                            itemsP.add(ItemP("1", "1", "1"))
                            items.add(Item("$14", "$270","W 79th St, NY, 10024", "W 139th St, NY, 10030", 15, "TODAY", "05:10 PM", itemsP))
                        }
                    }
                }
            }

            return items
        }
    }

    inner class ItemP(val pengunjungD: String, val pengunjungL: String, val pengunjungT: String)

//    inner class ListAdapter(context: Context, objects: List<Item?>) : ArrayAdapter<Item>(context, 0, objects) {
//        private var unfoldedIndexes = HashSet<Int>()
//        private var defaultRequestBtnClickListener: View.OnClickListener? = null
//
//        @SuppressLint("SetTextI18n")
//        override fun getView(
//            position: Int,
//            convertView: View?,
//            parent: ViewGroup
//        ): View? {
//            // get item for selected view
//            val item: Item? = getItem(position)
//            // if cell is exists - reuse it, if not - create the new one from resource
//            var cell = convertView as FoldingCell?
//            val viewHolder: ViewHolder
//            if (cell == null) {
//                viewHolder = ViewHolder()
//                val vi = LayoutInflater.from(this@SuperFilter)
//                cell = vi.inflate(R.layout.activity_super_filter_cell, parent, false) as FoldingCell
//                // binding view parts to view holder
//                viewHolder.price = cell.findViewById(R.id.title_price)
//                viewHolder.time = cell.findViewById(R.id.title_time_label)
//                viewHolder.date = cell.findViewById(R.id.title_date_label)
//                viewHolder.fromAddress = cell.findViewById(R.id.title_from_address)
//                viewHolder.toAddress = cell.findViewById(R.id.title_to_address)
//                viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count)
//                viewHolder.requestsCountContent = cell.findViewById(R.id.head_image_left_text)
//                viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge)
//                viewHolder.rl = cell.findViewById(R.id.RLPSF)
//                viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn)
//                cell.tag = viewHolder
//            } else {
//                // for existing cell set valid valid state(without animation)
//                if (unfoldedIndexes.contains(position)) {
//                    cell.unfold(true)
//                } else {
//                    cell.fold(true)
//                }
//                viewHolder = cell.tag as ViewHolder
//            }
//            if (item == null) return cell
//
//            // bind data from selected element to view through view holder
//            viewHolder.price!!.text = item.getPrice()
//            viewHolder.time!!.text = item.getTime()
//            viewHolder.date!!.text = item.getDate()
//            viewHolder.fromAddress!!.text = item.getFromAddress()
//            viewHolder.toAddress!!.text = item.getToAddress()
//            viewHolder.requestsCount!!.text = java.lang.String.valueOf(item.getRequestsCount())
//            viewHolder.requestsCountContent!!.text = java.lang.String.valueOf(item.getRequestsCount())
//            viewHolder.pledgePrice!!.text = item.getPledgePrice()
//
//            Log.e("itemP", item.getItemP()!!.get(0).pengunjungD)
////            innerList(viewHolder)
//
//            // set custom btn handler for list item from that item
//            if (item.getRequestBtnClickListener() != null) {
//                viewHolder.contentRequestBtn!!.setOnClickListener(item.getRequestBtnClickListener())
//            } else {
//                // (optionally) add "default" handler if no handler found in item
//                viewHolder.contentRequestBtn!!.setOnClickListener(defaultRequestBtnClickListener)
//            }
//
//            return cell
//        }
//
//        private fun innerList(vh : ViewHolder) {
//            val z = 10
//            try {
//                // bind data from selected element to view through view holder (Pengunjung)
//                val lv = LinearLayout(this@SuperFilter)
//                lv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                lv.orientation = LinearLayout.VERTICAL
//                lv.isBaselineAligned = false
//
//                val lh0 = LinearLayout(this@SuperFilter)
//                lh0.layoutParams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                lh0.orientation = LinearLayout.HORIZONTAL
//                lh0.weightSum = 4.0f
//                lh0.isBaselineAligned = false
//                (0 until 4).forEach {
//                    val tx = TextView(this@SuperFilter)
//                    tx.apply {
//                        background = resources.getDrawable(R.drawable.border2)
//                        layoutParams = LinearLayout.LayoutParams(
//                            0,
//                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                            1.2f
//                        )
//                        textAlignment = View.TEXT_ALIGNMENT_CENTER
//                        setTypeface(typeface, Typeface.BOLD)
//                        when (it) {
//                            0 -> {
//                                layoutParams = LinearLayout.LayoutParams(
//                                    0,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                                    0.4f
//                                )
//                                text = "No."
//                            }
//                            1 -> text = "Local"
//                            2 -> text = "Manca"
//                            3 -> text = "Total"
//                        }
//                    }
//                    lh0.addView(tx)
//                }
//                lv.addView(lh0)
//
//                var y = 18
//                var x = 0
//                for (it in 1 .. z) {
//                    val lh = LinearLayout(this@SuperFilter)
//                    lh.layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    lh.orientation = LinearLayout.HORIZONTAL
//                    lh.weightSum = 4.0f
//                    lh.isBaselineAligned = false
//                    (0 until 4).forEach { i ->
//                        val tx = TextView(this@SuperFilter)
//                        tx.apply {
//                            background = resources.getDrawable(R.drawable.border2)
//                            setTextColor(resources.getColor(R.color.blackTextColor))
//                            when {
//                                i == 0 -> {
//                                    layoutParams = LinearLayout.LayoutParams(
//                                        0,
//                                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                                        0.4f
//                                    )
//                                    textAlignment = View.TEXT_ALIGNMENT_CENTER
//                                    text = "$it."
//                                }
//                                else -> {
//                                    layoutParams = LinearLayout.LayoutParams(
//                                        0,
//                                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                                        1.2f
//                                    )
//                                    setPadding(5, 0, 0, 0)
//                                    text = "oke$it$i"
//                                }
//                            }
//                        }
//                        lh.addView(tx)
//                    }
//                    lv.addView(lh)
//                    x = it
//                    Log.e("x1$it", x.toString())
//                }
//                vh.rl!!.setOnClickListener {
//                    when {
//                        (y - x) < z && (y - x) > 0 -> {
//                            Log.e("y", (y - x).toString())
//                            for (it in 1 .. y - x) {
//                                val lh = LinearLayout(this@SuperFilter)
//                                lh.layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                )
//                                lh.orientation = LinearLayout.HORIZONTAL
//                                lh.weightSum = 4.0f
//                                lh.isBaselineAligned = false
//                                (0 until 4).forEach { i ->
//                                    val tx = TextView(this@SuperFilter)
//                                    tx.setTextColor(resources.getColor(R.color.blackTextColor))
//                                    tx.background = resources.getDrawable(R.drawable.border2)
//                                    if (i == 0) {
//                                        tx.layoutParams = LinearLayout.LayoutParams(
//                                            0,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                                            0.4f
//                                        )
//                                        tx.textAlignment = View.TEXT_ALIGNMENT_CENTER
//                                        tx.text = "${x + it}."
//                                    }
//                                    else {
//                                        tx.layoutParams = LinearLayout.LayoutParams(
//                                            0,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                                            1.2f
//                                        )
//                                        tx.setPadding(5, 0, 0, 0)
//                                        tx.text = "oke${x + it}$i"
//                                    }
//                                    lh.addView(tx)
//                                }
//                                lv.addView(lh)
//                                Log.e("x", "${x + it}")
//                            }
//                            y = 0
//                        }
//                        (y - x) >= z -> {
//                            for (it in x + 1 .. z + x) {
//                                val lh = LinearLayout(this@SuperFilter)
//                                lh.layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                )
//                                lh.orientation = LinearLayout.HORIZONTAL
//                                lh.weightSum = 4.0f
//                                lh.isBaselineAligned = false
//                                (0 until 4).forEach { i ->
//                                    val tx = TextView(this@SuperFilter)
//                                    tx.setTextColor(resources.getColor(R.color.blackTextColor))
//                                    tx.background = resources.getDrawable(R.drawable.border2)
//                                    if (i == 0) {
//                                        tx.layoutParams = LinearLayout.LayoutParams(
//                                            0,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                                            0.4f
//                                        )
//                                        tx.textAlignment = View.TEXT_ALIGNMENT_CENTER
//                                        tx.text = "$it."
//                                    }
//                                    else {
//                                        tx.layoutParams = LinearLayout.LayoutParams(
//                                            0,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                                            1.2f
//                                        )
//                                        tx.setPadding(5, 0, 0, 0)
//                                        tx.text = "oke$it$i"
//                                    }
//                                    lh.addView(tx)
//                                }
//                                lv.addView(lh)
//                                x = it
//                                Log.e("x1$it", x.toString())
//                            }
//                        }
//                    }
//                }
//                vh.rl!!.addView(lv)
//            } catch (e : Exception) {
//                Log.e("ex", e.message)
//            }
//        }
//
//        fun registerToggle(position: Int) {
//            if (unfoldedIndexes.contains(position)) registerFold(position) else registerUnfold(position)
//        }
//
//        private fun registerFold(position: Int) = unfoldedIndexes.remove(position)
//
//        private fun registerUnfold(position: Int) = unfoldedIndexes.add(position)
//
//        fun getDefaultRequestBtnClickListener(): View.OnClickListener? {
//            return defaultRequestBtnClickListener
//        }
//
//        fun setDefaultRequestBtnClickListener(defaultRequestBtnClickListener: View.OnClickListener?) {
//            this.defaultRequestBtnClickListener = defaultRequestBtnClickListener
//        }
//
//        // View lookup cache
//        inner class ViewHolder {
//            var price: TextView? = null
//            var contentRequestBtn: TextView? = null
//            var pledgePrice: TextView? = null
//            var fromAddress: TextView? = null
//            var toAddress: TextView? = null
//            var requestsCount: TextView? = null
//            var requestsCountContent: TextView? = null
//            var date: TextView? = null
//            var time: TextView? = null
//            var rl: RelativeLayout? = null
//        }
//    }

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