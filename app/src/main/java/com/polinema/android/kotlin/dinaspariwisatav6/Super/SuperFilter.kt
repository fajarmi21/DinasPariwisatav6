package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.okdroid.checkablechipview.CheckableChipView
import com.google.android.material.appbar.AppBarLayout
import com.nex3z.togglebuttongroup.ToggleButtonGroup
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.Super.Button.CustomCompoundButton
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.activity_super_filter.*
import kotlinx.android.synthetic.main.activity_super_filter_cell_content_layout.*
import java.util.*
import kotlin.collections.ArrayList

class SuperFilter : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_filter)
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

        group_multi_radiobutton.setOnCheckedChangeListener { group, checkedId, isChecked ->
            Log.v("Log","onCheckedStateChanged(): $checkedId, isChecked = $isChecked")
            Log.v("id", group_multi_radiobutton.checkedIds.toString())
        }

        rv()
    }

    override fun onStart() {
        super.onStart()
        check()
        spin()
//        radio()
    }

    private fun rv() {
        // prepare elements to display
        val items: ArrayList<Item> = Item().getTestingList() as ArrayList<Item>

        // add custom btn handler to first list item

        // add custom btn handler to first list item
        items[0].setRequestBtnClickListener(View.OnClickListener {
            Toast.makeText(this, "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show()
        })

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        val adapter = ListAdapter(this, items)

        // add default btn handler for each request btn on each item if custom handler not found
        adapter.setDefaultRequestBtnClickListener(View.OnClickListener {
            Toast.makeText(this, "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show()
        })

        // set elements to adapter
        lvFilter.adapter = adapter

        // set on click event listener to list view
        lvFilter.onItemClickListener = AdapterView.OnItemClickListener { _, view, pos, _ -> // toggle clicked cell state
//            ll_cell_content.visibility = View.VISIBLE
            (view as FoldingCell).toggle(false)
            // register in adapter that state for selected cell is toggled
            adapter.registerToggle(pos)
        }
    }

    @SuppressLint("ResourceType")
    private fun radio() {
        val t = 2.8
        val tg = findViewById<ToggleButtonGroup>(R.id.group_multi_radiobutton)
        val r = CustomCompoundButton(this)
        val y = R.string.user + 1
        r.id = y
        r.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (40*t).toInt())
//        r.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        r.setPadding((16).toInt(), (8).toInt(), (16).toInt(), (8).toInt())
//        r.background = ContextCompat.getDrawable(this, R.drawable.selector_bg_radio_button)
//        r.setTextColor(resources.getColor(R.color.selector_text_radio_button))
        r.text = "Ipsum"
//        r.setTextSize((16).toFloat())
        tg.addView(r)
    }

    private fun spin() {
        val hm = ArrayList<String>()
        hm.add("----- Select User -----")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hm)
        spinnerUserSF.adapter = adapter
    }

    private fun check() {
        TodayF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                MonthF.isChecked = false
                YearF.isChecked = false
            }
        }

        MonthF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                TodayF.isChecked = false
                YearF.isChecked = false
            }
        }

        YearF.onCheckedChangeListener = { view: CheckableChipView, isChecked: Boolean ->
            if (isChecked) {
                Toast.makeText(this, "${view.text} checked: $isChecked", Toast.LENGTH_SHORT).show()
                TodayF.isChecked = false
                MonthF.isChecked = false
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
        private var requestBtnClickListener: View.OnClickListener? = null

        constructor(price: String, pledgePrice: String, fromAddress: String, toAddress: String, requestsCount: Int, date: String, time: String) : this() {
            this.price = price
            this.pledgePrice = pledgePrice
            this.fromAddress = fromAddress
            this.toAddress = toAddress
            this.requestsCount = requestsCount
            this.date = date
            this.time = time
        }

        fun getPrice(): String? { return price }

        fun setPrice(price: String?) { this.price = price }

        fun getPledgePrice(): String? { return pledgePrice }

        fun setPledgePrice(pledgePrice: String?) { this.pledgePrice = pledgePrice }

        fun getFromAddress(): String? { return fromAddress }

        fun setFromAddress(fromAddress: String?) { this.fromAddress = fromAddress }

        fun getToAddress(): String? { return toAddress }

        fun setToAddress(toAddress: String?) { this.toAddress = toAddress }

        fun getRequestsCount(): Int { return requestsCount }

        fun setRequestsCount(requestsCount: Int) { this.requestsCount = requestsCount }

        fun getDate(): String? { return date }

        fun setDate(date: String?) { this.date = date }

        fun getTime(): String? { return time }

        fun setTime(time: String?) { this.time = time }

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

        public fun getTestingList(): ArrayList<Item>? {
            val items = ArrayList<Item>()
            items.add(Item("$14","$270","W 79th St, NY, 10024","W 139th St, NY, 10030",3,"TODAY","05:10 PM"))

            return items
        }
    }

    inner class ListAdapter(context: Context, objects: List<Item?>) : ArrayAdapter<Item>(context, 0, objects) {
        private var unfoldedIndexes = HashSet<Int>()
        private var defaultRequestBtnClickListener: View.OnClickListener? = null

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View? {
            // get item for selected view
            val item: Item? = getItem(position)
            // if cell is exists - reuse it, if not - create the new one from resource
            var cell = convertView as FoldingCell?
            val viewHolder: ViewHolder
            if (cell == null) {
                viewHolder = ViewHolder()
                val vi = LayoutInflater.from(this@SuperFilter)
                cell = vi.inflate(R.layout.activity_super_filter_cell, parent, false) as FoldingCell
                // binding view parts to view holder
                viewHolder.price = cell.findViewById(R.id.title_price)
                viewHolder.time = cell.findViewById(R.id.title_time_label)
                viewHolder.date = cell.findViewById<TextView>(R.id.title_date_label)
                viewHolder.fromAddress = cell.findViewById(R.id.title_from_address)
                viewHolder.toAddress = cell.findViewById(R.id.title_to_address)
                viewHolder.requestsCount = cell.findViewById(R.id.title_requests_count)
                viewHolder.pledgePrice = cell.findViewById(R.id.title_pledge)
                viewHolder.contentRequestBtn = cell.findViewById(R.id.content_request_btn)
                cell.tag = viewHolder
            } else {
                // for existing cell set valid valid state(without animation)
                if (unfoldedIndexes.contains(position)) {
                    cell.unfold(true)
                } else {
                    cell.fold(true)
                }
                viewHolder =
                    cell.tag as ViewHolder
            }
            if (item == null) return cell

            // bind data from selected element to view through view holder
            viewHolder.price!!.text = item.getPrice()
            viewHolder.time!!.text = item.getTime()
            viewHolder.date!!.text = item.getDate()
            viewHolder.fromAddress!!.text = item.getFromAddress()
            viewHolder.toAddress!!.text = item.getToAddress()
            viewHolder.requestsCount!!.text = java.lang.String.valueOf(item.getRequestsCount())
            viewHolder.pledgePrice!!.text = item.getPledgePrice()

            // set custom btn handler for list item from that item
            if (item.getRequestBtnClickListener() != null) {
                viewHolder.contentRequestBtn!!.setOnClickListener(item.getRequestBtnClickListener())
            } else {
                // (optionally) add "default" handler if no handler found in item
                viewHolder.contentRequestBtn!!.setOnClickListener(defaultRequestBtnClickListener)
            }
            return cell
        }

        fun registerToggle(position: Int) {
            if (unfoldedIndexes.contains(position)) registerFold(position) else registerUnfold(position)
        }

        private fun registerFold(position: Int) {
            unfoldedIndexes.remove(position)
        }

        private fun registerUnfold(position: Int) {
            unfoldedIndexes.add(position)
        }

        fun getDefaultRequestBtnClickListener(): View.OnClickListener? {
            return defaultRequestBtnClickListener
        }

        fun setDefaultRequestBtnClickListener(defaultRequestBtnClickListener: View.OnClickListener?) {
            this.defaultRequestBtnClickListener = defaultRequestBtnClickListener
        }

        // View lookup cache
        inner class ViewHolder {
            var price: TextView? = null
            var contentRequestBtn: TextView? = null
            var pledgePrice: TextView? = null
            var fromAddress: TextView? = null
            var toAddress: TextView? = null
            var requestsCount: TextView? = null
            var date: TextView? = null
            var time: TextView? = null
        }
    }
}