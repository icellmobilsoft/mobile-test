package com.mstoica.dogoapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.mstoica.dogoapp.R
import com.mstoica.dogoapp.commons.handleClearFocusOnTouchOutside
import com.mstoica.dogoapp.commons.utils.ObservableScrollListener
import com.mstoica.dogoapp.commons.utils.ScreenHelper
import com.mstoica.dogoapp.databinding.FragmentSearchBinding
import com.mstoica.dogoapp.di.DaggerViewModelFactory
import com.mstoica.dogoapp.di.Injectable
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.view.adapter.DogImageAdapter
import com.mstoica.dogoapp.viewmodel.FavouritesViewModel
import com.mstoica.dogoapp.viewmodel.SearchViewModel
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class FragmentSearch: Fragment(), Injectable, DogImageAdapter.IDogoListener, TextWatcher{

    @Inject
    lateinit var providerFactory: DaggerViewModelFactory

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val imageAdapter: DogImageAdapter by lazy {
        DogImageAdapter(requireContext())
    }

    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory).get(SearchViewModel::class.java)
    }

    private val favouritesViewModel: FavouritesViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory).get(FavouritesViewModel::class.java)
    }

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

        setupSearchInputField()
        setupActionButtons()
        setupRecyclerView()
        subscribeObservers()
    }

    private fun setupActionButtons() {
        binding.btnLoadMore.setOnClickListener {
            searchViewModel.loadNewImages()
        }
    }

    private fun subscribeObservers() {
        searchViewModel.searchResultLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                NetworkResource.ResourceStatus.ERROR -> {
                    longToast(getString(R.string.general_error_message_suggestion))
                }
                NetworkResource.ResourceStatus.LOADING -> {
                    //toast("loading...")
                }
                NetworkResource.ResourceStatus.SUCCESS -> {
                    setAdapterItems(it.data!!)
                }
            }
        })
        favouritesViewModel.likeModificationLiveData.observe(
            viewLifecycleOwner,
            Observer { currentState ->
                if (currentState != null) {
                    val refDog = currentState.first
                    imageAdapter.notifyItemChanged(refDog)
                }
            })
    }

    private fun setAdapterItems(data: List<Dog>) {
        imageAdapter.items = mutableListOf<Dog>().apply {
            addAll(data)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(
            requireContext(),
            getNumOfColumns()
        )
        binding.rvSearchResults.layoutManager = layoutManager
        binding.rvSearchResults.adapter = imageAdapter.also {
            it.listener = this
        }
        val scrollListener = ObservableScrollListener(layoutManager)
        binding.rvSearchResults.addOnScrollListener(scrollListener)

        scrollListener.reachedBottomLiveData.observe(viewLifecycleOwner, Observer { reachedBottom ->
            TransitionManager.beginDelayedTransition(binding.root, Fade())
            binding.btnLoadMore.visibility = if (reachedBottom) VISIBLE else GONE
        })
    }

    private fun getNumOfColumns(): Int =
        if (ScreenHelper.isTablet(requireContext())) COLUMN_SIZE_TABLET else COLUMN_SIZE_DEFAULT

    private fun setupSearchInputField() {
        binding.searchBar.etSearch.addTextChangedListener(this)
    }

    override fun onItemClicked(dog: Dog, position: Int) {
        findNavController().navigate(
            FragmentSearchDirections.actionAppBarSearchToDogDialogFragment(dog)
        )
    }

    override fun onLikeChanged(dog: Dog, like: Boolean) {
        favouritesViewModel.modifyLike(dog, like)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // no implementation
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        searchViewModel.makeSearch(p0.toString())
    }

    override fun afterTextChanged(p0: Editable?) {
        // no implementation
    }

    companion object {
        const val COLUMN_SIZE_DEFAULT = 3
        const val COLUMN_SIZE_TABLET = 5
    }
}