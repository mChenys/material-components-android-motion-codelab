package com.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.materialstudies.reply.R

/**
 * Author: ChenYouSheng
 * Date: 2022/7/21
 * Email: chenyousheng@lizhi.fm
 * Desc: 元素共享动画处理
 */
class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.clContainer,FirstFragment())
            commitNowAllowingStateLoss()
        }
    }


}