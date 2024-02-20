package com.github.kr328.clash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.setUUID
import com.github.kr328.clash.design.MainDesign
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.remote.StatusClient
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

class ExternalControlActivity : Activity(), CoroutineScope by MainScope() {

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Handle the results of PropertiesActivity here if necessary.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent.action) {
            Intent.ACTION_VIEW -> handleActionView()
            Intents.ACTION_TOGGLE_CLASH -> toggleClash()
            Intents.ACTION_START_CLASH -> startClash()
            Intents.ACTION_STOP_CLASH -> stopClash()
        }
        finish()
    }

    private fun handleActionView() {
        val uri = intent?.data ?: return
        val url = uri.getQueryParameter("url") ?: return

        launch {
            val uuid = withProfile {
                val type = when (uri.getQueryParameter("type")?.lowercase(Locale.getDefault())) {
                    "url" -> Profile.Type.Url
                    "file" -> Profile.Type.File
                    else -> Profile.Type.Url
                }
                val name = uri.getQueryParameter("name") ?: getString(R.string.new_profile)

                create(type, name).also {
                    patch(it, name, url, 0)
                }
            }
            val intent = PropertiesActivity::class.intent.setUUID(uuid)
            startForResult.launch(intent)
        }
    }

    private fun toggleClash() {
        if (Remote.broadcasts.clashRunning) {
            stopClashServiceWithToast()
        } else {
            startClashServiceWithToast()
        }
    }

    private fun startClash() {
        if (!Remote.broadcasts.clashRunning) {
            startClashServiceWithToast()
        } else {
            showToastIfRunning(R.string.external_control_started)
        }
    }

    private fun stopClash() {
        if (Remote.broadcasts.clashRunning) {
            stopClashServiceWithToast()
        } else {
            showToastIfRunning(R.string.external_control_stopped)
        }
    }

    private fun startClashServiceWithToast() {
        val vpnRequest = startClashService()
        if (vpnRequest != null) {
            showToast(R.string.unable_to_start_vpn)
        } else {
            showToast(R.string.external_control_started)
        }
    }

    private fun stopClashServiceWithToast() {
        stopClashService()
        showToast(R.string.external_control_stopped)
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    private fun showToastIfRunning(messageResId: Int) {
        showToast(messageResId)
    }
}
