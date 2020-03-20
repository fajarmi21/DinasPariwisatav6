package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch

import com.polinema.android.kotlin.dinaspariwisatav6.R
import kotlinx.android.synthetic.main.fragment_super_detail_ud.view.*

/**
 * A simple [Fragment] subclass.
 */
class SuperDetailUDFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_super_detail_ud, container, false)
        val kode = arguments?.get("kode").toString().toInt()
        when(kode) {
            0 -> {
                v.txNameUD.text = "Upload"
                v.buttonUploadUD.text = "Upload"
            }
            1 -> {
                v.txNameUD.text = "Download"
                v.buttonUploadUD.text = "Download"
            }
        }
        return v
    }


}
