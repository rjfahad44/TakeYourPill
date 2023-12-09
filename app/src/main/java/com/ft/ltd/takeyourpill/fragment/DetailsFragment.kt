package com.ft.ltd.takeyourpill.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.ft.ltd.takeyourpill.utils.Constants
import com.ft.ltd.takeyourpill.utils.DayOfYear
import com.ft.ltd.takeyourpill.utils.getDateTimeString
import com.ft.ltd.takeyourpill.utils.getTimeString
import com.ft.ltd.takeyourpill.utils.navigateSafe
import com.ft.ltd.takeyourpill.utils.themeColor
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import com.ft.ltd.takeyourpill.R
import com.ft.ltd.takeyourpill.adapter.ReminderAdapter
import com.ft.ltd.takeyourpill.databinding.FragmentDetailsBinding
import com.ft.ltd.takeyourpill.fragment.dialog.DeleteDialog
import com.ft.ltd.takeyourpill.utils.*
import com.ft.ltd.takeyourpill.reminder.NotificationManager
import com.ft.ltd.takeyourpill.reminder.ReminderManager
import com.ft.ltd.takeyourpill.reminder.ReminderUtil
import com.ft.ltd.takeyourpill.utils.disableAnimations
import com.ft.ltd.takeyourpill.utils.onClick
import com.ft.ltd.takeyourpill.utils.setBackgroundColorShaped
import com.ft.ltd.takeyourpill.viewmodel.DetailsViewModel
import com.ft.ltd.takeyourpill.viewmodel.MainViewModel
import java.util.*


@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val model: DetailsViewModel by viewModels()
    private val mainModel: MainViewModel by activityViewModels()

    private val args: DetailsFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentDetailsBinding::bind)
    private val reminderAdapter = ReminderAdapter(showDelete = false, showRipple = false)

    private var launchedFromNotification = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
        }

        postponeEnterTransition()

        launchedFromNotification = requireArguments().getBoolean(Constants.INTENT_EXTRA_LAUNCHED_FROM_NOTIFICATION, false)

        var pillId = requireArguments().getLong(Constants.INTENT_EXTRA_PILL_ID, -1L)
        Log.d("PILL_ID", "pill id : $pillId")
        if (pillId == -1L) pillId = args.pillId

        model.getPillById(pillId).observe(viewLifecycleOwner) { pill ->
            pill?.let {
                model.pill = it
                initViews()
                model.loadedData()
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        binding.run {

            textPillName.text = model.pill.name
            viewPillColor.setBackgroundColorShaped(model.pill.colorResource(requireContext()))

            textPillDescription.apply {
                text = model.pill.description
                isVisible = model.pill.isDescriptionVisible
            }

            recyclerReminders.adapter = reminderAdapter
            recyclerReminders.disableAnimations()

            cardPhoto.isVisible = model.pill.isPhotoVisible
            val photoDrawable = model.pill.getPhotoDrawable(requireContext())
            imagePillPhoto.setImageDrawable(photoDrawable)
            imageViewFullScreen.setImageDrawable(photoDrawable)

            val accent = model.pill.colorResource(requireContext())
            val accentList = ColorStateList.valueOf(accent)
            listOf(buttonDelete, buttonHistory).forEach {
                it.setTextColor(accent)
                it.rippleColor = accentList
            }
            buttonEdit.backgroundTintList = accentList
            buttonTaken.backgroundTintList = accentList

            buttonEdit.onClick { navigateToEdit() }
            buttonHistory.onClick { navigateToHistory() }
            buttonDelete.onClick { navigateToDelete() }

            // If last reminder date is null, then this is the first reminder
            model.pill.lastReminderDate?.let { lastDate ->
                // Only add next cycle if this is the first reminder today
                if (lastDate.DayOfYear != Calendar.getInstance().DayOfYear) {
                    model.pill.options.nextCycleIteration()
                }
            }

            with(model.pill.options) {
                when {
                    isFinite() -> {
                        intakeDaysActive.isVisible = true
                        if (isInactive()) {
                            infoDayLimit.text = getString(R.string.inactive)
                        } else {
                            infoDayLimit.text = getString(
                                R.string.day_limit_format,
                                todayCycle,
                                daysActive
                            )
                        }
                    }
                    isCycle() -> {
                        intakeDaysActive.isVisible = true
                        intakeDaysInactive.isVisible = true
                        if (isInactive()) {
                            infoDayLimit.text = daysActive.toString()
                            infoResumeAfter.text = getString(
                                R.string.resume_after_format,
                                todayCycle - daysActive,
                                daysInactive
                            )
                        } else {
                            infoDayLimit.text = getString(
                                R.string.day_limit_format,
                                todayCycle,
                                daysActive
                            )
                            infoResumeAfter.text = daysInactive.toString()
                        }
                    }
                }
            }

            model.getLastReminded(model.pill.id).observe(viewLifecycleOwner) { history ->
                history?.let {
                    intakeLastReminded.isVisible = true
                    infoLastReminded.text = history.reminded.time.getDateTimeString()
                    textIntakeOptions.isVisible = true
                } ?: run {
                    intakeLastReminded.isVisible = false
                    textIntakeOptions.isVisible = intakeDaysActive.isVisible
                }
                model.loadedData()
            }

            cardPhoto.onClick {
                imageFullscreen.isVisible = true
            }

            imageFullscreen.onClick {
                imageFullscreen.isVisible = false
            }


            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                if (imageFullscreen.isVisible) {
                    imageFullscreen.isVisible = false
                } else {
                    findNavController().popBackStack()
                }
            }
        }

        model.setReminders(model.pill.reminders)

        model.reminders.observe(viewLifecycleOwner) { data ->
            reminderAdapter.submitList(data) {
                model.loadedData()
            }
        }

        model.getLatestHistory(!launchedFromNotification)
            .observe(viewLifecycleOwner) { history ->
                if (history != null) {
                    showConfirmLayout(true)
                    binding.textQuestionTake.text = binding.root.context.getString(
                        R.string.pill_taken_question,
                        history.amount,
                        history.reminded.time.getTimeString(requireContext())
                    )
                    binding.buttonTaken.onClick {
                        model.confirmPill(requireContext(), history)
                            .observe(viewLifecycleOwner) {
                                when (it) {
                                    true -> showConfirmLayout(false)
                                    false -> showMessage(getString(R.string.error))
                                }
                            }
                    }
                } else {
                    showConfirmLayout(false)
                }
                model.loadedData()
            }

        model.loadedData.observe(viewLifecycleOwner) {
            if (it == 4) { // We observe on 3 things + init layout, wait for all of them load :D
                view?.doOnPreDraw { startPostponedEnterTransition() }
                model.finishedLoading()
            }
        }
    }

    private fun showConfirmLayout(visible: Boolean) {
        if (binding.layoutConfirm.isVisible == visible) return

        val duration = resources.getInteger(android.R.integer.config_shortAnimTime)

        val transition = Slide(Gravity.TOP).apply {
            setDuration(duration.toLong())
            addTarget(binding.layoutConfirm)
        }

        TransitionManager.beginDelayedTransition(binding.detailsParent, transition)
        binding.layoutConfirm.isVisible = visible
    }

    private fun navigateToEdit() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        findNavController().navigateSafe(
            DetailsFragmentDirections.actionDetailsFragmentToEditFragment(model.pill.id),
            R.id.details
        )
    }

    private fun navigateToHistory() =
        findNavController().navigateSafe(
            DetailsFragmentDirections.actionDetailsToFragmentHistoryView(model.pill.id),
            R.id.details
        )

    private fun navigateToDelete() {
        DeleteDialog().apply {
            setUserListener { what ->
                when (what) {
                    true -> model.deletePillWithHistory(model.pill)
                    false -> model.deletePill(model.pill.pillEntity)
                }
                exitOnDelete()
            }
        }.show(childFragmentManager, "confirm_delete")
    }


    private fun exitOnDelete() {
        NotificationManager.removeNotificationChannel(
            requireContext(),
            model.pill.id.toString()
        )

        model.pill.reminders.forEach {
            NotificationManager.cancelNotification(requireContext(), it.id)
            val alarmAgain = ReminderUtil.getAlarmAgainIntent(requireContext(), it.id, 0, 0)
            val alarm = ReminderUtil.getAlarmIntent(requireContext(), it.id)
            alarmAgain.cancel()
            alarm.cancel()
            ReminderManager.getAlarmManager(requireContext()).cancel(alarm)
            ReminderManager.getAlarmManager(requireContext()).cancel(alarmAgain)
        }

        exitTransition = Slide().apply {
            addTarget(R.id.detailsView)
        }
        sharedElementEnterTransition = null
        findNavController().navigateUp()
    }

    private fun showMessage(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()

    override fun onResume() {
        super.onResume()
        //mainModel.adMobAdsManager.showInterstitialAd(requireActivity())
    }
}