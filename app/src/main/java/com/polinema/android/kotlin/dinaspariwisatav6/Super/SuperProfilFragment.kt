package com.polinema.android.kotlin.dinaspariwisatav6.Super


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.polinema.android.kotlin.dinaspariwisatav6.R

/**
 * A simple [Fragment] subclass.
 */
class SuperProfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_super_profil, container, false)
    }


}
