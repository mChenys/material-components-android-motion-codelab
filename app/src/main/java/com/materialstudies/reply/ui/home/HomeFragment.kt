/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.reply.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.transition.MaterialElevationScale
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.databinding.FragmentHomeBinding
import com.materialstudies.reply.ui.MainActivity
import com.materialstudies.reply.ui.MenuBottomSheetDialogFragment
import com.materialstudies.reply.ui.nav.NavigationModel

/**
 * A [Fragment] that displays a list of emails.
 */
class HomeFragment : Fragment(), EmailAdapter.EmailAdapterListener {

    private val args: HomeFragmentArgs by navArgs()

    private lateinit var binding: FragmentHomeBinding

    private val emailAdapter = EmailAdapter(this)

    // An on back pressed callback that handles replacing any non-Inbox HomeFragment with inbox
    // on back pressed.
    private val nonInboxOnBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            NavigationModel.setNavigationMenuItemChecked(NavigationModel.INBOX_ID)
            (requireActivity() as MainActivity)
                .navigateToHome(R.string.navigation_inbox, Mailbox.INBOX);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Set up MaterialFadeThrough enterTransition.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Set up postponed enter transition.
        /**
         * 延迟EnterTransition和启动PostPonedEnterTransition。如果调用了延迟EnterTransition，则任何要运行的输入转换都将被保留，
         * 直到调用了对startPostponedEnterTransition的结束调用。
         * 当 Fragment 使用 postponeEnterTransition() 方法实现延迟加载的时候，所期望的效果是添加了 Fragment 的容器，在 Fragment 调用 startPostponedEnterTransition() 之前，
         * 不运行任何进入界面的动画或者之前已经在队列里的退出动画 (比如 replace() 操作)。另外一个预期的效果是当容器推迟加载的时候，Fragment 不会进入 RESUMED 状态。
         */
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        // Only enable the on back callback if this home fragment is a mailbox other than Inbox.
        // This is to make sure we always navigate back to Inbox before exiting the app.
        nonInboxOnBackCallback.isEnabled = args.mailbox != Mailbox.INBOX
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            nonInboxOnBackCallback
        )

        binding.recyclerView.apply {
            val itemTouchHelper = ItemTouchHelper(ReboundingSwipeActionCallback())
            itemTouchHelper.attachToRecyclerView(this)
            adapter = emailAdapter
        }
        binding.recyclerView.adapter = emailAdapter

        EmailStore.getEmails(args.mailbox).observe(viewLifecycleOwner) {
            emailAdapter.submitList(it)
        }
    }

    override fun onEmailClicked(cardView: View, email: Email) {
        // TODO: Set up MaterialElevationScale transition as exit and reenter transitions.
        // 这种方式是没有转场动画的
//        val directions = HomeFragmentDirections.actionHomeFragmentToEmailFragment(email.id)
//        findNavController().navigate(directions)

        // Fragment->Fragment的动画
        // 1.当前Fragment的退出动画,缩小退出
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        // 2.当前Fragment重新进入的动画,例如栈顶的Fragment按返回键触发,放大进入
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }

        // 3.设置共享元素的名字和"开始"View的对应关系,这里的View对于列表item的@+id/card_view,
        // 同时需要在"结束"View@+id/email_card_view中也添加 android:transitionName="@string/email_card_detail_transition_name"
        val emailCardDetailTransitionName = getString(R.string.email_card_detail_transition_name)
        // 组成pair对象后传入FragmentNavigator.Extras
        val extras = FragmentNavigatorExtras(cardView to emailCardDetailTransitionName)

        // 4.然后需要给RecycleView添加属性:android:transitionGroup="true", 让动画应用到整个列表

        // 5.创建导航对象
        val directions = HomeFragmentDirections.actionHomeFragmentToEmailFragment(email.id)

        // 6.开始导航
        findNavController().navigate(directions, extras)
    }

    override fun onEmailLongPressed(email: Email): Boolean {
        MenuBottomSheetDialogFragment
          .newInstance(R.menu.email_bottom_sheet_menu)
          .show(parentFragmentManager, null)

        return true
    }

    override fun onEmailStarChanged(email: Email, newValue: Boolean) {
        EmailStore.update(email.id) { isStarred = newValue }
    }

    override fun onEmailArchived(email: Email) {
        EmailStore.delete(email.id)
    }
}
