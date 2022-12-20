package net.edara.edaracash.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import net.edara.edaracash.R
import net.edara.edaracash.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var isNavigationUpAllowed: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val controller =
            (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController

        setupWithNavController(binding.bottomNavigation, controller)
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.chooseOrderTypeFragment),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.toolBar.setupWithNavController(controller, appBarConfiguration)

        handleNavChangesToTheView(controller)
        onBackPressed(controller)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun onBackPressed(controller: NavController) {
        onBackPressedDispatcher.addCallback {
            if (isNavigationUpAllowed)
                controller.navigateUp()
            else
                finish()
        }
    }


    private fun handleNavChangesToTheView(controller: NavController) {
        controller.addOnDestinationChangedListener { _, dest, _ ->
            val fragmentNavChanges: FragmentNavChanges =
                when (dest.id) {
                    R.id.servicesFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = false,
                            title = "Privet Service",
                            isAppBarAvailable = true,
                        )
                    }
                    R.id.insuranceFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = true,
                            title = "Insurance",
                            isAppBarAvailable = true,
                        )

                    }
                    R.id.resultFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = true,
                            title = "Search Result",
                            isAppBarAvailable = true,
                        )

                    }
                    R.id.editWorkOrderFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = true,
                            title = "Edit Order",
                            isAppBarAvailable = true,
                        )

                    }
                    R.id.paymentFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = true,
                            title = "Payment",
                            isAppBarAvailable = true,

                            )
                    }
                    R.id.invoiceFragment -> {
                        FragmentNavChanges(
                            isBackEnabled = true,
                            title = "Invoice",
                            isAppBarAvailable = true,
                        )

                    }
                    else -> {
                        FragmentNavChanges(
                            title = "Edit Order",
                        )

                    }
                }
            handleAppBarAndBottomNavViewChanges(fragmentNavChanges)
        }
    }

    private fun handleAppBarAndBottomNavViewChanges(fragmentNavChanges: FragmentNavChanges) {

        binding.appBarLayout.visibility =
            if (fragmentNavChanges.isAppBarAvailable) View.VISIBLE else View.GONE
        binding.toolBar.title = fragmentNavChanges.title

        isNavigationUpAllowed = fragmentNavChanges.isBackEnabled
        binding.bottomNavigation.visibility =
            if (fragmentNavChanges.isBottomNavAvailable) View.VISIBLE else View.GONE


    }

    private data class FragmentNavChanges(
        val isBottomNavAvailable: Boolean = false,
        val isAppBarAvailable: Boolean = false,
        val isBackEnabled: Boolean = false,
        var title: String
    )

}

