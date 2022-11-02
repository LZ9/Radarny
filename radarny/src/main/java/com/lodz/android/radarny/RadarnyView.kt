package com.lodz.android.radarny

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * 雷达维度图
 * @author zhouL
 * @date 2022/11/1
 */
open class RadarnyView : View {

    companion object{
        /** 默认边框线宽度 */
        const val DEF_FRAME_STROKE_WIDTH = 5
        /** 默认边框间距 */
        const val DEF_INNER_FRAME_PERCENTAGE = 0.3f
        /** 默认起始角度 */
        const val DEF_START_ANGLE = -90.0

    }

    /** 边框颜色 */
    @ColorInt
    private var mFrameColor = Color.BLACK
    /** 边框线宽度 */
    private var mFrameStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 边框间距 */
    private var mFramePadding = -1
    /** 边框是否圆形，否为多边形 */
    private var isRound = true
    /** 边框画笔 */
    private var mFramePaint: Paint? = null

    /** 内圈边框颜色 */
    @ColorInt
    private var mInnerFrameColor = Color.BLACK
    /** 内圈边框线宽度 */
    private var mInnerFrameStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 内圈边框占比 */
    private var mInnerFramePercentage = DEF_INNER_FRAME_PERCENTAGE
    /** 内圈边框绘画风格 */
    private var mInnerFramePaintStyle = Paint.Style.STROKE
    /** 内圈边框画笔 */
    private var mInnerFramePaint: Paint? = null

    /** 是否现实内部线 */
    private var isShowLine = true
    /** 内部线颜色 */
    @ColorInt
    private var mInnerLineColor = Color.BLACK
    /** 内部线宽度 */
    private var mInnerLineStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 内部线画笔 */
    private var mInnerLinePaint: Paint? = null

    /** 文字颜色 */
    @ColorInt
    private var mLabelColor = Color.BLACK
    /** 文字宽度 */
    private var mLabelStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 文字画笔 */
    private var mLabelPaint: Paint? = null

    /** 数据列表 */
    private var mList = ArrayList<RadarnyBean>()
    /** 外圈数据点位列表 */
    private var mPointPairList = ArrayList<Pair<Double, Double>>()
    /** 内圈数据点位列表 */
    private var mPointInnerPairList = ArrayList<Pair<Double, Double>>()

    /** 控件边长 */
    private var mSideLength = -1
    /** 控件中点 */
    private var mCenter = -1f
    /** 控件半径 */
    private var mRadius = -1f
    /** 每个点位的平均角度 */
    private var mAverageAngle = -1


    constructor(context: Context?) : super(context){
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet?) {
        configLayout(attrs)
        build(createDefData())
    }

    private fun configLayout(attrs: AttributeSet?) {
        var typedArray: TypedArray? = null
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarnyView)
        }







        typedArray?.recycle()

    }

    fun build(list: ArrayList<RadarnyBean>) {
        mSideLength = -1
        mFramePaint = createFramePaint()
        mInnerFramePaint = createInnerFramePaint()
        mInnerLinePaint = createInnerLinePaint()
        mLabelPaint = createLabelPaint()
        if (list.size >= 3){
            mList = list
        }
    }

    private fun createDefData(): ArrayList<RadarnyBean> {
        val list = ArrayList<RadarnyBean>()
        for (i in 0 until 5) {
            list.add(RadarnyBean("label$i", 0f))
        }
        return list
    }

    private fun createFramePaint(): Paint {
        val paint = Paint()
        paint.strokeWidth = mFrameStrokeWidth.toFloat()
        paint.color = mFrameColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = Paint.Style.STROKE
        return paint
    }

    private fun createInnerFramePaint(): Paint {
        val paint = Paint()
        paint.strokeWidth = mInnerFrameStrokeWidth.toFloat()
        paint.color = mInnerFrameColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = mInnerFramePaintStyle
        return paint
    }

    private fun createInnerLinePaint(): Paint {
        val paint = Paint()
        paint.strokeWidth = mInnerLineStrokeWidth.toFloat()
        paint.color = mInnerLineColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = Paint.Style.STROKE
        return paint
    }

    private fun createLabelPaint(): Paint {
        val paint = Paint()
        paint.strokeWidth = mLabelStrokeWidth.toFloat()
        paint.color = mLabelColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = Paint.Style.STROKE
        return paint
    }

    /** 边框是否圆形 */
    fun isFrameRound() = isRound

    /** 设置边框是否圆形[isRound]，否为多边形 */
    fun setFrameRound(isRound: Boolean): RadarnyView {
        this.isRound = isRound
        return this
    }

    /** 获取边框颜色 */
    fun getFrameColor() = mFrameColor

    /** 获取边框线宽度 */
    fun getFrameStrokeWidth() = mFrameStrokeWidth

    /** 设置边框属性：颜色[color]，线宽[] */
    fun setFrame(
        @ColorInt color: Int,
        strokeWidth: Int = DEF_FRAME_STROKE_WIDTH,
    ): RadarnyView {
        mFrameColor = color
        mFrameStrokeWidth = strokeWidth
        return this
    }

    /** 设置内圈边框属性：颜色[color]，线宽[] */
    fun setInnerFrame(
        @ColorInt color: Int,
        strokeWidth: Int = DEF_FRAME_STROKE_WIDTH,
        paintStyle: Paint.Style = Paint.Style.STROKE,
        percentage: Float = DEF_INNER_FRAME_PERCENTAGE
    ): RadarnyView {
        mInnerFrameColor = color
        mInnerFrameStrokeWidth = strokeWidth
        mInnerFramePaintStyle = paintStyle
        mInnerFramePercentage = percentage
        return this
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        anchorParam()
        if (isRound) {
            drawFrameCircle(canvas)
        } else {
            drawFrameLine(canvas, mFramePaint, mPointPairList) // 外边框
            drawFrameLine(canvas, mInnerFramePaint, mPointInnerPairList) // 内边框
        }
        mPointPairList.forEachIndexed { i, pair ->
            if (isShowLine){
                drawLine(canvas, pair.first, pair.second)
            }
            drawLabel(canvas, mList[i], pair.first, pair.second)
        }

    }

    /** 画圆边框 */
    private fun drawFrameCircle(canvas: Canvas) {
        val paint = mFramePaint ?: return
        // 外圈
        canvas.drawCircle(mCenter, mCenter, mRadius, paint)
        val innerPaint = mInnerFramePaint ?: return
        // 内圈
        canvas.drawCircle(mCenter, mCenter, mRadius * mInnerFramePercentage, innerPaint)
    }

    /** 画线边框 */
    private fun drawFrameLine(canvas: Canvas, paints: Paint?, pointPairList: ArrayList<Pair<Double, Double>>) {
        val paint = paints ?: return
        var lastX = 0.0
        var lastY = 0.0
        var firstX = 0.0
        var firstY = 0.0
        for (i in 0 until pointPairList.size) {
            val x = pointPairList[i].first
            val y = pointPairList[i].second
            if (i == 0) {//第一个点位保存数据
                lastX = x
                lastY = y
                firstX = x
                firstY = y
                continue
            }
            canvas.drawLine(lastX.toFloat(), lastY.toFloat(), x.toFloat(), y.toFloat(), paint)
            if (i == pointPairList.size - 1) {//最后一个点位收边
                canvas.drawLine(x.toFloat(), y.toFloat(), firstX.toFloat(), firstY.toFloat(), paint)
            }
            lastX = x
            lastY = y
        }
    }

    /** 画内部线 */
    private fun drawLine(canvas: Canvas, x: Double, y: Double) {
        val paint = mInnerLinePaint ?: return
        canvas.drawLine(mCenter, mCenter, x.toFloat(), y.toFloat(), paint)
    }

    /** 画标签文字 */
    private fun drawLabel(canvas: Canvas, bean: RadarnyBean, x: Double, y: Double) {
        val paint = mLabelPaint ?: return
        val labelRect = Rect()
        val valueRect = Rect()
        paint.getTextBounds(bean.label, 0, bean.label.length, labelRect)
        paint.getTextBounds(bean.value.toString(), 0, bean.value.toString().length, valueRect)



        //        if (null == mLabels || mLabels!!.size != mSides) {
        //            return
        //        }
        //
        //        val maxLength = mActuallyRadius
        //
        //        val strNumericValues = DecimalFormat("##").format((mAnimationProgress!![i] * 100).toDouble())
        //
        //        val textBoundLabel = Rect()
        //        val textBoundNumeric = Rect()
        //
        //        mPaintLabels!!.getTextBounds(mLabels!![i], 0, mLabels!![i]!!.length, textBoundLabel)
        //        mPaintLabels!!.getTextBounds(strNumericValues, 0, strNumericValues.length, textBoundNumeric)
        //
        //        val actuallyValues = (maxLength + textBoundLabel.width()).toFloat()
        //
        //        val x = (cos(angle) * actuallyValues + mViewCenter).toInt().toFloat()
        //        val y = (sin(angle) * actuallyValues + mViewCenter).toInt().toFloat()
        //
        //        //Draw Label
        //        canvas.drawText(
        //            mLabels!![i].toString(),
        //            x - textBoundLabel.width() / 2f, y + textBoundLabel.height() / 2f, mPaintLabels!!
        //        )
        //        //Draw Progress Value
        //        canvas.drawText(
        //            strNumericValues,
        //            x - textBoundNumeric.width() / 2f, y - textBoundLabel.height() / 2f, mPaintLabels!!
        //        )

    }

    /** 锚定参数 */
    private fun anchorParam() {
        if (mSideLength == -1) {
            mPointPairList.clear()
            mPointInnerPairList.clear()
            mSideLength = min(width, height)//获取最短边长
            mCenter = mSideLength / 2.0f//获取中心位置
            mFramePadding = mSideLength / 6
            mRadius = mCenter - mFramePadding//获取半径
            mAverageAngle = 360 / mList.size
            mList.forEachIndexed { i, radarnyBean ->
                val angle = mAverageAngle * i + DEF_START_ANGLE
                var x = cos(Math.toRadians(angle)) * mRadius + mCenter
                var y = sin(Math.toRadians(angle)) * mRadius + mCenter
                mPointPairList.add(Pair(x, y))
                x = cos(Math.toRadians(angle)) * mRadius * mInnerFramePercentage + mCenter
                y = sin(Math.toRadians(angle)) * mRadius * mInnerFramePercentage + mCenter
                mPointInnerPairList.add(Pair(x, y))
            }
        }
    }






}