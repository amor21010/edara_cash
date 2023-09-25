@file:Suppress("DEPRECATION")

package net.edara.edaracash

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.fawry.pos.retailer.connect.FawryConnect
import com.fawry.pos.retailer.connect.model.connection.ConnectionType
import com.fawry.pos.retailer.connect.model.messages.user.UserData
import com.fawry.pos.retailer.connect.model.messages.user.UserType
import com.fawry.pos.retailer.connect.model.payment.PaymentOptionType
import com.fawry.pos.retailer.ipc.IPCConnectivity
import com.fawry.pos.retailer.modelBuilder.sale.CardSale
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.device.printer.AlignEnum
import com.nexgo.oaf.apiv3.device.printer.Printer
import dagger.hilt.android.AndroidEntryPoint
import net.edara.edaracash.databinding.ActivityMainBinding
import net.edara.edaracash.models.Consts.FAWRY_Password
import net.edara.edaracash.models.Consts.FAWRY_USERNAME
import net.edara.edaracash.paxPrint.print.PrintReceipt
import net.edara.edaracash.paymentMethods.fawry.FawryPaymentResult
import net.edara.edaracash.paymentMethods.geidea.GeideaPaymentResult
import net.edara.edaracash.paymentMethods.geidea.isGeideaInstalled
import net.edara.sunmiprinterutill.GeideaPrinterUtil
import javax.inject.Inject


private const val PAYMENT_REQUEST_CODE = 100
private const val RECEIPT_PRINT_REQUEST_CODE = 200

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isNavigationUpAllowed: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var onSuccess: (transitionNo: String) -> Unit
    private lateinit var navController: NavController

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @Inject
    lateinit var geideaPrinterUtil: GeideaPrinterUtil
    lateinit var fawryPrinter: Printer
    private val geideaPackageName = "com.geidea.meeza.smartpos"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController
        setupWithNavController(binding.bottomNavigation, navController)
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.chooseOrderTypeFragment),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        if (!geideaPrinterUtil.aidlUtil.isConnect) geideaPrinterUtil.aidlUtil.connectPrinterService(
            this
        )
        handleNavChangesToTheView(navController)
        onBackPressed(navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PAYMENT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val status = data?.getStringExtra("status")
                    if (status.equals("Approved") || status.equals("Declined")) {
                        val result = data?.getStringExtra("result")
                        try {
                            if (result != null) {
                                val resultPayment = GeideaPaymentResult.fromJson(result)

                                Toast.makeText(
                                    this, resultPayment.responseMessage, Toast.LENGTH_SHORT
                                ).show()
                                if (resultPayment.responseCode in listOf(
                                        "000",
                                        "001",
                                        "003",
                                        "007",
                                        "087",
                                        "089"
                                    )
                                )
                                    onSuccess(resultPayment.transactionRefNo)
                            }
                        } catch (e: Exception) {
                            // Handle exception
                            status?.let {}

                        }
                    } else {

                        val reason = data?.getStringExtra("reason") // reason for payment abort
                        Toast.makeText(this, reason, Toast.LENGTH_LONG).show()

                    }
                }
            }
            RECEIPT_PRINT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val msg = data?.getStringExtra("message")
                    val status = data?.getStringExtra("status")
                    Toast.makeText(this, "status:$status\nmessage:$msg", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun onBackPressed(controller: NavController) {
        onBackPressedDispatcher.addCallback {
            if (isNavigationUpAllowed) controller.navigateUp()
            else finish()
        }
    }

    fun fawryPay(
       amount: Double, onSuccess: (transitionNo: String) -> Unit
    ) {

        dataStore.data.asLiveData().observe(this@MainActivity) { data ->
            val username = data[FAWRY_USERNAME]
            val password = data[FAWRY_Password]
            if (username.isNullOrEmpty()
                || password.isNullOrEmpty()
            ) {
                navController.navigate(R.id.fawryAuthFragment)
                return@observe
            }

            val fawryConnect: FawryConnect = FawryConnect.setup<IPCConnectivity.Builder>(
                ConnectionType.IPC, UserData(
                    username = username,
                    password = password,
                    userType = UserType.MCC
                )
            ).setContext(applicationContext)
                .setConnectionCallBack(FawryConnect.OnConnectionCallBack(onConnected = {
                    Log.d("TAG", "fawryPay: connected")

                }, onDisconnected = {
                    Log.d("TAG", "fawryPay: disconnected")


                }, onFailure = { _, throwable ->
                    Log.d("TAG", "fawryPay: ${throwable?.message}")
                    Toast.makeText(
                        this@MainActivity,
                        "Failed Due To: ${throwable?.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })).connect()!!

            fawryConnect.requestSale<CardSale.Builder>(PaymentOptionType.CARD)
                .setAmount(amount)
                .setCurrency("EGP")
                .setExtras(null)
                .setPrintReceipt(true)
                .setDisplayInvoice(false)
                .setPromoCode(null)
                .setOrderID(null)
                .send(999,
                    FawryConnect.OnTransactionCallBack(
                        onTransactionRequestSuccess = { resultJson ->
                            Log.d("TAG", "fawryPay: $resultJson")
                            val result = FawryPaymentResult.buildFromJson(resultJson)
                            result.body?.fawryReference?.let { reference ->
                                onSuccess(
                                    reference
                                )
                            }

                        }, onTransactionRequestFailure = { errorCode, cause ->
                            Log.d(
                                "TAG",
                                "fawryPay:error code:$errorCode cause: ${cause?.message}"
                            )
                        })
                )
        }

    }

    private fun handleNavChangesToTheView(controller: NavController) {
        controller.addOnDestinationChangedListener { _, dest, _ ->
            val fragmentNavChanges: FragmentNavChanges = when (dest.id) {
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
                R.id.paymentMethodFragment -> {
                    FragmentNavChanges(
                        isBackEnabled = true,
                        title = "Choose Payment Methode",
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

    fun requestPayment(
        price: Float, onSuccess: (transitionNo: String) -> Unit
    ) {


        if (isGeideaInstalled(this, geideaPackageName)) {
            val paymentIntent = Intent()
            paymentIntent.action = "com.geidea.meeza.smartpos.PURCHASE"
            paymentIntent.putExtra(Intent.EXTRA_TEXT, price.toString())
            paymentIntent.putExtra("CUSTOMER_RECEIPT_FLAG", false)
            paymentIntent.putExtra("HOME_BUTTON_STATUS", true)

            paymentIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(paymentIntent, null)
            this.onSuccess = onSuccess
            startActivityForResult(shareIntent, PAYMENT_REQUEST_CODE)

        } else {
            Toast.makeText(
                this, "Meeza app is not installed to process transaction", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fawryPrint(view: View) {
        try {
            val deviceEngine = APIProxy.getDeviceEngine(this)
            fawryPrinter = deviceEngine.printer
            fawryPrinter.setTypeface(Typeface.SANS_SERIF)
            val bitmap = geideaPrinterUtil.scaleImage(geideaPrinterUtil.convertViewToBitmap(view))
            fawryPrinter.appendImage(bitmap, AlignEnum.CENTER)
            fawryPrinter.startPrint(true) {
                Log.d("fawryPrint", "fawryPrint: $it")
            }
        } catch (e: LinkageError) {

            Log.d("TAG", "fawryPrint: ${e.message}")
            paxPrint(view)

        } catch (e: Exception) {
            Log.d("fawryPrint", "fawryPrint: ${e.message}")
            paxPrint(view)

        }
    }

    private data class FragmentNavChanges(
        val isBottomNavAvailable: Boolean = false,
        val isAppBarAvailable: Boolean = false,
        val isBackEnabled: Boolean = false,
        var title: String
    )

    fun printReceipt(view: View) {

        if (isGeideaInstalled(this, geideaPackageName))
            geideaPrinterUtil.sendViewToPrinter(view)
        else {
            try {
                fawryPrint(view)
                view.requestLayout()
                view.invalidate()

            } catch (e: Exception) {
                Toast.makeText(this, "Printer Not Found", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "printReceipt: ${e.message}")

            }
        }

    }

    private fun paxPrint(view: View) {
        try {
            val bitmap = geideaPrinterUtil.scaleImage(geideaPrinterUtil.convertViewToBitmap(view))
            PrintReceipt(this).printReceipt(bitmap, application as EdaraCashApplication)
        } catch (e: java.lang.Error) {
            Toast.makeText(this, "Printer Not Found", Toast.LENGTH_SHORT).show()
        }
    }

}

