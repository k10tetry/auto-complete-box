package com.k10tetry.autocompletebox

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView

class AutoCompleteBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var selectedAutoCompleteBoxItem: AutoCompleteBoxItem? = null
        private set
        get() {
            return field ?: AutoCompleteBoxItem(-1, autoCompleteTextView.text.toString())
        }

    var items: List<AutoCompleteBoxItem> = emptyList()
        set(value) {
            field = value
            setAdapter(items)
        }

    var viewCornerRadius = 12f.toPx(resources)
        set(value) {
            field = if (value > 16f.toPx(resources)) {
                16f.toPx(resources)
            } else {
                value
            }
        }

    var horizontalPadding = 8f.toPx(resources)
        set(value) {
            field = if (value > 16f.toPx(resources) || value < 4f.toPx(resources)) {
                8f.toPx(resources)
            } else {
                value
            }
        }

    var verticalPadding = 10f.toPx(resources)
        set(value) {
            field = if (value > 16f.toPx(resources) || value < 4f.toPx(resources)) {
                10f.toPx(resources)
            } else {
                value
            }
        }

    var viewStrokeSize = 1f.toPx(resources)
        set(value) {
            field = if (value > 4f.toPx(resources)) {
                4f.toPx(resources)
            } else {
                value
            }
        }

    var fieldStrokeSize = 1f.toPx(resources)
        set(value) {
            field = if (value > 4f.toPx(resources)) {
                4f.toPx(resources)
            } else {
                value
            }
        }

    var fieldPadding = 12f.toPx(resources)
        set(value) {
            field = if (value > 16f.toPx(resources) || value < 4f.toPx(resources)) {
                12f.toPx(resources)
            } else {
                value
            }
        }

    var isSingleLine = true
    var isEditable = true
    var boxStrokeEnabled = true
    var editTextStokeEnabled = true

    var labelTextColor = context.getColor(R.color.primaryColor)
    var viewStrokeColor = context.getColor(R.color.tertiaryVariantColor)
    var fieldStrokeColor = context.getColor(R.color.tertiaryVariantColor)
    var viewBackgroundColor = context.getColor(R.color.backgroundColor)
    var fieldBackgroundColor = context.getColor(R.color.tertiaryColor)
    var fieldTextColor = context.getColor(R.color.primaryColor)

    var fieldTextSize = 16f.toPx(resources)
    var labelTextSize = 16f.toPx(resources)
    var fieldText: String? = null
    var labelText: String? = null

    var minimumCharacters = 1

    var fieldInputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
    var fieldImeOption = EditorInfo.IME_ACTION_DONE

    var autoCompleteBoxListener: AutoCompleteBoxListener? = null

    private val boxPaint = Paint(ANTI_ALIAS_FLAG)
    private val editTextBackgroundPaint = Paint(ANTI_ALIAS_FLAG)
    private val boxStrokePaint = Paint(ANTI_ALIAS_FLAG)
    private val editTextStrokePaint = Paint(ANTI_ALIAS_FLAG)

    private val textView = TextView(context)
    private val autoCompleteTextView = AutoCompleteTextView(context)

    private val boxPath: Path = Path()
    private val editTextPath: Path = Path()

    init {
        obtainStyledAttributes(context, attrs)
        initProperties()

        addView(textView)
        addView(autoCompleteTextView)
    }

    private fun initProperties() {
        boxPaint.apply {
            setColor(viewBackgroundColor)
            style = Paint.Style.FILL
        }

        editTextBackgroundPaint.apply {
            setColor(fieldBackgroundColor)
            style = Paint.Style.FILL
        }

        boxStrokePaint.apply {
            setColor(viewStrokeColor)
            style = Paint.Style.STROKE
            strokeWidth = viewStrokeSize
        }

        editTextStrokePaint.apply {
            setColor(fieldStrokeColor)
            style = Paint.Style.STROKE
            strokeWidth = fieldStrokeSize
        }

        textView.apply {
            text = labelText
            setTextColor(labelTextColor)
            gravity = Gravity.START
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize)
        }

        autoCompleteTextView.apply {
            setOnClickListener { if (isEditable.not()) showDropDown() }
            setText(fieldText)
            setTextColor(fieldTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, fieldTextSize)
            setPadding(fieldPadding.toInt(), paddingTop, fieldPadding.toInt(), paddingBottom)
            imeOptions = fieldImeOption
            inputType = fieldInputType
            dropDownVerticalOffset = 2f.toPx(resources).toInt()
            threshold = minimumCharacters
            isFocusable = isEditable
            isSingleLine = this@AutoCompleteBox.isSingleLine
            background = null
            if (fieldInputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun obtainStyledAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteBox)

        labelText = typedArray.getString(R.styleable.AutoCompleteBox_labelText)
        labelTextColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_labelTextColor, labelTextColor
        )
        labelTextSize = typedArray.getDimension(
            R.styleable.AutoCompleteBox_labelTextSize, labelTextSize
        )
        viewCornerRadius = typedArray.getDimension(
            R.styleable.AutoCompleteBox_viewCornerRadius, viewCornerRadius
        )
        horizontalPadding = typedArray.getDimension(
            R.styleable.AutoCompleteBox_horizontalPadding, horizontalPadding
        )
        verticalPadding = typedArray.getDimension(
            R.styleable.AutoCompleteBox_verticalPadding, verticalPadding
        )
        viewStrokeSize = typedArray.getDimension(
            R.styleable.AutoCompleteBox_viewStrokeSize, viewStrokeSize
        )
        fieldStrokeSize = typedArray.getDimension(
            R.styleable.AutoCompleteBox_fieldStrokeSize, fieldStrokeSize
        )
        viewStrokeColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_viewStrokeColor, viewStrokeColor
        )
        fieldStrokeColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_fieldStrokeColor, fieldStrokeColor
        )
        viewBackgroundColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_viewBackgroundColor, viewBackgroundColor
        )
        fieldBackgroundColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_fieldBackgroundColor, fieldBackgroundColor
        )
        fieldPadding = typedArray.getDimension(
            R.styleable.AutoCompleteBox_fieldPadding, fieldPadding
        )
        fieldTextSize = typedArray.getDimension(
            R.styleable.AutoCompleteBox_fieldTextSize, fieldTextSize
        )
        fieldTextColor = typedArray.getColor(
            R.styleable.AutoCompleteBox_fieldTextColor, fieldTextColor
        )
        fieldText = typedArray.getString(R.styleable.AutoCompleteBox_fieldText)
        boxStrokeEnabled = typedArray.getBoolean(
            R.styleable.AutoCompleteBox_viewStrokeEnabled, boxStrokeEnabled
        )
        editTextStokeEnabled = typedArray.getBoolean(
            R.styleable.AutoCompleteBox_fieldStrokeEnabled, editTextStokeEnabled
        )
        isEditable = typedArray.getBoolean(
            R.styleable.AutoCompleteBox_isEditable, isEditable
        )
        fieldInputType = typedArray.getInteger(
            R.styleable.AutoCompleteBox_android_inputType, fieldInputType
        )
        fieldImeOption = typedArray.getInteger(
            R.styleable.AutoCompleteBox_android_imeOptions, fieldImeOption
        )
        typedArray.recycle()
    }

    fun setPasswordVisible(visible: Boolean) {
        autoCompleteTextView.transformationMethod = if (visible) {
            null
        } else {
            PasswordTransformationMethod.getInstance()
        }
        autoCompleteTextView.setSelection(autoCompleteTextView.text?.length ?: 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // find the required width of the view group
        val widthSpec = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val maxWidth = widthSpec - horizontalPadding.times(2) - viewStrokeSize.times(2)

        // set the max width of children
        textView.maxWidth = maxWidth.times(0.66).toInt()
        autoCompleteTextView.width = maxWidth.toInt()

        // measure every child
        measureChild(textView, maxWidth.toInt(), heightMeasureSpec)
        measureChild(autoCompleteTextView, maxWidth.toInt(), heightMeasureSpec)

        // calculate the required height of the view group
        val heightUsed =
            textView.measuredHeight + autoCompleteTextView.measuredHeight + verticalPadding.times(3) + viewStrokeSize.times(
                3
            )

        // set measurements
        setMeasuredDimension(widthSpec, heightUsed.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        calculateBoxPath()
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var totalHeight = verticalPadding + viewStrokeSize

        textView.layout(
            horizontalPadding.toInt() + viewStrokeSize.toInt(),
            totalHeight.toInt(),
            (textView.measuredWidth + horizontalPadding + viewStrokeSize).toInt(),
            (totalHeight + textView.measuredHeight).toInt()
        )

        totalHeight += textView.measuredHeight + viewStrokeSize

        autoCompleteTextView.layout(
            (horizontalPadding + viewStrokeSize).toInt(),
            (totalHeight + verticalPadding).toInt(),
            (autoCompleteTextView.measuredWidth + horizontalPadding + viewStrokeSize).toInt(),
            (totalHeight + verticalPadding + viewStrokeSize + autoCompleteTextView.measuredHeight).toInt()
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        drawBox(canvas)
        drawEditText(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawEditText(canvas: Canvas) {
        if (editTextStokeEnabled) {
            canvas.drawPath(editTextPath, editTextStrokePaint)
        }
        canvas.drawPath(editTextPath, editTextBackgroundPaint)
    }

    private fun drawBox(canvas: Canvas) {
        canvas.drawPath(boxPath, boxPaint)
        if (boxStrokeEnabled) {
            canvas.drawPath(boxPath, boxStrokePaint)
        }
    }

    private fun calculateBoxPath() {

        boxPath.reset()
        editTextPath.reset()

        val firstWidth =
            textView.measuredWidth.toFloat() + horizontalPadding.times(2) + viewStrokeSize.times(2)
        val firstHeight = textView.measuredHeight.toFloat() + verticalPadding + viewStrokeSize
        val secondWidth =
            autoCompleteTextView.measuredWidth.toFloat() + horizontalPadding.times(2) + viewStrokeSize.times(
                2
            )
        val secondHeight =
            autoCompleteTextView.measuredHeight.toFloat() + verticalPadding.times(2) + viewStrokeSize
        val cornerDiameter = viewCornerRadius

        // Top - Left Corner
        boxPath.moveTo(cornerDiameter, viewStrokeSize)
        // Top Line
        boxPath.lineTo(firstWidth - cornerDiameter.times(2) - viewStrokeSize, viewStrokeSize)
        // Top - Right Cubic
        boxPath.cubicTo(
            firstWidth,
            viewStrokeSize,
            firstWidth,
            firstHeight + viewStrokeSize,
            firstWidth + cornerDiameter.times(2),
            firstHeight + viewStrokeSize
        )
        // Top Line
        boxPath.lineTo(
            secondWidth - cornerDiameter - viewStrokeSize, firstHeight + viewStrokeSize
        )
        // Top - Right Arc
        boxPath.arcTo(
            secondWidth - cornerDiameter.times(2) - viewStrokeSize,
            firstHeight + viewStrokeSize,
            secondWidth - viewStrokeSize,
            firstHeight + viewStrokeSize + cornerDiameter.times(2),
            270f,
            90f,
            true
        )
        // Bottom - Right Arc
        boxPath.arcTo(
            secondWidth - cornerDiameter.times(2) - viewStrokeSize,
            secondHeight + firstHeight + viewStrokeSize.times(2) - cornerDiameter.times(2),
            secondWidth - viewStrokeSize,
            secondHeight + firstHeight,
            0f,
            90f,
            false
        )
        // Bottom - Left Arc
        boxPath.arcTo(
            viewStrokeSize,
            secondHeight + firstHeight + viewStrokeSize - cornerDiameter.times(2),
            64f,
            secondHeight + firstHeight,
            90f,
            90f,
            false
        )
        // Top - Left Arc
        boxPath.arcTo(
            viewStrokeSize,
            viewStrokeSize,
            cornerDiameter.times(2),
            cornerDiameter.times(2),
            180f,
            90f,
            false
        )

        editTextPath.addRoundRect(
            horizontalPadding + viewStrokeSize,
            firstHeight + viewStrokeSize + verticalPadding,
            secondWidth - horizontalPadding - viewStrokeSize,
            firstHeight + secondHeight - verticalPadding,
            cornerDiameter,
            cornerDiameter,
            Path.Direction.CW
        )
    }

    private fun setAdapter(autoCompleteBoxItems: List<AutoCompleteBoxItem>) {
        val arrayAdapter = ArrayAdapter(context,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            android.R.id.text1,
            autoCompleteBoxItems.map { it.data })

        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.setDropDownBackgroundResource(R.drawable.round_border_tertiary)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedAutoCompleteBoxItem = items[position]
            autoCompleteBoxListener?.onSelectItem(selectedAutoCompleteBoxItem!!)
        }
    }

    data class AutoCompleteBoxItem(val id: Long, val data: String)

    interface AutoCompleteBoxListener {
        fun onSelectItem(autoCompleteBoxItem: AutoCompleteBoxItem)
    }

    fun Float.toPx(resources: Resources): Float = this * resources.displayMetrics.density
}