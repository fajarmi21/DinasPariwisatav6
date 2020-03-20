package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.net.toUri

import com.polinema.android.kotlin.dinaspariwisatav6.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_super_detail_upload.view.*
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SuperDetailUploadFragment : Fragment() {

    lateinit var mediaController: MediaController

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_super_detail_upload, container, false)
        mediaController = MediaController(this.context)
        mediaController.setAnchorView(view.vivDetail)

        val id = arguments?.get("id")
        val user = arguments?.get("user")
        val date = arguments?.get("date")
        val destine = arguments?.get("destine")
        val i1 = arguments?.get("income1").toString()
        val i2 = arguments?.get("income2").toString()
        val im1 = arguments?.get("im1").toString()
        val im2 = arguments?.get("im2").toString()
        val im3 = arguments?.get("im3").toString()
        val im4 = arguments?.get("im4").toString()
        val viv = arguments?.get("viv").toString()
        Log.e("im1", im1)
        view.idDetailU.text = "(${id})"
        view.userDetail.setText("$user")
        view.dateDetail.setText("$date")
        view.destineDetail.setText("$destine")
        view.txincomeDDetailD.setText(i1)
        view.txincomeLDetailD.setText(i2)

        Picasso.get().load(im1).into(view.imvDet1)
        Picasso.get().load(im2).into(view.imvDet2)
        Picasso.get().load(im3).into(view.imvDet3)
        Picasso.get().load(im4).into(view.imvDet4)

        view.vivDetail.setMediaController(mediaController)
        view.vivDetail.setVideoURI(viv.toUri())
        view.vivDetail.start()

        return view
    }
}
