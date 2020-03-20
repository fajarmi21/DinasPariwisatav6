package com.polinema.android.kotlin.dinaspariwisatav6.Super

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.okdroid.checkablechipview.CheckableChipView
import com.google.android.material.appbar.AppBarLayout
import com.nex3z.togglebuttongroup.ToggleButtonGroup
import com.nex3z.togglebuttongroup.button.CircularToggle
import com.nex3z.togglebuttongroup.button.ToggleButton
import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.polinema.android.kotlin.dinaspariwisatav6.Super.Button.CustomCompoundButton
import kotlinx.android.synthetic.main.activity_super_filter.*

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
    }

    override fun onStart() {
        super.onStart()
        check()
        spin()
    }

    @SuppressLint("ResourceType")
    private fun radio() {
        val t = 2.8
        val tg = findViewById(R.id.group_single_radiobutton) as ToggleButtonGroup
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
}