package com.lodz.android.radarnydemo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.lodz.android.corekt.anko.dp2px
import com.lodz.android.corekt.anko.getColorCompat
import com.lodz.android.corekt.anko.sp2px
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.pandora.base.activity.BaseActivity
import com.lodz.android.pandora.utils.viewbinding.bindingLayout
import com.lodz.android.radarny.RadarnyBean
import com.lodz.android.radarnydemo.databinding.ActivityMainBinding
import java.util.ArrayList
import java.util.Random

class MainActivity : BaseActivity() {

    private val mBinding: ActivityMainBinding by bindingLayout(ActivityMainBinding::inflate)

    override fun getViewBindingLayout(): View = mBinding.root

    /** 指标数 */
    private var mCount = 0

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        getTitleBarLayout().setTitleName(R.string.app_name)
        getTitleBarLayout().needBackButton(false)
        getTitleBarLayout().setBackgroundColor(getColorCompat(R.color.color_a9e0e2))
        initRadarnyView()
    }

    private fun initRadarnyView() {
        mBinding.defRadarnyView
            .setMaxValue(100f)
            .setAnimDuration(400)
            .setFrameColor(ContextCompat.getColor(getContext(), R.color.color_d04741))
            .setFrameRound(false)
            .setFrameStrokeWidth(5)
            .setInnerFrameColor(ContextCompat.getColor(getContext(), R.color.color_f0f0f0))
            .setInnerFrameStrokeWidth(3)
            .setInnerFramePercentage(0.3f)
            .setInnerLineColor(ContextCompat.getColor(getContext(), R.color.color_f0f0f0))
            .setInnerLineStrokeWidth(3)
            .setShowLine(true)
            .setTextColor(ContextCompat.getColor(getContext(), R.color.color_d04741))
            .setTextPercentage(1.2f)
            .setTextSize(sp2px(11))
            .setValueColor(ContextCompat.getColor(getContext(), R.color.color_7fd04741))
            .setValueStrokeWidth(5)
            .setValuePaintStyle(Paint.Style.FILL)
            .setShowSrc(true)
            .setSrcResId(R.drawable.ic_pokeball)
            .setSrcWidth(dp2px(25))
            .setSrcHeight(dp2px(25))
            .setSrcBgColor(Color.WHITE)
            .setSrcBgPercentage(0.7f)
    }

    override fun setListeners() {
        super.setListeners()
        mBinding.addBtn.setOnClickListener {
            mCount += 1
            mBinding.countEdit.setText(mCount.toString())
        }

        mBinding.deleteBtn.setOnClickListener {
            mCount -= 1
            mBinding.countEdit.setText(mCount.toString())
        }

        mBinding.updateBtn.setOnClickListener {
            val text = mBinding.countEdit.text
            if (text.isEmpty()) {
                toastShort(R.string.main_input_hint)
                return@setOnClickListener
            }
            var num = text.toString().toInt()
            if (num < 3) {
                num = 3
                toastShort(R.string.main_correct_count)
            }
            if (num > 100) {
                num = 100
                toastShort(R.string.main_correct_count)
            }
            mCount = num
            mBinding.countEdit.setText(mCount.toString())
            mBinding.countEdit.setSelection(mCount.toString().length)
            updateRadarny(mCount)
        }
    }

    private fun createData(count: Int): ArrayList<RadarnyBean> {
        val list = ArrayList<RadarnyBean>()
        for (i in 0 until count) {
            list.add(RadarnyBean("label$i", Random().nextInt(101).toFloat()))
        }
        return list
    }

    override fun initData() {
        super.initData()
        mCount = mBinding.countEdit.text.toString().toInt()
        updateRadarny(mCount)
        showStatusCompleted()
    }

    private fun updateRadarny(count: Int) {
        mBinding.radarnyView.setData(createData(count)).build()
        mBinding.defRadarnyView.setData(createData(count)).build()
    }
}