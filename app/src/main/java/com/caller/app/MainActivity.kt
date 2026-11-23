package com.caller.app

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.caller.app.ui.theme.CallerTheme

class MainActivity : ComponentActivity() {

    private var screeningRoleHeld by mutableStateOf(false)

    private val screeningRoleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshScreeningRole()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshScreeningRole()
        setContent {
            CallerTheme {
                CallerApp(
                    screeningRoleHeld = screeningRoleHeld,
                    onEnableScreening = ::requestScreeningRole
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshScreeningRole()
    }

    private fun refreshScreeningRole() {
        screeningRoleHeld = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java)
                ?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
        } else {
            false
        }
    }

    private fun requestScreeningRole() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

        val roleManager = getSystemService(RoleManager::class.java) ?: return
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            val requestIntent: Intent =
                roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            screeningRoleLauncher.launch(requestIntent)
        } else {
            refreshScreeningRole()
        }
    }
}
