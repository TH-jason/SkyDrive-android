package com.we.player.player.exo

import android.app.Application
import com.we.player.player.PlayerFactory

/**
 *
 * @Description:
 * @Author: Wisn
 * @CreateDate: 2020/11/14 上午8:54
 */
open class ExoPlayerFactory : PlayerFactory<ExoAPlayer>() {

    override fun createPlayer(app: Application): ExoAPlayer {
       return ExoAPlayer(app)
    }
}