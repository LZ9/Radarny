package com.lodz.android.radarnydemo

import android.os.Bundle
import android.view.View
import com.lodz.android.corekt.anko.getColorCompat
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
            mBinding.radarnyView.setData(createData(mCount)).build()
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
        mBinding.radarnyView.setData(createData(mCount)).build()
        showStatusCompleted()
    }

}