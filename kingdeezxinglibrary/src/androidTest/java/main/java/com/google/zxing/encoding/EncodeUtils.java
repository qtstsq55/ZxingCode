package main.java.com.google.zxing.encoding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 
 * @todo:生成二维码
 * @author:hg_liuzl@163.com
 * @date:2014年7月7日 上午9:48:27
 */
public class EncodeUtils {
	/**二维码的边长**/
	public static final int QR_LENGTH = 300;
	
	/**设置二维码的附加logo*/
	public static final int QR_LOGO = 0;
	
	private static EncodeUtils instance = null;
	
	public synchronized static EncodeUtils getInstance(){
		if(null == instance){
			instance = new EncodeUtils();
		}
		return instance;
	}
	
	
	/**
	 * @todo:默认边长为300的二维码图片
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日 上午9:48:49
	 */
	public Bitmap createQRImage(String url) {
		return createImage(url,QR_LENGTH,QR_LOGO,null);
	}
	
	/**
	 * @todo:边长为300的带logo,二维码图片，
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日
	 */
	public Bitmap createQRImage(String url,int drawableId,Context context)
	{
		return createImage(url,QR_LENGTH,drawableId,context);
	}
	
	/**
	 * @todo:自定义边长的二维码图片
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日
	 */
	public Bitmap createQRImage(String url,int length)
	{
		return createImage(url,length,QR_LOGO,null);
	}
	
	/**
	 * @todo:自定义边长的二维码图片且带图片logo，
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日
	 */
	public Bitmap createQRImage(String url,int length,int drawableId,Context context)
	{
		return createImage(url,length,drawableId,context);
	}
	

	/**
	 * 
	 * @todo:转换二维码
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日 上午10:03:17
	 */
	private Bitmap createImage(String url,int length,int drawableId,Context context) {
		Bitmap QR_bitmap = null;
		int informationColor = 0xff000000;//有用信息点颜色，固定黑色
		try {
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//编码格式
			//注：设置纠错级别(L 7%~M 15%~Q 25%~H 30%),纠错级别越高，黑色点就越密，解码成功率越高
			//若要在二维码图片上加logo，就要把纠错级别设置的高些
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, length, length, hints);
			int[] pixels = new int[length * length];
			boolean isFirstPoint = true;
			int startX = 0;
			int startY = 0;
			// 下面这里按照二维码的算法，逐个生成二维码的图片，两个for循环是图片横列扫描的结果
			for (int y = 0; y < length; y++) {
				for (int x = 0; x < length; x++) {
					if (bitMatrix.get(x, y)) {
						if(isFirstPoint){
							isFirstPoint = false;
							startX = x;
							startY = y;
						}
						pixels[y * length + x] = informationColor;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, length, 0, 0, length, length);
			
			if(startX <= 0 || startY <= 0){
				return bitmap;
			}
			
			int width = length - startX * 2;//去除无用边框
			int height = length - startY * 2;
			QR_bitmap = Bitmap.createBitmap(bitmap, startX, startY, width, height);
			if(QR_LOGO != drawableId)
				QR_bitmap = drawLogoQRImage(context,QR_bitmap,drawableId);
			
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return QR_bitmap;
	}
	

	/**
	 * 
	 * @todo:绘制带有logo的二维码
	 * @author:hg_liuzl@163.com
	 * @date:2014年7月7日 上午10:02:53
	 * @params:@return
	 */
	private Bitmap drawLogoQRImage(Context context,Bitmap backBitmap, int drawableId){
		if(backBitmap == null){
			return null;
		}
		Bitmap resultBitmap = Bitmap.createBitmap(backBitmap.getWidth(), backBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Bitmap logoBitmap = ((BitmapDrawable)context.getResources().getDrawable(drawableId)).getBitmap();
		int backBitmapWidth = backBitmap.getWidth();
		int backBitmapHeight = backBitmap.getHeight();
		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(backBitmap, 0, 0, null);//画背景
		canvas.drawBitmap(logoBitmap, (backBitmapWidth - logoBitmap.getWidth())/2, (backBitmapHeight - logoBitmap.getHeight())/2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);  
		canvas.restore();
		return resultBitmap;
	}

}
