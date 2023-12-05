package com.ft.ltd.takeyourpill.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.ft.ltd.takeyourpill.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.adapter.ColorAdapter
import com.ft.ltd.takeyourpill.adapter.ReminderAdapter
import com.ft.ltd.takeyourpill.databinding.FragmentEditBinding
import com.ft.ltd.takeyourpill.fragment.dialog.ReminderDialog
import com.ft.ltd.takeyourpill.utils.*
import com.ft.ltd.takeyourpill.model.PillColor
import com.ft.ltd.takeyourpill.utils.themeColor
import com.ft.ltd.takeyourpill.model.Reminder
import com.ft.ltd.takeyourpill.utils.Builders
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.viewmodel.EditViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class EditFragment : Fragment(R.layout.fragment_edit) {

    private val binding by viewBinding(FragmentEditBinding::bind)

    private val model: EditViewModel by viewModels()
    private val args: EditFragmentArgs by navArgs()

    private val colorAdapter = ColorAdapter()
    private val reminderAdapter = ReminderAdapter()

    private val isCreatingNewPill
        get() = args.pillId == -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isCreatingNewPill) {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.floatingActionButton)
                endView = binding.layoutEdit
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(com.google.android.material.R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(com.google.android.material.R.attr.colorSurface)
            }

            returnTransition = Slide().addTarget(R.id.layoutEdit)
        } else {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }

        if (isCreatingNewPill) {
            binding.textNewPill.text = getString(R.string.new_pill)
            if (!model.isPillInitialized) {
                model.pill = model.getNewEmptyPill()
            }
            model.firstNameEdit = false
            model.firstDescriptionEdit = false
            initViews()
        } else {
            binding.textNewPill.text = getString(R.string.edit_pill)
            postponeEnterTransition()
            if (!model.isPillInitialized) {
                model.getPillById(args.pillId).observe(viewLifecycleOwner) {
                    model.pill = it
                    initViews()
                    startPostponedEnterTransition()
                }
            } else {
                initViews()
                startPostponedEnterTransition()
            }
        }
    }

    /**
     * Called when imagepicker finishes
     */
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                onNewImagePicked(data?.data!!)
            }
            ImagePicker.RESULT_ERROR -> {
                showSnackbar(ImagePicker.getError(data))
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun initViews() {
        model.initFields()

        model.pillColors.observe(viewLifecycleOwner) {
            colorAdapter.submitList(it)
            setUiColor(model.pill.color)
        }
        model.reminders.observe(viewLifecycleOwner) { reminderAdapter.submitList(it) }

        binding.run {

            inputName.setText(model.pill.name)
            inputDescription.setText(model.pill.description)

            refreshImage()
            model.photoBitmap.observe(viewLifecycleOwner) {
                refreshImage()
            }

            recyclerReminders.adapter = reminderAdapter
            reminderAdapter.onReminderClicked { _, reminder -> showReminderDialog(reminder, true) }
            reminderAdapter.onReminderDelete { _, reminder -> model.removeReminder(reminder) }

            recyclerColor.adapter = colorAdapter
            recyclerColor.disableAnimations()

            colorAdapter.setOnColorClickedListener { _, pillColor ->
                model.setActivePillColor(
                    pillColor
                )
            }

            pillOptionsView.setOptions(model.pill.options)
            pillOptionsView.onChange {
                model.hasPillBeenEdited = true
            }

            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameLayout.showError(
                    if (model.onNameChanged(text)) getString(R.string.enter_field_name) else null
                )
            }
            inputDescription.doOnTextChanged { text, _, _, _ -> model.onDescriptionChanged(text) }
            buttonSave.onClick { onPillSave() }
            buttonAddReminder.onClick { showReminderDialog() }
            imageChooser.setOnImageClickListener { onPickImage() }
            imageChooser.setOnDeleteClickListener { onImageDelete() }

            scrollEdit.setOnScrollChangeListener { _, _, _, _, _ ->
                val offset = scrollEdit.scrollY
                when (buttonSave.isExtended) {
                    true -> if (offset > 0) buttonSave.shrink()
                    false -> if (offset == 0) buttonSave.extend()
                }
            }

        }

    }

    private fun refreshImage() {
        binding.imageChooser.setImageDrawable(model.pill.getPhotoDrawable(requireContext()))
        binding.imageChooser.setDeleteVisible(model.pill.isPhotoVisible)
    }

    private fun setUiColor(checkedColor: PillColor) {
        val color = checkedColor.getColor(requireContext())
        val colorStateList = ColorStateList.valueOf(color)
        binding.run {
            buttonSave.backgroundTintList = colorStateList
            listOf(inputName, inputDescription).forEach {
                it.highlightColor = color
            }
            listOf(inputNameLayout, inputDescriptionLayout).forEach {
                it.boxStrokeColor = color
                it.hintTextColor = colorStateList
            }

            buttonAddReminder.strokeColor = colorStateList
            buttonAddReminder.iconTint = colorStateList
            buttonAddReminder.rippleColor = colorStateList
            buttonAddReminder.setTextColor(color)

            pillOptionsView.setButtonTint(color)
        }
    }

    private fun onBackPressed() {
        if (model.hasPillBeenEdited) {
            Builders.getConfirmDialog(
                requireContext(),
                getString(R.string.confirm_exit_edit),
                getString(R.string.confirm_exit_edit_description),
                {
                    findNavController().popBackStack()
                    it.dismiss()
                }
            )
        } else {
            findNavController().popBackStack()
        }
    }

    private fun onImageDelete() =
        Builders.getConfirmDialog(
            requireContext(),
            getString(R.string.confirm_delete_photo),
            getString(R.string.confirm_delete_photo_description),
            {
                model.deleteImage()
                it.dismiss()
            }
        )

    private fun showReminderDialog(
        reminder: Reminder = Reminder.create(pillId = model.pill.id),
        editing: Boolean = false
    ) = ReminderDialog().apply {
        onConfirm { rem, edit -> onNewReminderConfirmed(rem, edit) }
        isEditing = editing
        this.reminder = reminder
        accentColor = model.pill.color
    }.show(childFragmentManager, Constants.TAG_REMINDER_DIALOG)


    private fun onNewReminderConfirmed(reminder: Reminder, editing: Boolean) {
        val reminderDialog =
            childFragmentManager.findFragmentByTag(Constants.TAG_REMINDER_DIALOG) as ReminderDialog

        val sameReminder = model.pill.reminders.find { reminder.hasSameTime(it) }
        // There is a reminder that has the same time
        if (sameReminder != null && ((editing && sameReminder != reminder) || !editing)) {
            reminderDialog.showError(getString(R.string.two_reminders_same_time))
            return
        }
        reminderDialog.dismiss()
        if (editing) model.editReminder(reminder)
        else model.addReminder(reminder)


    }

    private fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA
                )
            ) {
                ImagePicker.with(this)
                    .compress(Constants.IMAGE_MAX_SIZE)
                    .maxResultSize(Constants.IMAGE_MAX_WIDTH, Constants.IMAGE_MAX_HEIGHT)
                    .crop()
                    .start()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.storage_perm_rationale),
                    Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
                )
            }
        }else{
            if (EasyPermissions.hasPermissions(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            ) {
                ImagePicker.with(this)
                    .compress(Constants.IMAGE_MAX_SIZE)
                    .maxResultSize(Constants.IMAGE_MAX_WIDTH, Constants.IMAGE_MAX_HEIGHT)
                    .crop()
                    .start()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.storage_perm_rationale),
                    Constants.READ_EXTERNAL_STORAGE_PERMISSION_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun onNewImagePicked(uri: Uri) {
        model.setImage(uri, requireContext()).observe(viewLifecycleOwner) {
            if (!it) {
                showSnackbar(getString(R.string.error_opening_image))
            }
        }
    }

    private fun onPillSave() = binding.run {
        // Does Pill have a name?
        if (model.pill.name.isBlank()) {
            inputNameLayout.error = getString(R.string.enter_field_name)
            scrollEdit.smoothScrollTo(0, 0)
            return@run
        }

        // Does Pill have at least one reminder?
        if (model.pill.reminders.isEmpty()) {
            showSnackbar(getString(R.string.no_reminders_set))
            return@run
        }

        model.pill.options = pillOptionsView.getOptions()

        if (isCreatingNewPill) {
            model.addPill(model.pill, requireActivity().applicationContext)
            exitTransition = Slide().addTarget(R.id.layoutEdit)
        } else {
            model.updatePill(model.pill, requireActivity().applicationContext)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        }
        findNavController().popBackStack()
    }


    private fun showSnackbar(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
}