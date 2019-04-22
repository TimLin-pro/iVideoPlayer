package com.android.timlin.ivedioplayer.player.widget

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log

/**
 * Created by linjintian on 2019/4/22.
 */
private const val TAG = "RegionNumberEditText"
class RegionNumberEditText : android.support.v7.widget.AppCompatEditText {
    private var max: Int = 0
    private var min: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 设置输入数字的范围
     *
     * @param maxNum 最大数
     * @param minNum 最小数
     */
    fun setRegion(minNum: Int, maxNum: Int) {
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        this.max = maxNum
        this.min = minNum
    }

    fun setTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var input = s
                if (start >= 0) {
                    try {
                        val num = Integer.parseInt(input.toString())
                        if (num > max) {
                            input = max.toString()//如果大于max，则内容为max
                            setText(input)
                        } else if (num < min) {
                            input = min.toString()//如果小于min,则内容为min
                        }
                        setText(input)
                    } catch (e: NumberFormatException) {
                        Log.e(TAG, "onTextChanged NumberFormatException" , e)
                    }
                    return
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }
}