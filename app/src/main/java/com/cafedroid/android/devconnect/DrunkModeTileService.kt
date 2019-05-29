package com.cafedroid.android.devconnect

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import com.cafedroid.android.devconnect.utils.PrefConfig

@RequiresApi(Build.VERSION_CODES.N)
class DrunkModeTileService : TileService() {

    private lateinit var prefConfig: PrefConfig
    private var isActive: Boolean = false

    override fun onCreate() {
        super.onCreate()
        prefConfig = PrefConfig.getInstance(this)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        this.qsTile.state = Tile.STATE_INACTIVE
    }

    override fun onClick() {
        super.onClick()
        if (isActive)
            this.qsTile.state = Tile.STATE_INACTIVE
        else this.qsTile.state = Tile.STATE_ACTIVE

        isActive = !isActive
        prefConfig.saveBoolean(PrefConfig.DRUNK_MODE_ACTIVE, isActive)
        this.qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        this.qsTile.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()
        isActive = qsTile.state == Tile.STATE_ACTIVE
        this.qsTile.updateTile()
    }

}
