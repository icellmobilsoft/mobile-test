package com.mstoica.dogoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.navigation.Navigation
import com.mstoica.dogoapp.databinding.FragmentWelcomeHomeBinding

class FragmentWelcomeHome: Fragment() {

    private var _binding: FragmentWelcomeHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionButtons()
    }

    private fun setupActionButtons() {
        binding.ivFavourites.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                FragmentWelcomeHomeDirections.actionFragmentWelcomeHomeToFragmentFavourites()
            )
        )

        binding.card.ivLike.setOnClickListener {

        }

        binding.btnNext.setOnClickListener {

        }
    }
}