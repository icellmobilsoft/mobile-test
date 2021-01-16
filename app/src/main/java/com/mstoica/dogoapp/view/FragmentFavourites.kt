package com.mstoica.dogoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.databinding.FragmentFavouritesBinding
import com.mstoica.dogoapp.di.Injectable

class FragmentFavourites: Fragment(), Injectable {

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val homeNavController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.home_nav_host_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.header.tvHeader.text = getString(R.string.favourites_label)

        binding.header.ivNavigateUp.setOnClickListener {
            homeNavController.navigateUp()
        }
    }

}