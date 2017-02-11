package info.puzz.a10000sentences.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import temp.DBG;

public final class PermissionRequester {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3672178;

    private final Callback callback;

    private final String permission;

    public interface Callback {
        void onGranted();
        void onRejected();
    }

    public PermissionRequester(Activity activity, Callback callback, String permission) {
        this.callback = callback != null ? callback : new Callback() {
            @Override
            public void onGranted() {}

            @Override
            public void onRejected() {}
        };
        this.permission = permission;
    }

    /**
     * Note, the activity must implement the {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * and call {@link #onRequestPermissionsResult(int, String[], int[])} in it.
     */
    private void checkPermissionIfNeeded(Activity activity) {
        // Here, thisActivity is the current activity
        if (isGranted(activity)) {
            callback.onGranted();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                DBG.todo("Show rationale");
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    public boolean isGranted(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onGranted();
            } else {
                callback.onRejected();
            }
            return;
        }
    }
}
