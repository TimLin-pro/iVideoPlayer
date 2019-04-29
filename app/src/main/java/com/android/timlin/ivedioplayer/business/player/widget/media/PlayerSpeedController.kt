package com.android.timlin.ivedioplayer.business.player.widget.media

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.android.timlin.ivedioplayer.R
import com.android.timlin.ivedioplayer.common.utils.ScreenUtil
import kotlinx.android.synthetic.main.layout_play_speed.view.*

private const val MAX_SPEED = 4.00f
private const val MIN_SPEED = 0.25f
private const val INTERVAL = 0.05f
private const val TAG = "PlayerSpeedController"

/**
 * Created by linjintian on 2019/4/22.
 */
class PlayerSpeedController(speedController: IPlaySpeedController, popupContainer: View) {
    private var mPopup: PopupWindow = PopupWindow(popupContainer.context)
    private var mSpeedController: IPlaySpeedController = speedController
    private var mContainer = popupContainer

    init {
        val contentView = LayoutInflater.from(popupContainer.context).inflate(R.layout.layout_play_speed, null, false)
        mPopup.contentView = contentView
        mPopup.isOutsideTouchable = true//点击外部区域时，隐藏
        contentView.et_play_speed.setText(100.toString())
        initListener(contentView)
//        initPlayerSpeedEt(contentView)
    }

    private fun initListener(contentView: View) {
        contentView.iv_close.setOnClickListener {
            hide()
        }
        contentView.iv_decrease_speed.setOnClickListener {
            decreaseSpeed()
        }
        contentView.iv_increase_speed.setOnClickListener {
            increaseSpeed()
        }
    }

    private fun initPlayerSpeedEt(contentView: View) {
        contentView.et_play_speed.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged")
                mSpeedController.setSpeed(s.toString().toFloat())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }


    fun show() {
        if (!mPopup.isShowing) {
            mPopup.showAtLocation(mContainer, Gravity.BOTTOM or Gravity.RIGHT, ScreenUtil.dip2px(12), ScreenUtil.dip2px(12))
        }
    }

    fun hide() {
        if (mPopup.isShowing) {
            mPopup.dismiss()
        }
    }

    private fun decreaseSpeed() {
        val currentSpeed = mSpeedController.getSpeed()
        if (currentSpeed <= MIN_SPEED) {
            return
        }
        val speed = currentSpeed - INTERVAL
        mSpeedController.setSpeed(speed)
        updateSpeed(speed)
    }

    private fun increaseSpeed() {
        //接口有问题，传入的参数并没有使用
        val currentSpeed = mSpeedController.getSpeed()
        if (currentSpeed >= MAX_SPEED) {
            return
        }
        val speed = currentSpeed + INTERVAL
        mSpeedController.setSpeed(speed)
        updateSpeed(speed)
    }

    @SuppressLint("SetTextI18n")
    private fun updateSpeed(speed: Float) {
        mPopup.contentView.et_play_speed.setText(Math.round(speed * 100).toString())
    }
}