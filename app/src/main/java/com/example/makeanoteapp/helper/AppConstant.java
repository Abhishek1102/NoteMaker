package com.example.makeanoteapp.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.makeanoteapp.R;

public class AppConstant {

    public static String IS_DARKMODE_ON = "is_darkmode_on";
    public static String IS_LIST_SELECTED = "is_list_selected";

    public static ProgressDialog dialog;

    public static boolean isOnline(final Context ctx) {
        if (ctx != null) {
            final ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final NetworkInfo ni = cm.getActiveNetworkInfo();
                    Network activeNetwork = cm.getActiveNetwork();
                    NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
                    if (caps != null) {
                        boolean vpnInUse = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                        if (vpnInUse) {
                            Toast.makeText(ctx, ctx.getString(R.string.vpn), Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            if (ni != null) {
                                if (ni.isConnectedOrConnecting()) {
                                    return true;
                                } else {
                                    Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            } else {
                                Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_VPN);
                    if (networkInfo != null) {
                        if (networkInfo.isConnectedOrConnecting()) {
                            Toast.makeText(ctx, "Please disconnect VPN and try again.", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            final NetworkInfo ni = cm.getActiveNetworkInfo();
                            if (ni != null) {
                                if (ni.isConnectedOrConnecting()) {
                                    return true;
                                } else {
                                    Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            } else {
                                Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static void showProgressDialog(Context context) {
        dialog = new ProgressDialog(context);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setContentView(R.layout.custom_progress);
    }

    public static void hideProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert methodManager != null && view != null;
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

}