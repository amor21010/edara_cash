package net.edara.edaracash.paxPrint.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.pax.gl.page.IPage;
import com.pax.gl.page.PaxGLPage;

import net.edara.edaracash.EdaraCashApplication;

public class PrintReceipt {
    private Bitmap bitmap;

    private Context context;
    private Handler handler = new Handler();
    AlertDialog.Builder alertDialog;

    public PrintReceipt(Context context) {
        this.context = context;
    }

    public void printReceipt(Bitmap viewBitmap, EdaraCashApplication application) {
        bitmap = viewBitmap;

        if (viewBitmap == null) {
            return;
        }

        try {
            generateInvoice(application);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void generateInvoice(EdaraCashApplication application) {


        try {
            PaxGLPage paxGLPage = PaxGLPage.getInstance(context);
            IPage page = paxGLPage.createPage();
            page.adjustLineSpace(-9);
            page.addLine().addUnit(bitmap, IPage.EAlign.CENTER);
            page.addLine().addUnit("\n", 7);
            bitmap = paxGLPage.pageToBitmap(page, 384);
            printReceipt(application);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printReceipt(EdaraCashApplication application) {
        Log.i("printReceipt", Build.MANUFACTURER.toUpperCase() + '\n' +
                Build.BRAND.toUpperCase() + '\n' +
                Build.DEVICE.toUpperCase() + '\n' +
                Build.MODEL.toUpperCase() + '\n' +
                Build.PRODUCT.toUpperCase() + '\n'
        );

        if ("PAX".equalsIgnoreCase(Build.BRAND) || "PAX".equalsIgnoreCase(Build.MANUFACTURER)) { //case of pax device
            try {

                new Thread(() -> {
                    PaxPrinter paxPrinter = PaxPrinter.getInstance(application);
                    paxPrinter.init();
                    paxPrinter.printBitmap(bitmap);
                    onShowMessage(paxPrinter.start(), application);
                }).start();


            } catch (RuntimeException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        } else { //for other devices use bluetooth print
            Log.i("printReceipt", "not pax\n");

            Toast.makeText(context, "No Printer Found", Toast.LENGTH_LONG).show();
        }
    }

    private void onShowMessage(final String message, EdaraCashApplication application) {
        handler.post(() -> {
            if (!message.equals("Success")) {
                if (alertDialog == null)
                    alertDialog = new AlertDialog.Builder(context);
                else
                    alertDialog.create().dismiss();
                alertDialog.setTitle("Something went wrong");
                alertDialog.setMessage(message);
                alertDialog.setPositiveButton("Try again",
                        (dialog, which) -> {
                            printReceipt(application);
                            dialog.cancel();
                        });
                alertDialog.setNegativeButton("Cancel",
                        (dialog, which) -> {
                            dialog.cancel();
                        });
                alertDialog.create().show();
            } else {
                Toast.makeText(application.getApplicationContext(), "printing", Toast.LENGTH_SHORT).show();
            }

        });

    }
}
