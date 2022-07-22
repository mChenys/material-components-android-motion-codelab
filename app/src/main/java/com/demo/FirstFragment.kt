package com.demo

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import com.materialstudies.reply.R

/**
 * Author: ChenYouSheng
 * Date: 2022/7/21
 * Email: chenyousheng@lizhi.fm
 * Desc:
 */
class FirstFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.ivStart)
        // 设置元素共享的name
         ViewCompat.setTransitionName(imageView,"transitionA")

        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                addSharedElement(imageView, "transitionB") // 这里为即将跳转的 Fragment 中的共享 View 的 Transition Name
                replace(R.id.clContainer, SecondFragment())
                addToBackStack(null)
                commitAllowingStateLoss()
            }
        }
    }


}