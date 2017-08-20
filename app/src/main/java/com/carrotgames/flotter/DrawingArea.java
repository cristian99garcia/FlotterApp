package com.carrotgames.flotter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.carrotgames.flotter.pojo.Funcion;

import java.util.ArrayList;

/**
 * Created by cristian on 06/10/16.
 */

public class DrawingArea extends View {

    private Paint paint;
    private ArrayList<Funcion> funs;
    private float startX = 0f;
    private float startY = 0f;
    private float lastX;
    private float lastY;
    private float maxX = 10f;
    private float maxY = 10f;
    private boolean reseted = true;
    private float[] lastPoint = new float[] { 0f, 0f };
    private float[] draggingPoint = new float[] { 0f, 0f };
    private int actionBarHeight = 0;
    private AppCompatActivity activity;
    private boolean calculando = false;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.f;

    public DrawingArea(Context context) {
        super(context);

        gestureDetector = new GestureDetector(context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        paint = new Paint();
        funs = new ArrayList<>();

        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

        startY = -actionBarHeight;
        activity = (AppCompatActivity)context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.makeFunctionsPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.drawGrid(canvas);
        this.drawAxes(canvas);
        this.drawNumbers(canvas);
        this.drawFunctions(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case (MotionEvent.ACTION_DOWN):
                this.draggingPoint = new float[] { event.getX(), event.getY() };
                this.lastPoint = new float[] { event.getX(), event.getY() };
                break;

            case (MotionEvent.ACTION_UP):
                // FIXME: Evitar calcular cuando hay una doble pulsación

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        calculando = false;  // Si se estaba calculando antes, se detendrá
                        makeFunctionsPoints();

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update();
                            }
                        });
                    }
                });

                t.start();
                break;

            case (MotionEvent.ACTION_MOVE):
                if (Math.abs(event.getX() - lastPoint[0]) > 10 || Math.abs(event.getY() - lastPoint[1]) > 10) {
                    startX += event.getX() - lastPoint[0];
                    startY += event.getY() - lastPoint[1];

                    lastPoint[0] = event.getX();
                    lastPoint[1] = event.getY();
                    update();
                }

                break;
        }

        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void drawGrid(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();

        paint.setStrokeWidth(1f);
        paint.setColor(Color.LTGRAY);

        int factor = 1;
        if (mScaleFactor < .2f) {
            factor = 10;
        } else if (mScaleFactor < .3f) {
            factor = 5;
        } else if (mScaleFactor < .5f) {
            factor = 2;
        }

        float[] point;
        int lastLine = 0;

        point = this.getRealPoint(lastLine, 0);
        while (point[0] > 0) {
            lastLine -= factor;
            point = this.getRealPoint(lastLine, 0);
            canvas.drawLine(point[0], actionBarHeight, point[0], height, paint);
        }

        lastLine = 0;
        point = this.getRealPoint(lastLine, 0);
        while (point[0] < getWidth()) {
            lastLine += factor;
            point = this.getRealPoint(lastLine, 0);
            canvas.drawLine(point[0], actionBarHeight, point[0], height, paint);
        }

        lastLine = 0;
        point = this.getRealPoint(0, lastLine);
        while (point[1] < getHeight()) {
            lastLine -= factor;
            point = this.getRealPoint(0, lastLine);
            canvas.drawLine(0, point[1], width, point[1], paint);
        }

        while (point[1] > 0) {
            lastLine += factor;
            point = this.getRealPoint(0, lastLine);
            canvas.drawLine(0, point[1], width, point[1], paint);
        }
    }

    private void drawAxes(Canvas canvas) {
        float[] rPoint = this.getRealStartPoint();
        float width = getWidth();
        float height = getHeight();

        paint.setStrokeWidth(2f);
        paint.setColor(Color.GRAY);

        canvas.drawLine(0, rPoint[1] + actionBarHeight, width, rPoint[1] + actionBarHeight, paint);  // X axe
        canvas.drawLine(rPoint[0], actionBarHeight, rPoint[0], height, paint);   // Y axe
    }

    private void drawNumbers(Canvas canvas) {
        float[] rPoint = this.getFalsePoint(0, actionBarHeight);
        int minX = (int)rPoint[0] - 1;
        int minY = (int)rPoint[1] - 1;

        float[] fPoint = this.getFalsePoint(getWidth(), getHeight());
        int maxX = (int)fPoint[0] + 1;
        int maxY = (int)fPoint[1] + 1;

        int fsize = 50;
        int space = 5;
        float x, y;
        float[] p;

        p = getRealPoint(0, 0);
        x = p[0] + space;
        if (x < space)
            x = space;
        else if (x > getWidth() - space - fsize)
            x = getWidth() - space - fsize;

        y = p[1] + fsize + space;
        if (y < actionBarHeight + space + fsize)
            y = actionBarHeight + space + fsize;
        else if (y > getHeight() - space)
            y = getHeight() - space;

        paint.setColor(Color.BLACK);
        paint.setTextSize(fsize);

        for (int i = minX; i <= maxX; i++) {
            if (i == 0) {
                continue;
            }

            p = getRealPoint(i, 0);
            canvas.drawText(""+i, p[0] + space, y, paint);
        }

        for (int i = (maxY*-1); i <= (minY*-1); i++) {
            if (i == 0) {
                continue;
            }

            p = getRealPoint(0, i);
            canvas.drawText(""+i, x, p[1] + space, paint);
        }
    }

    private void drawFunctions(Canvas canvas) {
        this.reset();
        paint.setStrokeWidth(5f);

        float[] before;
        float[] actual;

        for (Funcion fun: funs) {
            paint.setColor(fun.getColor());

            for (int i = 0; i < fun.puntos.size(); i++) {
                if (i > 0) {
                    before = fun.puntos.get(i - 1);
                } else {
                    // No es importante, solo para evitar el warning
                    before = new float[] { 0f, 0f };
                }
                actual = fun.puntos.get(i);
                if (i > 0 && (this.getPointInScreen(before[0], before[1])) || this.getPointInScreen(actual[0], actual[1])) {
                    this.drawLineTo(canvas, actual[0], actual[1]);
                } else if (i < fun.puntos.size() - 1) {
                    float[] nPoint = fun.puntos.get(i + 1);
                    if (this.getPointInScreen(nPoint[0], nPoint[1])) {
                        this.drawLineTo(canvas, actual[0], actual[1]);
                    }
                } else {
                    this.setLastPointTo(actual[0], actual[1]);
                }
            }
            this.reset();
        }
    }

    private void reset() {
        this.reseted = true;
    }

    private boolean drawLineTo(Canvas canvas, float x, float y) {
        float lastPoint[] = this.getRealPoint(lastX, lastY);
        float newPoint[] = this.getRealPoint(x, y);
        float tolerance = 2f / mScaleFactor;
        if (tolerance < 2f) {
            tolerance = 2;
        }

        if (!this.reseted && this.getDistance(new float[] { x, y }, new float[] { lastX, lastY }) < tolerance) {
            canvas.drawLine(lastPoint[0], lastPoint[1], newPoint[0], newPoint[1], paint);
        }

        this.setLastPointTo(x, y);
        return (this.getRealPointInScreen(lastPoint[0], lastPoint[1]));  // El último punto queda fuera de la pantalla
    }

    public float getDistance(float[] pointA, float[] pointB) {
        return (float)Math.sqrt(Math.pow(pointB[0] - pointA[0], 2) + Math.pow(pointB[1] - pointA[1], 2));
    }

    public boolean getRealPointInScreen(float x, float y) {
        float width = getWidth();
        float height = getHeight();
        float tolerance = 10f;

        return ((x >= -tolerance && x <= width + tolerance) && (y > -tolerance && y < height + tolerance));
    }

    public boolean getPointInScreen(float x, float y) {
        float[] point = getRealPoint(x, y);
        return this.getRealPointInScreen(point[0], point[1]);
    }

    private void setLastPointTo(float x, float y) {
        this.lastX = x;
        this.lastY = y;
        this.reseted = false;
    }

    private float[] getRealPoint(float x, float y) {
        // Devuelve las coordenadas de un pixel a partir de un punto en el sistema de ejes cartesianos
        float unitX = Math.max(getWidth(), getHeight()) / maxX;
        float unitY = Math.max(getWidth(), getHeight()) / maxY;
        float[] startPoint = this.getRealStartPoint();

        return new float[]{
                startPoint[0] + unitX * x * mScaleFactor,
                (startPoint[1] - unitY * y * mScaleFactor) + actionBarHeight
        };
    }

    private float[] getFalsePoint(float x, float y) {
        // Devuelve el punto en el sistema de ejes cartesianos a partir las coordenadas de un pixel

        float unitX = Math.max(getWidth(), getHeight()) / maxX;
        float unitY = Math.max(getWidth(), getHeight()) / maxY;

        // Está re mal
        float rX = (x / unitX) / mScaleFactor - ((startX / unitX) / mScaleFactor) - ((getWidth() * .5f / unitX) / mScaleFactor);
        float rY = (y / unitY) / mScaleFactor - ((startY / unitY) / mScaleFactor) - (((getHeight() + actionBarHeight) * .5f / unitY) / mScaleFactor);

        //Log.i("getFalsePoint2", ""+rY);
        return new float[] { rX, rY };
    }

    private float[] getStartPoint() {
        // (0, 0)
        return new float[] { 0, 0 };
    }

    private float[] getRealStartPoint() {
        // Devuelve los pixeles para el (0, 0) de los ejes cartesianos
        float[] point = this.getStartPoint();
        return new float[]{
                point[0] + getWidth() / 2f + startX,
                point[1] + getHeight() / 2f + startY
        };
    }

    public void agregarFuncion(Funcion fun) {
        this.funs.add(fun);
        this.update();
    }

    public void makeFunctionsPoints() {
        float advance = .05f;

        if (mScaleFactor < .2f) {
            advance = .2f;
        } else if (mScaleFactor < .3f) {
            advance = .15f;
        } else if (mScaleFactor < .5f) {
            advance = .1f;
        }

        float[] startPoint = this.getFalsePoint(0, 0);
        startPoint[0] -= 1;
        startPoint[1] += 1;

        float[] endPoint = this.getFalsePoint(getWidth(), getHeight());
        endPoint[0] += 1;
        endPoint[1] -= 1;

        float[] continuePoint = new float[] { 0f, 0f };
        boolean useContinuePoint = false;

        calculando = true;

        for (Funcion fun: this.funs) {
            fun.puntos.clear();
            useContinuePoint = false;

            if (fun.esEspecial()) {
                // FIXME: Buscar el primer y último punto
                for (float i=-1; i < getWidth() + 1; i+= 1) {
                    if (!calculando) {
                        return;
                    }

                    float x = getFalsePoint(i, 0)[0];
                    float[] point = new float[] { x, fun.getY(x) };

                    if (!getPointInScreen(point[0], point[1])) {
                        continue;
                    }

                    fun.puntos.add(point);
                }
            } else {
                for (float i = startPoint[0]; i < endPoint[0]; i += advance) {
                    if (!calculando) {
                        return;
                    }

                    float[] point = new float[]{i, fun.getY(i)};
                    if (this.getPointInScreen(i + advance, fun.getY(i + advance)) ||    // Si el siguiente está en pantalla, este entra
                            this.getPointInScreen(point[0], point[1])) {                    // Si este está en pantalla también entra

                        if (useContinuePoint) {
                            fun.puntos.add(continuePoint);
                            continuePoint = new float[]{0f, 0f};
                            useContinuePoint = false;
                        }
                        fun.puntos.add(point);
                    } else {
                        useContinuePoint = true;
                        continuePoint = point;
                    }
                }
            }
        }

        calculando = false;
    }

    public void update() {
        invalidate();
    }

    public ArrayList<Funcion> getFunciones() {
        return this.funs;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            update();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.i("DOUBLE TAP", "Tapped at: (" + event.getX() + "," + event.getY() + ")");
            return true;
        }
    }
}
