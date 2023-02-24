package net.edara.sunmiprinterutill.model;



public interface PrinterCallback {
    String getResult();
    void onReturnString(String result);
}
