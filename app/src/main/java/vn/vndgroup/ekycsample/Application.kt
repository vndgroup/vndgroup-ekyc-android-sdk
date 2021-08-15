package vn.vndgroup.ekycsample

import android.app.Application
import vn.vndgroup.ekyc.global.EKYCManager

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        EKYCManager.initialize()
    }
}