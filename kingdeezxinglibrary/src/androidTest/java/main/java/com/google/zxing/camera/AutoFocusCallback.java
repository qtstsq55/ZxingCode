package main.java.com.google.zxing.camera;

import android.hardware.Camera;
import android.os.Handler;

public  class AutoFocusCallback implements Camera.AutoFocusCallback {
  private static final long AUTOFOCUS_INTERVAL_MS = 1000L;
  private Runnable doAutoFocus = new Runnable() {
      public void run() {
         CameraManager.get().requestAutoFocus();
      }
  };
  private Handler autoFocusHandler=new Handler();

  public void onAutoFocus(boolean success, Camera camera) {
	  autoFocusHandler.postDelayed(doAutoFocus, AUTOFOCUS_INTERVAL_MS);
  }

}
