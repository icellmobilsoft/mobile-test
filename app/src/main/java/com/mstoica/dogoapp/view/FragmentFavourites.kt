package com.mstoica.dogoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.commons.utils.ScreenHelper
import com.mstoica.dogoapp.databinding.FragmentFavouritesBinding
import com.mstoica.dogoapp.di.DaggerViewModelFactory
import com.mstoica.dogoapp.di.Injectable
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.view.adapter.DogImageAdapter
import com.mstoica.dogoapp.viewmodel.FavouritesViewModel
import org.jetbrains.anko.support.v4.longToast
import javax.inject.Inject

class FragmentFavourites: Fragment(), Injectable, DogImageAdapter.IDogoListener {

    @Inject
    lateinit var providerFactory: DaggerViewModelFactory

    private var _binding: FragmentFavouritesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val homeNavController: NavController by lazy {
        Navigation.findNavController(requireActivity(), R.id.home_nav_host_fragment)
    }

    private val imageAdapter: DogImageAdapter by lazy {
        DogImageAdapter(requireContext())
    }

    private val viewModel: FavouritesViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory).get(FavouritesViewModel::class.java)
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

        setupHeader()
        setupRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.favouritesLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                NetworkResource.ResourceStatus.ERROR -> {
                    longToast(getString(R.string.general_error_message_suggestion))
                    binding.layoutSwipeRefresh.isRefreshing = false
                }
                NetworkResource.ResourceStatus.LOADING -> {
                    binding.layoutSwipeRefresh.isRefreshing = true
                }
                NetworkResource.ResourceStatus.SUCCESS -> {
                    setAdapterItems(it.data!!)
                    binding.layoutSwipeRefresh.isRefreshing = false
                }
            }
        })

        viewModel.likeModificationLiveData.observe(viewLifecycleOwner, Observer { currentState ->
            if (currentState != null) {
                val refDog = currentState.first
                val likeModState = currentState.second

                if (likeModState == FavouritesViewModel.LikeState.UNLIKED) {
                    imageAdapter.removeDog(refDog)
                } else if (likeModState == FavouritesViewModel.LikeState.ERROR) {
                    longToast(getString(R.string.general_error_message_suggestion))
                }
            }
        })
    }

    private fun setAdapterItems(data: List<Dog>) {
        imageAdapter.items = mutableListOf<Dog>().apply {
            addAll(data)
        }
    }

    private fun setupRecyclerView() {
        setupRefreshLayout()
        binding.rvFavourites.layoutManager = GridLayoutManager(requireContext(), getNumOfColumns())
        binding.rvFavourites.adapter = imageAdapter.also {
            it.listener = this
        }
    }

    private fun setupRefreshLayout() = with(binding.layoutSwipeRefresh) {
        setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorOnPrimary
            )
        )
        setOnRefreshListener {
            viewModel.loadFavourites()
        }
    }

    private fun getNumOfColumns(): Int =
        if (ScreenHelper.isTablet(requireContext())) COLUMN_SIZE_TABLET else COLUMN_SIZE_DEFAULT

    private fun setupHeader() = with(binding.header) {
        tvHeader.text = getString(R.string.favourites_label)
        ivNavigateUp.setOnClickListener {
            homeNavController.navigateUp()
        }
    }

    override fun onItemClicked(dog: Dog, position: Int) {
        findNavController().navigate(
            FragmentFavouritesDirections.actionFragmentFavouritesToDogDialogFragment(dog)
        )
    }

    override fun onLikeChanged(dog: Dog, like: Boolean) {
        viewModel.modifyLike(dog, like)
    }

    companion object {
        const val COLUMN_SIZE_DEFAULT = 3
        const val COLUMN_SIZE_TABLET = 5
    }
}