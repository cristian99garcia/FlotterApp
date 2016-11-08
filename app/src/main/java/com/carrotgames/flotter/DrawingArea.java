package com.carrotgames.flotter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
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

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.f;

    public DrawingArea(Context context) {
        super(context);

        gestureDetector = new GestureDetector(context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        paint = new Paint();
        funs = new ArrayList<>();
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
                // Evitar calcular cuando hay una doble pulsación
                this.makeFunctionsPoints();
                invalidate();
                break;

            case (MotionEvent.ACTION_MOVE):
                if (Math.abs(event.getX() - lastPoint[0]) > 10 || Math.abs(event.getY() - lastPoint[1]) > 10) {
                    startX += event.getX() - lastPoint[0];
                    startY += event.getY() - lastPoint[1];

                    lastPoint[0] = event.getX();
                    lastPoint[1] = event.getY();
                    invalidate();
                }

                break;
        }

        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    /*
    private float getFactor() {
        float[] pointA = this.getRealPoint(1, 1);
        float[] pointB = this.getRealPoint(2, 1);

        // Distancia entre dos puntos
        float a = (float) Math.pow(pointA[0] + pointB[0], 2);
        float b = (float) Math.pow(pointA[1] + pointB[1], 2);
        float d = (float) Math.sqrt(a + b);

        // FIXME: está re mal esto
        /*if (d >= 1000) {
            return 2f;
        } else if (d <= 400) {
            return 5f;
        } else if (d <= 800) {
            return 4f;
        } else if (d <= 1000) {
            return 3f;
        } else {
            return 1f;
        }////
        return 4;
    }
    */

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
            canvas.drawLine(point[0], 0, point[0], height, paint);
        }

        lastLine = 0;
        point = this.getRealPoint(lastLine, 0);
        while (point[0] < getWidth()) {
            lastLine += factor;
            point = this.getRealPoint(lastLine, 0);
            canvas.drawLine(point[0], 0, point[0], height, paint);
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

        canvas.drawLine(0, rPoint[1], width, rPoint[1], paint);  // X axe
        canvas.drawLine(rPoint[0], 0, rPoint[0], height, paint);   // Y axe
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

        return new float[]{ startPoint[0] + unitX * x * mScaleFactor, startPoint[1] - unitY * y * mScaleFactor };
    }

    private float[] getFalsePoint(float x, float y) {
        // Devuelve el punto en el sistema de ejes cartesianos a partir las coordenadas de un pixel
        float unitX = Math.max(getWidth(), getHeight()) / maxX;
        float unitY = Math.max(getWidth(), getHeight()) / maxY;

        // Está re mal
        float rX = (x / unitX) / mScaleFactor - ((startX / unitX) / mScaleFactor) - ((getWidth() * .5f / unitX) / mScaleFactor);
        float rY = (y / unitY) / mScaleFactor - ((startY / unitY) / mScaleFactor) - ((getHeight() * .5f / unitY) / mScaleFactor);

        return new float[] { rX, rY };
    }

    private float[] getStartPoint() {
        // (0, 0)
        //point[0] = 0;
        //point[1] = 0;

        return new float[] { 0, 0 };
    }

    private float[] getRealStartPoint() {
        // (0, 0) in screen
        float[] point = this.getStartPoint();
        return new float[]{ point[0] + getWidth() / 2f + startX, point[1] + getHeight() / 2f + startY};
    }

    public void agregarFuncion(Funcion fun) {
        this.funs.add(fun);
        this.update();
    }

    public void makeFunctionsPoints() {
        //float start, end, advance;
        //start = -20f;
        //end = 20f;

        //for (Funcion fun: funs) {
        //    fun.puntos.clear();
        //    fun.crearPuntos(start, end, advance);
        //}

        float advance = .05f;

        if (mScaleFactor < .2f) {
            advance = .2f;
        } else if (mScaleFactor < .3f) {
            advance = .15f;
        } else if (mScaleFactor < .5f) {
            advance = .1f;
        }

        float[] startPoint = this.getFalsePoint(0, 0);
        float[] endPoint = this.getFalsePoint(getWidth(), getHeight());
        float[] continuePoint = new float[] { 0f, 0f };
        boolean useContinuePoint = false;

        for (Funcion fun: this.funs) {
            //fun.makePoints(start, end, advance);
            fun.puntos.clear();
            useContinuePoint = false;

            for (float i=startPoint[0]; i<endPoint[0]; i += advance) {
                float[] point = new float[] { i, fun.getY(i) };
                if (this.getPointInScreen(i + advance, fun.getY(i + advance)) ||    // Si el siguiente está en pantalla, este entra
                        this.getPointInScreen(point[0], point[1])) {                    // Si este está en pantalla también entra
                    if (useContinuePoint) {
                        fun.puntos.add(continuePoint);
                        continuePoint = new float[] { 0f, 0f };
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

            invalidate();
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
