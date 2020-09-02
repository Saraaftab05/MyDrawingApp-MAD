package com.example.mydrawingapp.View;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mydrawingapp.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawingView extends View {

    public static final float TOUCH_TOLERANCE = 10;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private float brushSize, lastBrushSize;
    private boolean erase=false;


    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;


    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.getStrokeWidth();
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);
        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); //event type
        int actionIndex = event.getActionIndex(); // pointer (Finger , mouse)

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(action));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event); //take care of the movement and start drawing things onto the screen
        }

        invalidate();

        return true;
    }

    private void touchMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);
            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                //calcule how far the user moved from the last update.
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to be considered a movement then

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);
                    // store the new cordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }


    public void setDrawingColor(int color) {

        paintLine.setColor(color);
    }

    public int getDrawingColor() {

        return paintLine.getColor();
    }

    public void setLineWidth(int width) {

        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }



    //set brush size
    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        paintLine.setStrokeWidth(brushSize);
    }

    //get and set last brush size
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    //set erase true or false
    public void setErase(boolean isErase){
        erase=isErase;
        if (erase) paintLine.setColor(Color.WHITE);
        else Toast.makeText(getContext(), "Eraser Not working",Toast.LENGTH_SHORT).show();

    }




    public void clear()  {
        pathMap.clear(); // remove all of the paths
     previousPointMap.clear();
     bitmap.eraseColor(Color.WHITE);
     invalidate(); // refresh the screen
 }
     private void touchEnded(int pointerId) {
     Path path = pathMap.get(pointerId); // get the corresponding Path
     bitmapCanvas.drawPath(path, paintLine); // Draw to bitmapCanvas
        path.reset();

    }


    private void touchStarted(float x, float y, int pointerId) {
    Path path; // store the path for given touch
    Point point; // store the last point in path
    if (pathMap.containsKey(pointerId)) {
        path = pathMap.get(pointerId);
        point = previousPointMap.get(pointerId);
    } else  {
        path = new Path();
        pathMap.put(pointerId, path);
        point = new Point();
        previousPointMap.put(pointerId, point);
    }
// move to the cordinates of touch
    path.moveTo(x , y);
    point.x = (int) x;
    point.y = (int) y;



    }








    /**
    private void loadImageFromStorage(String path) {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img =(ImageView)findViewById(R.id.imgPicker);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
**/


}
