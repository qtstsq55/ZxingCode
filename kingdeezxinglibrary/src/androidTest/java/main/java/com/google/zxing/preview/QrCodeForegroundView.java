package main.java.com.google.zxing.preview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

public abstract class QrCodeForegroundView extends View {

    private int ScreenRate;
    private Paint paint;
    private Bitmap resultBitmap;
    protected Resources resources = getResources();
    private Context context;
    protected abstract int getMaskColor();
    protected abstract int getResultColor();
    protected abstract int getFrameColor();
    protected abstract int getFrameLineColor();
    protected abstract int getPointColor();
    protected abstract int getCornerWidth();
    protected abstract int getScanTextColor();
    protected abstract String  getScanText();
    protected abstract int getScanTextLeft();
    protected abstract int getScanTextSize();
    protected abstract int getScanTextTop();
    protected abstract long getInvaliteTime();
    protected abstract Rect getPreviewFramingRect();
    protected abstract String  getScanType();
    protected abstract int  getOpaque();
    protected abstract boolean  isSupportResultPoint();
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    public QrCodeForegroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        float density = context.getResources().getDisplayMetrics().density;
		ScreenRate = (int)(20 * density);
        paint = new Paint();
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }
    @Override
    public void onDraw(Canvas canvas) {
        Rect frame=getPreviewFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paint.setColor(resultBitmap != null ? getResultColor() : getMaskColor());
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom , paint);
        canvas.drawRect(frame.right , frame.top, width, frame.bottom , paint);
        canvas.drawRect(0, frame.bottom , width, height, paint);

        if (resultBitmap != null) {
            paint.setAlpha(getOpaque());
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
        	paint.setColor(getFrameLineColor());
            canvas.drawRect(frame.left, frame.top, frame.right, frame.top, paint);
            canvas.drawRect(frame.left, frame.top, frame.left , frame.bottom, paint);
            canvas.drawRect(frame.right , frame.top, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom , frame.right, frame.bottom, paint);
            paint.setColor(getFrameColor());
            canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,frame.top + getCornerWidth(), paint);
			canvas.drawRect(frame.left, frame.top, frame.left + getCornerWidth(), frame.top+ ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate , frame.top, frame.right  ,frame.top + getCornerWidth(), paint);
			canvas.drawRect(frame.right - getCornerWidth() , frame.top, frame.right  , frame.top+ ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - getCornerWidth() , frame.left+ ScreenRate, frame.bottom , paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate ,frame.left + getCornerWidth(), frame.bottom , paint);
			canvas.drawRect(frame.right - ScreenRate , frame.bottom - getCornerWidth() ,	frame.right , frame.bottom , paint);
			canvas.drawRect(frame.right - getCornerWidth() , frame.bottom - ScreenRate ,frame.right , frame.bottom , paint);
            paint.setTextSize(getScanTextSize());
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(getScanTextColor());
            canvas.drawText(getScanText(), getScanTextLeft(),getScanTextTop(), paint);
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if(isSupportResultPoint()){
	            if (currentPossible.isEmpty()) {
	                lastPossibleResultPoints = null;
	            } else {
	                possibleResultPoints = new HashSet<ResultPoint>(5);
	                lastPossibleResultPoints = currentPossible;
	                paint.setAlpha(getOpaque());
	                paint.setColor(getPointColor());
	                for (ResultPoint point : currentPossible) {
	                    canvas.drawCircle(frame.left + point.getX(), frame.top+ point.getY(), 6.0f, paint);
	                   }
	               }
	            if (currentLast != null) {
	                paint.setAlpha(getOpaque() / 2);
	                paint.setColor(getPointColor());
	                for (ResultPoint point : currentLast) {
	                    canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
	                    }
	                }
            }
            postInvalidateDelayed(getInvaliteTime(), frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }



}

