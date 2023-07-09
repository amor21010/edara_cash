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
import net.edara.edaracash.paymentMethods.fawry.FawryPaymentResult
import net.edara.edaracash.paymentMethods.geidea.GeideaPaymentResult
import net.edara.edaracash.paymentMethods.geidea.isGeideaInstalled
import net.edara.paxprinter.PaxPrinter
import net.edara.sunmiprinterutill.PrinterUtil
import javax.inject.Inject


private const val PAYMENT_REQUEST_CODE = 100
private const val RECEIPT_PRINT_REQUEST_CODE = 200

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isNavigationUpAllowed: Boolean = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var onSuccess: (transitionNo: String) -> Unit

    @Inject
    lateinit var printer: PrinterUtil
    lateinit var fawryPrinter: Printer
    private val geideaPackageName = "com.geidea.meeza.smartpos"
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
        if (!printer.aidlUtil.isConnect) printer.aidlUtil.connectPrinterService(this)
        handleNavChangesToTheView(controller)
        onBackPressed(controller)
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
        val fawryConnect: FawryConnect = FawryConnect.setup<IPCConnectivity.Builder>(
            ConnectionType.IPC, UserData(
                username = "302302", password = "135792", userType = UserType.MCC
            )
        ).setContext(applicationContext)
            .setConnectionCallBack(FawryConnect.OnConnectionCallBack(onConnected = {
                Log.d("TAG", "fawryPay: connected")

            }, onDisconnected = {
                Log.d("TAG", "fawryPay: disconnected")


            }, onFailure = { _, throwable ->
                Log.d("TAG", "fawryPay: ${throwable?.message}")
                Toast.makeText(this, "Failed Due To: ${throwable?.message}", Toast.LENGTH_SHORT)
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
                        result.body?.fawryReference?.let { reference -> onSuccess(reference) }

                    }, onTransactionRequestFailure = { errorCode, cause ->
                        Log.d("TAG", "fawryPay:error code:$errorCode cause: ${cause?.message}")
                    })
            )
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
        val deviceEngine = APIProxy.getDeviceEngine(this)
        fawryPrinter = deviceEngine.printer
        fawryPrinter.setTypeface(Typeface.SANS_SERIF)
        val bitmap = printer.scaleImage(printer.convertViewToBitmap(view))
        fawryPrinter.appendImage(bitmap, AlignEnum.CENTER)
        fawryPrinter.startPrint(true) {
            Log.d("fawryPrint", "fawryPrint: $it")
        }
    }

    private data class FragmentNavChanges(
        val isBottomNavAvailable: Boolean = false,
        val isAppBarAvailable: Boolean = false,
        val isBackEnabled: Boolean = false,
        var title: String
    )

    fun printReceipt(view: View) {
        try {
            if (isGeideaInstalled(this, geideaPackageName))
                printer.sendViewToPrinter(view)
            else {
                try {
                    fawryPrint(view)
                } catch (e: Exception) {
                    val bitmap = printer.scaleImage(printer.convertViewToBitmap(view))
                    PaxPrinter.instance?.printBitmap(bitmap)
                    PaxPrinter.instance?.start()
                }
            }
            view.requestLayout()
            view.invalidate()
        } catch (e: Exception) {
            Toast.makeText(this, "Printer Not Found", Toast.LENGTH_SHORT).show()
        }
    }

}

