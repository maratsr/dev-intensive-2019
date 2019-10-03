package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.spToPx
import kotlin.math.min
import kotlin.random.Random

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = Color.TRANSPARENT // Color.GREEN
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val SCALE_TYPE = ScaleType.CENTER_CROP
    }

    private var mBitmapShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mBorderBounds: RectF
    private var mBitmapDrawBounds: RectF
    private var paintBitmap: Paint
    private var paintBorder: Paint
    private var mShaderMatrix: Matrix
    private var borderWidth = DEFAULT_BORDER_WIDTH
    private var borderColor = DEFAULT_BORDER_COLOR
    private var initials: String? = null
    //private var fillColor: Int? = null //added

    private val bgColors = arrayOf(
        "#7BC862",
        "#E17076",
        "#FAA774",
        "#6EC9CB",
        "#65AADD",
        "#A695E7",
        "#EE7AAE"
    )

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, 0, 0)

            borderColor = a.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimension(
                R.styleable.AvatarImageView_aiv_borderWidth,
                DEFAULT_BORDER_WIDTH
            )
            //fillColor = bgColors[Random.nextInt(bgColors.size)].toColorInt() // added
            //Log.d("M_AvatarImageView init","Color ${fillColor}" )
            a.recycle()
        }

        mShaderMatrix = Matrix()
        mBorderBounds = RectF()
        mBitmapDrawBounds = RectF()
        paintBitmap = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        //setInitials("??") //added
    }

    fun setInitials(initials: String) {
        this.initials = initials
        setImageDrawable(getTextAvatar(initials))
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val halfBorderWidth = paintBorder.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mBorderBounds.set(mBitmapDrawBounds)
        mBorderBounds.inset(halfBorderWidth, halfBorderWidth)

        updateBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawBorder(canvas)
    }

    @Dimension
    fun getBorderWidth(): Int = borderWidth.toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = dp.toFloat()

        updateBitmap()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)

        updateBitmap()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = resources.getColor(colorId, context.theme)
    }

    private fun drawBorder(canvas: Canvas) {
        if (paintBorder.strokeWidth > 0f) {
            canvas.drawOval(mBorderBounds, paintBorder)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, paintBitmap)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        super.setScaleType(SCALE_TYPE)

        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paintBitmap.shader = mBitmapShader

        updateBitmap()
    }

    private fun updateBitmap() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        paintBorder.color = borderColor
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = borderWidth

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds.width() / 2f
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height
            dx = mBitmapDrawBounds.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds.width() / 2f
            dy = mBitmapDrawBounds.top
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            BITMAP_CONFIG
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun getTextAvatar(text: String): Drawable {
        val size = resources.getDimensionPixelSize(R.dimen.avatar_round_size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val c = Canvas()
        c.setBitmap(bitmap)

        val halfSize = (size / 2).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color =  bgColors[Random.nextInt(bgColors.size)].toColorInt() // fillColor!!
        //Log.d("M_AvatarImageView","Color ${fillColor}" )

        c.drawPaint(paint)

        val bounds = Rect()

        paint.textSize = 40.spToPx()
        paint.color = resources.getColor(android.R.color.white, context.theme)
        paint.getTextBounds(text, 0, text.length, bounds)

        c.drawText(text, halfSize - paint.measureText(text) / 2, halfSize + bounds.height() / 2, paint)

        return bitmap.toDrawable(resources)
    }

}