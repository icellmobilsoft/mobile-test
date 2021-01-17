package com.mstoica.dogoapp.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mstoica.dogoapp.R
import org.jetbrains.anko.support.v4.find

class DogDialogFragment: DialogFragment() {

    private val args: DogDialogFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = view ?: inflater.inflate(R.layout.dialog_fragment_dog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivDog = find<ImageView>(R.id.iv_dog)

        Glide.with(requireContext())
            .load(args.dog.imageUrl)
            .apply(RequestOptions().centerCrop())
            .into(ivDog)

        val ivBack = find<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener {
            dismiss()
        }
    }
}