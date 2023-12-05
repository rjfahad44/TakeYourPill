package com.ft.ltd.takeyourpill.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ft.ltd.takeyourpill.databinding.DialogDeleteBinding
import com.ft.ltd.takeyourpill.utils.onClick

class DeleteDialog : RoundedDialogFragment() {
    private lateinit var binding: DialogDeleteBinding

    var listener: (Boolean) -> Unit = {}

    fun setUserListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            buttonPillOnly.onClick { listener(false) }
            buttonPillHistory.onClick { listener(true) }
        }
    }
}
