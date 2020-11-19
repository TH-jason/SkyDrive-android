package com.we.player.controller

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import com.blankj.utilcode.util.LogUtils
import com.we.player.view.MediaPlayerController

/**
 *
 * @Description:
 * @Author: Wisn
 * @CreateDate: 2020/11/12 下午7:55
 */
abstract class BaseViewController : FrameLayout, IViewController {
    val TAG: String = "BaseViewController"
    var iviewItemControllers: MutableList<IViewItemController> = arrayListOf()
    var IGestureViewItemControllers: MutableList<IViewItemController> = arrayListOf()
    var fadeout: Runnable = object : Runnable {
        override fun run() {
            hideController()
        }
    }
    var runProgress: Runnable = object : Runnable {
        override fun run() {
            LogUtils.d(TAG, "runProgress!!! ${iviewItemControllers.size} ")

            iviewItemControllers.forEach {
                if (mediaPlayerController != null) {
                    LogUtils.d(TAG, "runProgress ${mediaPlayerController!!.getDuration()}, ${mediaPlayerController!!.getCurrentPosition()}")
                    it.setProgress(mediaPlayerController!!.getDuration(), mediaPlayerController!!.getCurrentPosition())
                }
            }
            if (mediaPlayerController!!.isPlaying()) {
                postDelayed(this, mediaPlayerController!!.getRefreshTime())
            }
        }
    }

    private val mShowAnim: Animation by lazy {
        var show = AlphaAnimation(0f, 1f)
        show.duration = 300
        show
    }

    private val mHideAnim: Animation by lazy {
        var show = AlphaAnimation(1f, 0f)
        show.duration = 300
        show
    }
    var islock: Boolean = false
    var isRunProgress: Boolean = false
    var wrapController: WrapController? = null
    var mediaPlayerController: MediaPlayerController? = null
        set(value) {
            field = value
            if (value != null) {
                wrapController = WrapController(value, this)
                this.iviewItemControllers.forEach {
                    it.attach(wrapController)
                }
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true)
    }

    override fun setPlayStatus(status: Int) {

        this.iviewItemControllers.forEach {
            it.onPlayStateChanged(status)
        }
    }

    override fun hideController() {
        this.iviewItemControllers.forEach {
            it.onVisibilityChanged(false, mHideAnim)
        }
    }

    override fun showController() {
        this.iviewItemControllers.forEach {
            it.onVisibilityChanged(true, mShowAnim)
        }
    }

    override fun setLocked(isLock: Boolean) {
        this.islock = islock
        this.iviewItemControllers.forEach {
            it.onLockStateChanged(isLock)
        }
    }

    override fun isLocked(): Boolean {
        return islock
    }


    override fun startProgress() {
        LogUtils.d(TAG, "startProgress")
        if (!isRunProgress) {
            isRunProgress = true
            post(runProgress)
        }
    }

    override fun stopProgress() {
        isRunProgress = false
        removeCallbacks(runProgress)
    }

    override fun startTimeFade() {
        stopTimeFade()
        postDelayed(fadeout, 3800)
    }

    override fun stopTimeFade() {
        removeCallbacks(fadeout)
    }

    override fun hasCutout(): Boolean {
        return false
    }

    override fun getCutoutHeight(): Int {
        return 0
    }

    fun addIViewItemControllerList(isRemoveAll: Boolean, itemControllerlist: MutableList<IViewItemController>) {
        if (isRemoveAll) {
            this.iviewItemControllers.forEach {
                removeView(it.getView())
            }
            this.iviewItemControllers.clear()
            this.IGestureViewItemControllers.clear()
        }
        this.iviewItemControllers.addAll(itemControllerlist)
        this.iviewItemControllers.forEach {
            addView(it.getView())
            it.attach(wrapController)
            if (it is IGestureViewItemController) {
                IGestureViewItemControllers.add(it)
            }
        }
    }

    fun addIViewItemControllerOne(iviewItemController: IViewItemController) {
        this.iviewItemControllers.add(iviewItemController)
        removeView(iviewItemController.getView())
        addView(iviewItemController.getView())
        iviewItemController.attach(wrapController)
        if (iviewItemController is IGestureViewItemController) {
            IGestureViewItemControllers.add(iviewItemController)
        }
    }

}