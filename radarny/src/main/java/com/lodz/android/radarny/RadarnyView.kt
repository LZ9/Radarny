package com.lodz.android.radarny

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
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
    private val mValuePath = Path()
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
    private var mSrcResId = android.R.drawable.ic_menu_camera
    /** 图片Bitmap */
    private var mSrcBitmap: Bitmap? = null
    /** 图片画笔 */
    private var mSrcPaint: Paint? = null

    /** 控件边长 */
    private var mSideLength = -1
    /** 控件中点 */
    private var mCenter = -1f
    /** 控件半径 */
    private var mRadius = -1f
    /** 每个点位的平均角度 */
    private var mAverageAngle = -1
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
        build(createDefData())
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
        mSrcResId = typedArray?.getResourceId(R.styleable.RadarnyView_src, android.R.drawable.ic_menu_camera) ?: android.R.drawable.ic_menu_camera
        typedArray?.recycle()
    }

    /** 创建默认数据 */
    private fun createDefData(): ArrayList<RadarnyBean> {
        val list = ArrayList<RadarnyBean>()
        for (i in 0 until 5) {
            list.add(RadarnyBean("label$i", i * 20.0f))
//            list.add(RadarnyBean("label$i", 0f))
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

    /** 完成数据[list]构建 */
    fun build(list: ArrayList<RadarnyBean>) {
        mSideLength = -1
        mFramePaint = createFramePaint()
        mInnerFramePaint = createInnerFramePaint()
        mInnerLinePaint = createInnerLinePaint()
        mTextPaint = createTextPaint()
        mValuePaint = createValuePaint()
        mSrcPaint = createSrcPaint()
        if (list.size >= 3){
            mList = list
        }
        if (mSrcBitmap == null){
            mSrcBitmap = BitmapFactory.decodeResource(resources, mSrcResId)
        }
    }

    /** 外边框是否圆形 */
    fun isFrameRound() = isRound

    /** 获取外边框颜色 */
    fun getFrameColor() = mFrameColor

    /** 获取外边框线宽度 */
    fun getFrameStrokeWidth() = mFrameStrokeWidth

    /** 设置外边框属性：颜色[color]，是否圆形[isRound]，线宽[strokeWidth] */
    fun setFrame(
        @ColorInt color: Int,
        isRound: Boolean = true,
        strokeWidth: Int = DEF_FRAME_STROKE_WIDTH,
    ): RadarnyView {
        mFrameColor = color
        this.isRound = isRound
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
            drawFrameLine(canvas, mFramePaint, mFramePath, mPointPairList) // 外边框
            drawFrameLine(canvas, mInnerFramePaint, mInnerFramePath, mPointInnerPairList) // 内边框
        }
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
        canvas.drawLine(mCenter, mCenter, x.toFloat(), y.toFloat(), paint)
    }

    /** 画标签文字 */
    private fun drawLabel(canvas: Canvas, bean: RadarnyBean, i: Int) {
        val paint = mTextPaint ?: return
        val labelRect = Rect()
        val valueRect = Rect()
        paint.getTextBounds(bean.label, 0, bean.label.length, labelRect)
        paint.getTextBounds(bean.value.toString(), 0, bean.value.toString().length, valueRect)

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
            bean.value.toString(),
            x.toFloat() - valueRect.width() / 2f,
            y.toFloat() + valueRect.height(),
            paint
        )
    }

    /** 绘制多边形 */
    private fun drawPolygon(canvas: Canvas, list: ArrayList<RadarnyBean>) {
        val paint = mValuePaint ?: return
        for (i in 0 until list.size){
            val offset = mRadius * mInnerFramePercentage//偏移量
            val r = if (mMaxValue == 0f) offset else (mRadius - offset) * list[i].value / mMaxValue + offset
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
        canvas.drawCircle(mCenter, mCenter, offset * mSrcBgPercentage, paint)


        canvas.drawBitmap(
            bitmap,
            mCenter - bitmap.width / 2,
            mCenter - bitmap.height / 2,
            paint
        )
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
            var max = 0f
            mList.forEachIndexed { i, bean ->
                mPointPairList.add(getXY(i, mRadius, 1.0f))
                mPointInnerPairList.add(getXY(i, mRadius, mInnerFramePercentage))
                if (max < bean.value){
                    max = bean.value
                }
            }
            if (mMaxValue == 0f){
                mMaxValue = max
            }
        }
    }

    /** 根据比例获取XY坐标 */
    private fun getXY(index: Int, radius: Float, percentage: Float): Pair<Double, Double> {
        val angle = mAverageAngle * index + DEF_START_ANGLE
        val x = cos(Math.toRadians(angle)) * radius * percentage + mCenter
        val y = sin(Math.toRadians(angle)) * radius * percentage + mCenter
        return Pair(x, y)
    }

}