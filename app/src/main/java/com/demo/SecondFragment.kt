package com.demo

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.sax.EndElementListener
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.transition.*
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.materialstudies.reply.R
import kotlin.math.abs

/**
 * Author: ChenYouSheng
 * Date: 2022/7/21
 * Email: chenyousheng@lizhi.fm
 * Desc:
 */
class SecondFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_next, container, false)
    }

    private var mDownX: Float = 0f
    private var mStartX: Float = 0f
    private var change = false
    private var dragForce = 2.5f

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            /*activity?.supportFragmentManager?.beginTransaction()?.apply {
                addSharedElement(view.findViewById<ImageView>(R.id.ivLeft), "transitionA")
                replace(R.id.clContainer,FirstFragment())
                commitNowAllowingStateLoss()
            }*/
        }


        // 目标类必须设置元素共享的name
        ViewCompat.setTransitionName(view.findViewById(R.id.ivLeft), "transitionB")

        val rightIv = view.findViewById<ImageView>(R.id.ivRight)
        val leftIv = view.findViewById<ImageView>(R.id.ivLeft)

        leftIv.setOnClickListener {
            Toast.makeText(activity, "点击左边头像", Toast.LENGTH_SHORT).show()
        }


        view.findViewById<View>(R.id.clRootContainer).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.x
                    mStartX = rightIv.x
                    change = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.x - mDownX) / dragForce
                    mDownX = event.x
                    rightIv.apply {
                        if (dx > 0) {
                            if (x > mStartX && change) {
                                // 1.一开始先左滑,触发头像右移, 左滑过程中切换到右滑时,需要头像往回(左)滑
                                x -= dx
                            } else {
                                // 2.一开始右滑,那就头像右移,或者是1分支头像回正后右滑,头像继续右移
                                x += dx
                                change = false
                            }
                        } else {
                            if (x > mStartX && !change) {
                                // 4. 一开始右滑,触发头像右移, 右滑过程中切换到左滑时,需要头像往回(左)滑
                                x += dx
                            } else {
                                // 3. 一开始左滑, 头像需要右移动
                                x -= dx
                                change = true
                            }
                        }
                        var factor = (x - mStartX) / width
                        if (factor > 1) {
                            factor = 1f
                        }
                        view.findViewById<View>(R.id.llLeftTag)?.alpha = 1 - factor
                        view.findViewById<View>(R.id.llRightTag)?.alpha = 1 - factor
                        alpha = 1 - factor
                        Log.d("cys", "factor---->${factor}")
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    rightIv.x = mStartX

                    view.findViewById<View>(R.id.llLeftTag)?.alpha = 1f
                    view.findViewById<View>(R.id.llRightTag)?.alpha = 1f
                    rightIv.alpha = 1f
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setEnterSharedElementCallback(object : SharedElementCallback() {
//            override fun onSharedElementStart(
//                sharedElementNames: MutableList<String>?,
//                sharedElements: MutableList<View>?,
//                sharedElementSnapshots: MutableList<View>?
//            ) {
//                Log.d("cys", "setEnterSharedElementCallback-->onSharedElementStart")
//            }
//
//
//            override fun onSharedElementEnd(
//                sharedElementNames: MutableList<String>?,
//                sharedElements: MutableList<View>?,
//                sharedElementSnapshots: MutableList<View>?
//            ) {
//                Log.d("cys", "setEnterSharedElementCallback-->onSharedElementEnd")
//            }
//
//        })
//
//        setExitSharedElementCallback(object : SharedElementCallback() {
//            override fun onSharedElementStart(
//                sharedElementNames: MutableList<String>?,
//                sharedElements: MutableList<View>?,
//                sharedElementSnapshots: MutableList<View>?
//            ) {
//                view?.findViewById<ImageView>(R.id.ivRight)?.apply {
//                    Log.d("cys", "setExitSharedElementCallback-->onSharedElementStart")
//                }
//            }
//
//            override fun onSharedElementEnd(
//                sharedElementNames: MutableList<String>?,
//                sharedElements: MutableList<View>?,
//                sharedElementSnapshots: MutableList<View>?
//            ) {
//                view?.findViewById<ImageView>(R.id.ivRight)?.apply {
//                    Log.d("cys", "setExitSharedElementCallback-->onSharedElementEnd")
//                }
//            }
//        })

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()

            addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    view?.findViewById<ImageView>(R.id.ivRight)?.apply {
                        val startX = right
                        val endX = left
                        ObjectAnimator.ofFloat(this, "x", startX.toFloat(), endX.toFloat()).apply {
                            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
                            start()
                        }
                    }
                    view?.findViewById<View>(R.id.llLeftTag)?.alpha = 0f
                    view?.findViewById<View>(R.id.llRightTag)?.alpha = 0f
                }

                override fun onTransitionEnd(transition: Transition) {
                    view?.findViewById<View>(R.id.llLeftTag)?.animate()?.apply {
                        alpha(1f).setDuration(resources.getInteger(R.integer.reply_motion_duration_large).toLong()).start()
                    }
                    view?.findViewById<View>(R.id.llRightTag)?.animate()?.apply {
                        alpha(1f).setDuration(resources.getInteger(R.integer.reply_motion_duration_large).toLong()).start()
                    }
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }
            })

        }


//        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
//            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//        }

//        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
//            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//            addListener(object:  androidx.transition.Transition.TransitionListener {
//                override fun onTransitionStart(transition: androidx.transition.Transition) {
//                    Log.d("cys", "exitTransition-->onTransitionStart")
//                    view?.findViewById<ImageView>(R.id.ivRight)?.apply {
//                        val startX = left
//                        val endX = right
//                        ObjectAnimator.ofFloat(this, "x", startX.toFloat(), endX.toFloat()).apply {
//                            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//                            start()
//                        }
//                    }
//                }
//
//                override fun onTransitionEnd(transition: androidx.transition.Transition) {
//                    Log.d("cys", "exitTransition-->onTransitionEnd")
//                }
//
//                override fun onTransitionCancel(transition: androidx.transition.Transition) {
//                }
//
//                override fun onTransitionPause(transition: androidx.transition.Transition) {
//                }
//
//                override fun onTransitionResume(transition: androidx.transition.Transition) {
//                }
//            })
//        }

//        sharedElementEnterTransition = DetailsTransition().apply {
//            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//        }
    }

    class DetailsTransition : TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
        }
    }
}