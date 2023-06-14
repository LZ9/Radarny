package com.lodz.android.radarny

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import java.text.DecimalFormat
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
        /** 默认文字离边框的间距 */
        const val DEF_TEXT_PERCENTAGE = 1.3f
        /** 默认图片背景间距 */
        const val DEF_SRC_BG_PERCENTAGE = 0.6f
        /** 默认起始角度 */
        const val DEF_START_ANGLE = -90.0
        /** 默认文字大小 */
        const val DEF_TEXT_SIZE = 35
    }

    /** 边框颜色 */
    @ColorInt
    private var mFrameColor = Color.BLACK
    /** 边框线宽度 */
    private var mFrameStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 边框是否圆形，否为多边形 */
    private var isRound = true
    /** 边框路径 */
    private var mFramePath = Path()
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
    /** 内圈边框路径 */
    private var mInnerFramePath = Path()
    /** 内圈边框画笔 */
    private var mInnerFramePaint: Paint? = null

    /** 是否显示内部线 */
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
    private var mTextColor = Color.BLACK
    /** 文字大小 */
    private var mTextSize = DEF_TEXT_SIZE
    /** 文字离边框占比 */
    private var mTextPercentage = DEF_TEXT_PERCENTAGE
    /** 文字画笔 */
    private var mTextPaint: Paint? = null

    /** 数据列表 */
    private var mList = ArrayList<RadarnyBean>()
    /** 外圈数据点位列表 */
    private var mPointPairList = ArrayList<Pair<Double, Double>>()
    /** 内圈数据点位列表 */
    private var mPointInnerPairList = ArrayList<Pair<Double, Double>>()
    /** 总大小 */
    private var mMaxValue = 0f

    /** 数值颜色 */
    @ColorInt
    private var mValueColor = Color.argb(125, 30, 110, 210)
    /** 数值线宽度 */
    private var mValueStrokeWidth = DEF_FRAME_STROKE_WIDTH
    /** 数值绘画风格 */
    private var mValuePaintStyle = Paint.Style.FILL
    /** 数值路径 */
    private var mValuePath = Path()
    /** 数值画笔 */
    private var mValuePaint: Paint? = null

    /** 图片背景颜色 */
    @ColorInt
    private var mSrcBgColor = Color.WHITE
    /** 图片背景占比 */
    private var mSrcBgPercentage = DEF_SRC_BG_PERCENTAGE
    /** 是否显示图片 */
    private var isShowSrc: Boolean = true
    /** 图片资源id */
    @DrawableRes
    private var mSrcResId = 0
    /** 图片宽度 */
    private var mSrcWidth = 0
    /** 图片高度 */
    private var mSrcHeight = 0
    /** 图片Bitmap */
    private var mSrcBitmap: Bitmap? = null
    /** 图片画笔 */
    private var mSrcPaint: Paint? = null

    /** 动画时长（0表示不启动动画） */
    private var mAnimDuration = 0
    /** 动画进度百分比 */
    private var mValueProgressPercentage  = 1f
    /** 是否动画正在运行 */
    private var isAnimRunning = false

    /** 控件边长 */
    private var mSideLength = -1
    /** 控件中点X */
    private var mCenterX = -1f
    /** 控件中点Y */
    private var mCenterY = -1f
    /** 控件半径 */
    private var mRadius = -1f
    /** 每个点位的平均角度 */
    private var mAverageAngle = -1.0
    /** 边框间距 */
    private var mFramePadding = -1


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
        setData(createDefData())
        build()
    }

    private fun configLayout(attrs: AttributeSet?) {
        var typedArray: TypedArray? = null
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarnyView)
        }
        mFrameColor = typedArray?.getColor(R.styleable.RadarnyView_frameColor, Color.BLACK) ?: Color.BLACK
        mFrameStrokeWidth = typedArray?.getInt(R.styleable.RadarnyView_frameWidth, DEF_FRAME_STROKE_WIDTH) ?: DEF_FRAME_STROKE_WIDTH
        isRound = typedArray?.getBoolean(R.styleable.RadarnyView_isRound, true) ?: true
        mInnerFrameColor = typedArray?.getColor(R.styleable.RadarnyView_innerFrameColor, Color.BLACK) ?: Color.BLACK
        mInnerFrameStrokeWidth= typedArray?.getInt(R.styleable.RadarnyView_innerFrameWidth, DEF_FRAME_STROKE_WIDTH) ?: DEF_FRAME_STROKE_WIDTH
        mInnerFramePercentage = typedArray?.getFloat(R.styleable.RadarnyView_innerFramePercentage, DEF_INNER_FRAME_PERCENTAGE) ?: DEF_INNER_FRAME_PERCENTAGE
        isShowLine = typedArray?.getBoolean(R.styleable.RadarnyView_isShowLine, true) ?: true
        mInnerLineColor = typedArray?.getColor(R.styleable.RadarnyView_lineColor, Color.BLACK) ?: Color.BLACK
        mInnerLineStrokeWidth = typedArray?.getInt(R.styleable.RadarnyView_lineWidth, DEF_FRAME_STROKE_WIDTH) ?: DEF_FRAME_STROKE_WIDTH
        mTextColor = typedArray?.getColor(R.styleable.RadarnyView_textColor, Color.BLACK) ?: Color.BLACK
        mTextSize = typedArray?.getDimensionPixelSize(R.styleable.RadarnyView_textSize, DEF_TEXT_SIZE) ?: DEF_TEXT_SIZE
        mTextPercentage = typedArray?.getFloat(R.styleable.RadarnyView_textPercentage, DEF_TEXT_PERCENTAGE) ?: DEF_TEXT_PERCENTAGE
        mMaxValue = typedArray?.getFloat(R.styleable.RadarnyView_maxValue, 0f) ?: 0f
        val defColor = Color.argb(125, 30, 110, 210)
        mValueColor = typedArray?.getColor(R.styleable.RadarnyView_valueColor, defColor) ?:defColor
        mValueStrokeWidth = typedArray?.getInt(R.styleable.RadarnyView_valueWidth, DEF_FRAME_STROKE_WIDTH) ?: DEF_FRAME_STROKE_WIDTH
        val valuePaintStyle = typedArray?.getInt(R.styleable.RadarnyView_valuePaintStyle, Paint.Style.FILL.ordinal) ?: Paint.Style.FILL.ordinal
        for (style in Paint.Style.values()) {
            if (valuePaintStyle == style.ordinal){
                mValuePaintStyle = style
            }
        }
        mSrcBgColor = typedArray?.getColor(R.styleable.RadarnyView_srcBgColor, Color.WHITE) ?:Color.WHITE
        mSrcBgPercentage = typedArray?.getFloat(R.styleable.RadarnyView_srcBgPercentage, DEF_SRC_BG_PERCENTAGE) ?: DEF_SRC_BG_PERCENTAGE
        isShowSrc = typedArray?.getBoolean(R.styleable.RadarnyView_isShowSrc, true) ?: true
        mSrcResId = typedArray?.getResourceId(R.styleable.RadarnyView_src, 0) ?: 0
        mSrcWidth = typedArray?.getDimensionPixelSize(R.styleable.RadarnyView_srcWidth, 0) ?: 0
        mSrcHeight = typedArray?.getDimensionPixelSize(R.styleable.RadarnyView_srcHeight, 0) ?: 0
        mAnimDuration = typedArray?.getInt(R.styleable.RadarnyView_animDuration, 0) ?: 0
        typedArray?.recycle()
    }

    /** 创建默认数据 */
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

    private fun createTextPaint(): Paint {
        val paint = Paint()
        paint.color = mTextColor
        paint.textSize = mTextSize.toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = Paint.Style.FILL
        return paint
    }

    private fun createValuePaint(): Paint {
        val paint = Paint()
        paint.strokeWidth = mValueStrokeWidth.toFloat()
        paint.color = mValueColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = mValuePaintStyle
        return paint
    }

    private fun createSrcPaint(): Paint {
        val paint = Paint()
        paint.color = mSrcBgColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.shader = null
        paint.style = Paint.Style.FILL
        return paint
    }

    /** 完成构建 */
    fun build() {
        if (isAnimRunning){//若动画未结束不响应更改
            return
        }
        mSideLength = -1
        mFramePaint = createFramePaint()
        mFramePath = Path()
        mInnerFramePaint = createInnerFramePaint()
        mInnerFramePath = Path()
        mInnerLinePaint = createInnerLinePaint()
        mTextPaint = createTextPaint()
        mValuePaint = createValuePaint()
        mValuePath = Path()
        mSrcPaint = createSrcPaint()
        if (mList.isEmpty()) {
            mList = createDefData()
        }
        if (mSrcResId != 0){
            mSrcBitmap = BitmapFactory.decodeResource(resources, mSrcResId)
        }
        if (mAnimDuration > 0) {
            doAnim(mAnimDuration)
        }
        invalidate()
    }

    /** 使用动画展示 */
    private fun doAnim(duration: Int) {
        isAnimRunning = true
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = duration.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.addUpdateListener {
            mValueProgressPercentage = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimRunning = false
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimRunning = false
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animator.start()
    }


    /** 设置数据[list] */
    fun setData(list: ArrayList<RadarnyBean>): RadarnyView {
        if (list.size >= 3) {
            mList = list
        }
        return this
    }

    /** 外边框是否圆形 */
    fun isFrameRound() = isRound

    /** 设置外边框是否圆形[isRound] */
    fun setFrameRound(isRound: Boolean): RadarnyView {
        this.isRound = isRound
        return this
    }

    /** 获取外边框颜色 */
    fun getFrameColor() = mFrameColor

    /** 设置外边框颜色[color] */
    fun setFrameColor(@ColorInt color: Int): RadarnyView {
        mFrameColor = color
        return this
    }

    /** 获取外边框线宽度 */
    fun getFrameStrokeWidth() = mFrameStrokeWidth

    /** 设置外边框线宽度[strokeWidth] */
    fun setFrameStrokeWidth(strokeWidth: Int): RadarnyView {
        mFrameStrokeWidth = strokeWidth
        return this
    }

    /** 获取内圈边框颜色 */
    fun getInnerFrameColor() = mInnerFrameColor

    /** 设置内圈边框颜色[color] */
    fun setInnerFrameColor(@ColorInt color: Int): RadarnyView {
        mInnerFrameColor = color
        return this
    }

    /** 获取内圈边框线宽度 */
    fun getInnerFrameStrokeWidth() = mInnerFrameStrokeWidth

    /** 设置内圈边框线宽度[strokeWidth] */
    fun setInnerFrameStrokeWidth(strokeWidth: Int): RadarnyView {
        mInnerFrameStrokeWidth = strokeWidth
        return this
    }

    /** 获取内圈边框占比 */
    fun getInnerFramePercentage() = mInnerFramePercentage

    /** 设置内圈边框占比[percentage] */
    fun setInnerFramePercentage(percentage: Float): RadarnyView {
        mInnerFramePercentage = percentage
        return this
    }

    /** 获取内圈边框绘画风格 */
    fun getInnerFramePaintStyle() = mInnerFramePaintStyle

    /** 设置内圈边框绘画风格[style] */
    fun setInnerFramePaintStyle(style: Paint.Style): RadarnyView {
        mInnerFramePaintStyle = style
        return this
    }

    /** 是否显示内部线 */
    fun isShowLine() = isShowLine

    /** 设置是否显示内部线[isShow] */
    fun setShowLine(isShow: Boolean): RadarnyView {
        this.isShowLine = isShow
        return this
    }

    /** 获取内部线颜色 */
    fun getInnerLineColor() = mInnerLineColor

    /** 设置内部线颜色[color] */
    fun setInnerLineColor(@ColorInt color: Int): RadarnyView {
        mInnerLineColor = color
        return this
    }

    /** 获取内部线宽度 */
    fun getInnerLineStrokeWidth() = mInnerLineStrokeWidth

    /** 设置内部线宽度[strokeWidth] */
    fun setInnerLineStrokeWidth(strokeWidth: Int): RadarnyView {
        mInnerLineStrokeWidth = strokeWidth
        return this
    }

    /** 获取文字颜色 */
    fun getTextColor() = mTextColor

    /** 设置文字颜色[color] */
    fun setTextColor(@ColorInt color: Int): RadarnyView {
        mTextColor = color
        return this
    }

    /** 获取文字大小 */
    fun getTextSize() = mTextSize

    /** 设置文字大小[size] */
    fun setTextSize(size: Int): RadarnyView {
        mTextSize = size
        return this
    }

    /** 获取文字离边框占比 */
    fun getTextPercentage() = mTextPercentage

    /** 设置文字离边框占比[percentage] */
    fun setTextPercentage(percentage: Float): RadarnyView {
        mTextPercentage = percentage
        return this
    }

    /** 获取总大小 */
    fun getMaxValue() = mMaxValue

    /** 设置总大小[max] */
    fun setMaxValue(max: Float): RadarnyView {
        mMaxValue = max
        return this
    }

    /** 获取数值颜色 */
    fun getValueColor() = mValueColor

    /** 设置数值颜色[color] */
    fun setValueColor(@ColorInt color: Int): RadarnyView {
        mValueColor = color
        return this
    }

    /** 获取数值线宽度 */
    fun getValueStrokeWidth() = mValueStrokeWidth

    /** 设置数值线宽度[strokeWidth] */
    fun setValueStrokeWidth(strokeWidth: Int): RadarnyView {
        mValueStrokeWidth = strokeWidth
        return this
    }

    /** 获取数值绘画风格 */
    fun getValuePaintStyle() = mValuePaintStyle

    /** 设置数值绘画风格[style] */
    fun setValuePaintStyle(style: Paint.Style): RadarnyView {
        mValuePaintStyle = style
        return this
    }

    /** 获取图片背景颜色 */
    fun getSrcBgColor() = mSrcBgColor

    /** 设置图片背景颜色[color] */
    fun setSrcBgColor(@ColorInt color: Int): RadarnyView {
        mSrcBgColor = color
        return this
    }

    /** 获取图片背景占比 */
    fun getSrcBgPercentage() = mSrcBgPercentage

    /** 设置图片背景占比[percentage] */
    fun setSrcBgPercentage(percentage: Float): RadarnyView {
        mSrcBgPercentage = percentage
        return this
    }

    /** 是否显示图片 */
    fun isShowSrc() = isShowSrc

    /** 设置是否显示图片[isShow] */
    fun setShowSrc(isShow: Boolean): RadarnyView {
        this.isShowSrc = isShow
        return this
    }

    /** 获取图片资源id */
    fun getSrcResId() = mSrcResId

    /** 设置图片资源id[id] */
    fun setSrcResId(@DrawableRes id: Int): RadarnyView {
        mSrcResId = id
        return this
    }

    /** 获取图片宽度 */
    fun getSrcWidth() = mSrcWidth

    /** 设置图片宽度[width] */
    fun setSrcWidth(width: Int): RadarnyView {
        mSrcWidth = width
        return this
    }

    /** 获取图片高度 */
    fun getSrcHeight() = mSrcHeight

    /** 设置图片高度[width] */
    fun setSrcHeight(height: Int): RadarnyView {
        mSrcHeight = height
        return this
    }

    /** 获取图片Bitmap */
    fun getSrcBitmap() = mSrcBitmap

    /** 设置图片Bitmap[bitmap] */
    fun setSrcBitmap(bitmap: Bitmap): RadarnyView {
        mSrcBitmap = bitmap
        return this
    }

    /** 获取动画时长（0表示不启动动画） */
    fun getAnimDuration() = mAnimDuration

    /** 设置动画时长[duration]，0表示不启动动画 */
    fun setAnimDuration(duration: Int): RadarnyView {
        this.mAnimDuration = duration
        return this
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        anchorParam()
        mList.forEachIndexed { i, bean ->
            if (isShowLine){
                drawLine(canvas, mPointPairList[i].first, mPointPairList[i].second)
            }
            drawLabel(canvas, bean, i)
        }
        drawPolygon(canvas, mList)
        if (isShowSrc) {
            drawSrc(canvas)
        }
        if (isRound) {
            drawFrameCircle(canvas)
        } else {
            drawFrameLine(canvas, mFramePaint, mFramePath, mPointPairList) // 外边框
            drawFrameLine(canvas, mInnerFramePaint, mInnerFramePath, mPointInnerPairList) // 内边框
        }
    }

    /** 画圆边框 */
    private fun drawFrameCircle(canvas: Canvas) {
        val paint = mFramePaint ?: return
        // 外圈
        canvas.drawCircle(mCenterX, mCenterY, mRadius, paint)
        val innerPaint = mInnerFramePaint ?: return
        // 内圈
        canvas.drawCircle(mCenterX, mCenterY, mRadius * mInnerFramePercentage, innerPaint)
    }

    /** 画线边框 */
    private fun drawFrameLine(canvas: Canvas, paints: Paint?, path: Path, list: ArrayList<Pair<Double, Double>>) {
        val paint = paints ?: return
        for (i in 0 until list.size) {
            val x = list[i].first.toFloat()
            val y = list[i].second.toFloat()
            if (i == 0){
                path.moveTo(x, y)
                continue
            }
            path.lineTo(x, y)
            if (i == list.size - 1) {
                path.close()
            }
        }
        canvas.drawPath(path, paint)
    }

    /** 画内部线 */
    private fun drawLine(canvas: Canvas, x: Double, y: Double) {
        val paint = mInnerLinePaint ?: return
        canvas.drawLine(mCenterX, mCenterY, x.toFloat(), y.toFloat(), paint)
    }

    /** 画标签文字 */
    private fun drawLabel(canvas: Canvas, bean: RadarnyBean, i: Int) {
        val paint = mTextPaint ?: return
        val value = formatValue(bean.value * mValueProgressPercentage)
        val labelRect = Rect()
        val valueRect = Rect()
        paint.getTextBounds(bean.label, 0, bean.label.length, labelRect)
        paint.getTextBounds(value.toString(), 0, value.toString().length, valueRect)

        val pair = getXY(i, mRadius, mTextPercentage)
        val x = pair.first
        val y = pair.second

        // 画标签
        canvas.drawText(
            bean.label,
            x.toFloat() - labelRect.width() / 2f,
            y.toFloat() - labelRect.height() / 2f,
            paint
        )
        // 画数值
        canvas.drawText(
            value.toString(),
            x.toFloat() - valueRect.width() / 2f,
            y.toFloat() + valueRect.height(),
            paint
        )
    }

    /** 绘制多边形 */
    private fun drawPolygon(canvas: Canvas, list: ArrayList<RadarnyBean>) {
        val paint = mValuePaint ?: return
        for (i in 0 until list.size){
            val value = formatValue(list[i].value * mValueProgressPercentage)
            val offset = mRadius * mInnerFramePercentage//偏移量
            val r = if (mMaxValue == 0f) offset else (mRadius - offset) * value / mMaxValue + offset
            val pair = getXY(i, r, 1f)
            val x = pair.first.toFloat()
            val y = pair.second.toFloat()
            if (i == 0) {
                mValuePath.moveTo(x, y)
                continue
            }
            mValuePath.lineTo(x, y)
            if (i == list.size - 1) {
                mValuePath.close()
            }
        }
        canvas.drawPath(mValuePath, paint)
    }

    /** 绘制图片 */
    private fun drawSrc(canvas: Canvas) {
        val bitmap = mSrcBitmap ?: return
        val paint = mSrcPaint ?: return
        val offset = mRadius * mInnerFramePercentage//偏移量
        canvas.drawCircle(mCenterX, mCenterY, offset * mSrcBgPercentage, paint)

        val srcWidth = if (mSrcWidth == 0) bitmap.width else mSrcWidth
        val srcHeight = if (mSrcHeight == 0) bitmap.height else mSrcHeight

        val src = Rect(0, 0, bitmap.width, bitmap.height)
        val dst = Rect(
            (mCenterX - srcWidth / 2).toInt(),
            (mCenterY - srcHeight / 2).toInt(),
            (mCenterX + srcWidth / 2).toInt(),
            (mCenterY + srcHeight / 2).toInt()
        )
        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    /** 锚定参数 */
    private fun anchorParam() {
        if (mSideLength == -1) {
            mPointPairList.clear()
            mPointInnerPairList.clear()
            mSideLength = min(width, height)//获取最短边长
            mFramePadding = mSideLength / 6
            //获取中心位置
            if (width == height) {
                mCenterX = mSideLength / 2.0f
                mCenterY = mSideLength / 2.0f
                mRadius = mCenterX - mFramePadding//获取半径
            } else if (width > height) {//横向矩形X轴要加上偏移量
                mCenterX = mSideLength / 2.0f + (width - height) / 2f
                mCenterY = mSideLength / 2.0f
                mRadius = mCenterY - mFramePadding//获取半径
            } else {//纵向矩形Y轴要加上偏移量
                mCenterX = mSideLength / 2.0f
                mCenterY = mSideLength / 2.0f + (height - width) / 2f
                mRadius = mCenterX - mFramePadding//获取半径
            }
            mAverageAngle = 360.0 / mList.size
            var max = 0f
            mList.forEachIndexed { i, bean ->
                mPointPairList.add(getXY(i, mRadius, 1.0f))
                mPointInnerPairList.add(getXY(i, mRadius, mInnerFramePercentage))
                if (max < bean.value){
                    max = bean.value
                }
            }
            if (mMaxValue <= max){
                mMaxValue = max
            }
        }
    }

    /** 根据比例获取XY坐标 */
    private fun getXY(index: Int, radius: Float, percentage: Float): Pair<Double, Double> {
        val angle = mAverageAngle * index + DEF_START_ANGLE
        val x = cos(Math.toRadians(angle)) * radius * percentage + mCenterX
        val y = sin(Math.toRadians(angle)) * radius * percentage + mCenterY
        return Pair(x, y)
    }

    /** 格式化一位小数 */
    private fun formatValue(value: Float): Float {
        val df = DecimalFormat("#.0")
        return df.format(value).toFloat()
    }
}