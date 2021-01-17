package com.mstoica.dogoapp.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.databinding.FragmentWelcomeHomeBinding
import com.mstoica.dogoapp.di.DaggerViewModelFactory
import com.mstoica.dogoapp.di.Injectable
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.network.ImageOptions
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.network.SessionManager
import com.mstoica.dogoapp.viewmodel.FavouritesViewModel
import com.mstoica.dogoapp.viewmodel.HomeViewModel
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject


class FragmentWelcomeHome: Fragment(), Injectable {

    @Inject
    lateinit var providerFactory: DaggerViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory).get(HomeViewModel::class.java)
    }

    private val favouritesViewModel: FavouritesViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory).get(FavouritesViewModel::class.java)
    }

    private var _binding: FragmentWelcomeHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val imageRequestOptions = RequestOptions().centerCrop()

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

        initUI()
        setupActionButtons()
        homeViewModel.update()
        subscribeObservers()
    }

    private fun initUI() {
        binding.tvUserInfo.text = sessionManager.userName
    }

    private fun subscribeObservers() {
        homeViewModel.randomDogLiveData.observe(viewLifecycleOwner, Observer {
            with(binding) {
                when (it.status) {
                    NetworkResource.ResourceStatus.ERROR -> {
                        longToast(getString(R.string.general_error_message_suggestion))
                        btnNext.isEnabled = true
                        card.root.visibility = VISIBLE
                        shimmer.visibility = INVISIBLE
                        card.ivLike.isEnabled = false
                        setupInfoCard(it.data)
                    }
                    NetworkResource.ResourceStatus.LOADING -> {
                        btnNext.isEnabled = false
                        card.ivLike.isEnabled = false
                        card.root.visibility = INVISIBLE
                        shimmer.visibility = VISIBLE
                    }
                    NetworkResource.ResourceStatus.SUCCESS -> {
                        btnNext.isEnabled = true
                        card.root.visibility = VISIBLE
                        shimmer.visibility = INVISIBLE
                        card.ivLike.isEnabled = it.data != null
                        setupInfoCard(it.data)
                    }
                }
            }
        })

        favouritesViewModel.likeModificationLiveData.observe(viewLifecycleOwner, Observer { currentState ->
            if (currentState != null) {
                val refDog = currentState.first
                val likeModState = currentState.second
                val currentDog = homeViewModel.currentDog!!

                if (currentDog == refDog) {
                    when (likeModState) {
                        FavouritesViewModel.LikeState.LIKED -> updateLikeButtonState(true)
                        FavouritesViewModel.LikeState.UNLIKED -> updateLikeButtonState(false)
                        FavouritesViewModel.LikeState.ERROR ->
                            longToast(getString(R.string.general_error_message_suggestion))
                    }
                } else {
                    updateLikeButtonState(currentDog.liked)
                }
            }
        })
    }

    private fun updateLikeButtonState(liked: Boolean) {
        val resource = if(liked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        val colorRes = if(liked) R.color.materialRed700 else R.color.materialGrey800
        val tint = ContextCompat.getColor(requireContext(), colorRes)

        binding.card.ivLike.setImageResource(resource)
        binding.card.ivLike.imageTintList = ColorStateList.valueOf(tint)
    }

    private fun setupInfoCard(responseData: Dog?) = with(binding.card) {
        if (responseData != null) {
            tvDogBreed.text = responseData.breedInfo ?: getString(R.string.no_information)
            tvDogInfo.text = responseData.temperament ?: getString(R.string.no_information)
        } else {
            tvDogBreed.text = getString(R.string.field_error_text)
            tvDogInfo.text = getString(R.string.field_error_text)
        }

        Glide.with(this@FragmentWelcomeHome)
            .load(responseData?.imageUrl)
            .apply(imageRequestOptions.override(ImageOptions.MED_IMAGE_SIZE))
            .into(ivDog)

        updateLikeButtonState(homeViewModel.currentDog?.liked == true)
    }

    private fun setupActionButtons() {
        binding.ivFavourites.setOnClickListener(
            Navigation.createNavigateOnClickListener(
                FragmentWelcomeHomeDirections.actionFragmentWelcomeHomeToFragmentFavourites()
            )
        )

        binding.card.ivLike.setOnClickListener {
            val currentDog = homeViewModel.currentDog!!
            val currentLike = currentDog.favId != null
            favouritesViewModel.modifyLike(currentDog, currentLike.not())
        }

        binding.btnNext.setOnClickListener {
            homeViewModel.getNewRandomDog()
        }
    }
}