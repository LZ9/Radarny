package com.lodz.android.radarnydemo

import android.os.Bundle
import android.view.View
import com.lodz.android.corekt.anko.getColorCompat
import com.lodz.android.corekt.anko.toastShort
import com.lodz.android.pandora.base.activity.BaseActivity
import com.lodz.android.pandora.utils.viewbinding.bindingLayout
import com.lodz.android.radarnydemo.databinding.ActivityMainBinding

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
            mCount = num
            mBinding.countEdit.setText(mCount.toString())
            toastShort(mCount.toString())
        }
    }

    override fun initData() {
        super.initData()
        mCount = mBinding.countEdit.text.toString().toInt()
        showStatusCompleted()
    }

}