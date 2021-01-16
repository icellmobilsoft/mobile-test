package com.mstoica.dogoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mstoica.dogoapp.commons.handleClearFocusOnTouchOutside
import com.mstoica.dogoapp.databinding.FragmentSearchBinding
import com.mstoica.dogoapp.di.Injectable

class FragmentSearch: Fragment(), Injectable {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleClearFocusOnTouchOutside(binding.touchInterceptor)
    }
}