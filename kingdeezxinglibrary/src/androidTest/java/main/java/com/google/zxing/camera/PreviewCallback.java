package main.java.com.google.zxing.camera;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class PreviewCallback implements Camera.PreviewCallback {
  public boolean isSuccess;
  private final CameraConfigurationManager configManager;
  private final boolean useOneShotPreviewCallback;
  private MultiFormatReader mMultiFormatReader;
  private List<BarcodeFormat> mFormats=new ArrayList<BarcodeFormat>();
  public static interface ScanResult{
	  public void onScanSuccess(String result, Bitmap bitmap);
	  public void onScanFail();
      public List<BarcodeFormat> getBarCodeFormat();
  };
  private ScanResult scanresult;
  PreviewCallback(CameraConfigurationManager configManager, boolean useOneShotPreviewCallback) {
    this.configManager = configManager;
    this.useOneShotPreviewCallback = useOneShotPreviewCallback;
  }

  public void setScanResult(ScanResult scanresult){
      this.scanresult=scanresult;
      initMultiFormatReader(scanresult.getBarCodeFormat());
  }

    private void initMultiFormatReader(List<BarcodeFormat> mFormats) {
        Map<DecodeHintType,Object> hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
        if(mFormats==null||mFormats.isEmpty()){
            mFormats=new ArrayList<BarcodeFormat>();
            mFormats.add(BarcodeFormat.QR_CODE);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, mFormats);
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
    }

  public void onPreviewFrame(byte[] data, Camera camera) {
    Point cameraResolution = configManager.getCameraResolution();
    if (!useOneShotPreviewCallback) {
      camera.setPreviewCallback(null);
    }
    if(!isSuccess){
    	decode(data,cameraResolution.x,cameraResolution.y);
        CameraManager.get().requestPreviewFrame();
     }
  }

  private void decode(byte[] data,int x,int y){
	  decodeQrCode(data,x,y);
  }

  private void decodeQrCode(byte[] data,int width, int height){
      Result rawResult = null;
      byte[] rotatedData = new byte[data.length];
      for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++)
              rotatedData[x * height + height - y - 1] = data[x + y * width];
      }
      int tmp = width; // Here we are swapping, that's the difference to #11
      width = height;
      height = tmp;
      PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData,width, height);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      try {
          rawResult = mMultiFormatReader.decodeWithState(bitmap);
      } catch (ReaderException re) {
          // continue
      } finally {
    	  mMultiFormatReader.reset();
      }
      if (rawResult != null) {
          isSuccess=true;
          Bitmap bitmapcode=source.renderCroppedGreyscaleBitmap();
          scanresult.onScanSuccess(rawResult.getText(), bitmapcode);
      } else {
          isSuccess=false;
          scanresult.onScanFail();
      }
  }


}
