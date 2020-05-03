package com.vincentz1911.drawandguess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.vincentz1911.drawandguess.Statics.PAINT;

//TODO PAINT BUCKET FILL (see below)
public class DrawingView extends View {

    private final Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Path mPath;
    private Paint mBitmapPaint;
    private final Context context;
    private final Paint circlePaint;
    private final Path cPath;
    private float mX, mY;
    private int circleAlpha = 100;
    private Timer circleTimer;

    public DrawingView(Context c) {
        super(c);
        context = c;
        mPaint = PAINT;
        mPath = new Path();
        cPath = new Path();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(PAINT.getColor());
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(PAINT.getStrokeWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int wOld, int hOld) {
        super.onSizeChanged(w, h, wOld, hOld);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(cPath, circlePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;

                if (circleTimer != null) circleTimer.cancel();
                circleAlpha = 255;
                cPath.reset();
                cPath.addCircle(mX, mY, mPaint.getStrokeWidth() + 10, Path.Direction.CW);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                cPath.reset();
                cPath.addCircle(mX, mY, mPaint.getStrokeWidth() + 10, Path.Direction.CW);

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);

                paths.add(mPath);
                mPath.reset();

                if (circleAlpha > 32) FadeCircle();
                else circleTimer.cancel();

                invalidate();
                break;
        }
        return true;
    }

    //TODO SAVE PATHS
    private final List<Path> paths = new ArrayList<>();

    private void FadeCircle() {
        circleTimer = new Timer();
        circleTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                circleAlpha = circleAlpha > 40 ? circleAlpha - 10 : 40;
                circlePaint.setColor(PAINT.getColor());
                circlePaint.setAlpha(circleAlpha);
                circlePaint.setStrokeWidth(PAINT.getStrokeWidth());

                ((Activity) context).runOnUiThread(() -> {
                    cPath.reset();
                    cPath.addCircle(mX, mY, mPaint.getStrokeWidth() + 10,
                            Path.Direction.CW);
                    invalidate();
                });
            }
        }, 0, 100);
    }
}

/*
private void FloodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor){
Queue<Point> q = new LinkedList<Point>();
q.add(pt);
while (q.size() > 0) {
    Point n = q.poll();
    if (bmp.getPixel(n.x, n.y) != targetColor)
        continue;

    Point w = n, e = new Point(n.x + 1, n.y);
    while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
        bmp.setPixel(w.x, w.y, replacementColor);
        if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
            q.add(new Point(w.x, w.y - 1));
        if ((w.y < bmp.getHeight() - 1)
                && (bmp.getPixel(w.x, w.y + 1) == targetColor))
            q.add(new Point(w.x, w.y + 1));
        w.x--;
    }
    while ((e.x < bmp.getWidth() - 1)
            && (bmp.getPixel(e.x, e.y) == targetColor)) {
        bmp.setPixel(e.x, e.y, replacementColor);

        if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
            q.add(new Point(e.x, e.y - 1));
        if ((e.y < bmp.getHeight() - 1)
                && (bmp.getPixel(e.x, e.y + 1) == targetColor))
            q.add(new Point(e.x, e.y + 1));
        e.x++;
    }
}}
 */