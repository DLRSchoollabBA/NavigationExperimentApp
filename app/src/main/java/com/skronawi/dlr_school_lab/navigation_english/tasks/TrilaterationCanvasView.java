package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

//coordinate distance: http://boulter.com/gps/distance/?from=52.4290%2C+13.5275&to=52.4290%2C+13.5270&units=k
public class TrilaterationCanvasView extends View {

    private static final int POINT_RADIUS = 30;
    private static final float TILE_WIDTH_METER = 35;

    /*
    a tile has width 1, then it has height 1.63.
     */
    private static float TILE_HEIGHT_FACTOR = 1.63f;
    private static float AXIS_STEP = 0.0005f;

    private static float X_RANGE = 0.004f;
    private static float X_START = 13.5270f;
    private static int NUMBER_TILES_X = 8;
    private static float X_OFFSET = 35;

    private static float Y_RANGE = 0.003f;
    private static float Y_START = 52.4270f;
    private static float Y_OFFSET = 80;

    private final Paint gridPaint;
    private final Paint axisTextPaint;
    private final Paint point1Paint;
    private final Paint point2Paint;
    private final Paint point3Paint;
    private final Paint point4Paint;
    private final Paint circle1Paint;
    private final Paint circle2Paint;
    private final Paint circle3Paint;
    private final Paint circle4Paint;
    private final Paint gridBorderPaint;
    private final Paint targetPaint;
    private final NumberFormat nf;
    private final Paint backgroundPaint;
    private final Paint numberPaint;

    private Point point1Coords; //alexandria
    private Point point2Coords; //portolan
    private Point point3Coords; //sextant
    private Point point4Coords; //height estimation
    private Point currentPoint;

    private int width;
    private int height;
    private float tileWidth;
    private float tileHeight;

    private Point targetPointCoords;
    private TrilaterationCoordReceiver trilaterationCoordReceiver;

    private boolean touchCirclesEnabled;
    private boolean touchTargetEnabled;
    private boolean doRenderRadii;
    private boolean doRenderCircles;
    private boolean doRenderTarget;

    public TrilaterationCanvasView(Context context, AttributeSet attrs) {

        super(context, attrs);

        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);

        gridBorderPaint = new Paint();
        gridBorderPaint.setAntiAlias(true);
        gridBorderPaint.setColor(Color.BLACK);
        gridBorderPaint.setStyle(Paint.Style.STROKE);
        gridBorderPaint.setStrokeWidth(6f);

        axisTextPaint = new Paint();
        axisTextPaint.setAntiAlias(true);
        axisTextPaint.setColor(Color.BLACK);
        axisTextPaint.setStyle(Paint.Style.STROKE);
        axisTextPaint.setTextSize(30);

        point1Paint = new Paint();
        point1Paint.setAntiAlias(true);
        point1Paint.setColor(Color.BLACK);
        point1Paint.setStrokeWidth(1f);

        point2Paint = new Paint();
        point2Paint.setAntiAlias(true);
        point2Paint.setColor(Color.BLACK);
        point2Paint.setStrokeWidth(1f);

        point3Paint = new Paint();
        point3Paint.setAntiAlias(true);
        point3Paint.setColor(Color.BLACK);
        point3Paint.setStrokeWidth(1f);

        point4Paint = new Paint();
        point4Paint.setAntiAlias(true);
        point4Paint.setColor(Color.BLACK);
        point4Paint.setStrokeWidth(1f);

        numberPaint = new Paint();
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(Color.WHITE);
        numberPaint.setStyle(Paint.Style.STROKE);
        numberPaint.setTextSize(30);
        numberPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        circle1Paint = new Paint();
        circle1Paint.setAntiAlias(true);
        circle1Paint.setColor(Color.BLACK);
        circle1Paint.setStyle(Paint.Style.STROKE);
        circle1Paint.setStrokeWidth(2f);

        circle2Paint = new Paint();
        circle2Paint.setAntiAlias(true);
        circle2Paint.setColor(Color.BLACK);
        circle2Paint.setStyle(Paint.Style.STROKE);
        circle2Paint.setStrokeWidth(2f);

        circle3Paint = new Paint();
        circle3Paint.setAntiAlias(true);
        circle3Paint.setColor(Color.BLACK);
        circle3Paint.setStyle(Paint.Style.STROKE);
        circle3Paint.setStrokeWidth(2f);

        circle4Paint = new Paint();
        circle4Paint.setAntiAlias(true);
        circle4Paint.setColor(Color.BLACK);
        circle4Paint.setStyle(Paint.Style.STROKE);
        circle4Paint.setStrokeWidth(2f);

        nf = NumberFormat.getNumberInstance(Locale.ENGLISH);

        targetPaint = new Paint();
        targetPaint.setAntiAlias(true);
        targetPaint.setColor(Color.DKGRAY);
        targetPaint.setStrokeWidth(1f);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }

    private Point gridPointFromCoords(float lat, float lon) {

        float yCoordDist = lat - Y_START; //in coordinate width units
        float yCanvasUnitsPerCoordUnits = height / Y_RANGE * TILE_HEIGHT_FACTOR;
        float y = yCoordDist * yCanvasUnitsPerCoordUnits; //compute canvas units

        float xCoordDist = lon - X_START;
        float xCanvasUnitsPerCoordUnits = width / X_RANGE;
        float x = xCoordDist * xCanvasUnitsPerCoordUnits;

        //(0,0) is in the upper left corner, but the canvas coordinate system (0,0) is in the lower left corner
        float y_ = height - y + Y_OFFSET;

        float x_ = x + X_OFFSET;

        return new Point(x_, y_, 0);
    }

    private Point coordsFromGridPoint(float x_, float y_) {

        //reverse of gridPointFromCoords()

        float lon = X_START + (x_ - X_OFFSET) / (width / X_RANGE);
        float lat = Y_START + (height - y_ + Y_OFFSET) / (height / Y_RANGE * TILE_HEIGHT_FACTOR);
        return new Point(lon, lat, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();

        tileWidth = (float) width / (float) NUMBER_TILES_X;
        tileHeight = tileWidth * TILE_HEIGHT_FACTOR;

        //init points
        if (point1Coords == null) {
            point1Coords = gridPointFromCoords(52.42733f, 13.52897f);
            point2Coords = gridPointFromCoords(52.42813f, 13.53011f);
            point3Coords = gridPointFromCoords(52.42885f, 13.53065f);
            point4Coords = gridPointFromCoords(52.42865f, 13.52703f);
        }

        drawBackground(canvas);
        drawGrid(canvas);

        if (doRenderCircles) {
            drawCircles(canvas);
        }
        if (doRenderRadii) {
            drawRadii(canvas);
        }
        if (doRenderTarget) {
            drawTarget(canvas);
        }

        drawCenters(canvas);
        drawAxes(canvas);
    }

    private void drawRadii(Canvas canvas) {

        DecimalFormat dfCircleRadius = (DecimalFormat) nf;
        dfCircleRadius.applyPattern("##0.0");

        canvas.drawText(dfCircleRadius.format(point1Coords.radius / tileWidth * TILE_WIDTH_METER) + " m",
                point1Coords.x - (POINT_RADIUS + 15), point1Coords.y - (POINT_RADIUS + 5), axisTextPaint);
        canvas.drawText(dfCircleRadius.format(point2Coords.radius / tileWidth * TILE_WIDTH_METER) + " m",
                point2Coords.x - (POINT_RADIUS + 15), point2Coords.y - (POINT_RADIUS + 5), axisTextPaint);
        canvas.drawText(dfCircleRadius.format(point3Coords.radius / tileWidth * TILE_WIDTH_METER) + " m",
                point3Coords.x - (POINT_RADIUS + 15), point3Coords.y - (POINT_RADIUS + 5), axisTextPaint);
        canvas.drawText(dfCircleRadius.format(point4Coords.radius / tileWidth * TILE_WIDTH_METER) + " m",
                point4Coords.x - (POINT_RADIUS + 10), point4Coords.y - (POINT_RADIUS + 5), axisTextPaint);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, width, height, backgroundPaint);
    }

    private void drawTarget(Canvas canvas) {

        if (targetPointCoords == null) {
            return;
        }

        canvas.drawRect(targetPointCoords.x - 15, targetPointCoords.y - 15,
                targetPointCoords.x + 15, targetPointCoords.y + 15, targetPaint);
    }

    private void drawCircles(Canvas canvas) {

        canvas.drawCircle(point1Coords.x, point1Coords.y, point1Coords.radius, circle1Paint);

        canvas.drawCircle(point2Coords.x, point2Coords.y, point2Coords.radius, circle2Paint);

        canvas.drawCircle(point3Coords.x, point3Coords.y, point3Coords.radius, circle3Paint);

        canvas.drawCircle(point4Coords.x, point4Coords.y, point4Coords.radius, circle4Paint);
    }

    private void drawCenters(Canvas canvas) {

        canvas.drawCircle(point1Coords.x, point1Coords.y, POINT_RADIUS, point1Paint);
        canvas.drawText("1", point1Coords.x - 10, point1Coords.y + 10, numberPaint);

        canvas.drawCircle(point2Coords.x, point2Coords.y, POINT_RADIUS, point2Paint);
        canvas.drawText("2", point2Coords.x - 10, point2Coords.y + 10, numberPaint);

        canvas.drawCircle(point3Coords.x, point3Coords.y, POINT_RADIUS, point3Paint);
        canvas.drawText("3", point3Coords.x - 10, point3Coords.y + 10, numberPaint);

        canvas.drawCircle(point4Coords.x, point4Coords.y, POINT_RADIUS, point4Paint);
        canvas.drawText("4", point4Coords.x - 10, point4Coords.y + 10, numberPaint);
    }

    private void drawAxes(Canvas canvas) {

        DecimalFormat dfAxisCaption = (DecimalFormat) nf;
        dfAxisCaption.applyPattern("00.0000");

        for (int i = 0; i < NUMBER_TILES_X; i++) {
            String axisCaption = dfAxisCaption.format(X_START + i * AXIS_STEP);
            canvas.drawText(axisCaption,
                    i * tileWidth + 10 + X_OFFSET,
                    height - 10,
                    axisTextPaint);
        }

        float currentHeight = height + Y_OFFSET;
        int step = 0;
        while (currentHeight > 0) {

            if (step > 0) {
                String axisCaption = dfAxisCaption.format(Y_START + step * AXIS_STEP);
                canvas.drawText(axisCaption,
                        10,
                        currentHeight + 25,
                        axisTextPaint);
            }

            currentHeight = Math.max(currentHeight - tileHeight, 0);
            step++;
        }
    }

    private void drawGrid(Canvas canvas) {

        //x axis or columns
        for (int i = 0; i <= NUMBER_TILES_X; i++) {
            canvas.drawLine(
                    (float) i * tileWidth + X_OFFSET, 0,
                    (float) i * tileWidth + X_OFFSET, height,
                    gridPaint);
        }
        canvas.drawLine(
                (float) 0, 0,
                (float) 0, height,
                gridBorderPaint);
        canvas.drawLine(
                (float) width, 0,
                (float) width, height,
                gridBorderPaint);

        //y axis or lines; note, that the coord-system for y is reversed!
        float currentHeight = height + Y_OFFSET;
        while (currentHeight > 0) {
            canvas.drawLine(
                    0, currentHeight,
                    width, currentHeight,
                    gridPaint);
            currentHeight = Math.max(currentHeight - tileHeight, 0);
        }
        canvas.drawLine(
                0, 0,
                width, 0,
                gridBorderPaint);
        canvas.drawLine(
                0, height,
                width, height,
                gridBorderPaint);
    }

    public Point[] getPoints() {
        return new Point[]{point1Coords, point2Coords, point3Coords, point4Coords, targetPointCoords};
    }

    public void setPoints(Point[] points) {
        point1Coords = points[0];
        point2Coords = points[1];
        point3Coords = points[2];
        point4Coords = points[3];
        targetPointCoords = points[4];
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        Point pointCoords = determineTouchedPoint(x, y);
        if (pointCoords != null) {
            currentPoint = pointCoords;
        }
    }

    private Point determineTouchedPoint(float x, float y) {

        if (touchCirclesEnabled) {
            double sqrt = Math.sqrt(Math.pow(point1Coords.x - x, 2) + Math.pow(point1Coords.y - y, 2));
            if (sqrt < 100) {
                return point1Coords;
            }
            sqrt = Math.sqrt(Math.pow(point2Coords.x - x, 2) + Math.pow(point2Coords.y - y, 2));
            if (sqrt < 100) {
                return point2Coords;
            }
            sqrt = Math.sqrt(Math.pow(point3Coords.x - x, 2) + Math.pow(point3Coords.y - y, 2));
            if (sqrt < 100) {
                return point3Coords;
            }
            sqrt = Math.sqrt(Math.pow(point4Coords.x - x, 2) + Math.pow(point4Coords.y - y, 2));
            if (sqrt < 100) {
                return point4Coords;
            }
        }

        if (touchTargetEnabled) {
            targetPointCoords = new Point(x, y, 0);
            Point point = coordsFromGridPoint(x, y);
            trilaterationCoordReceiver.setLatLon(point.y, point.x);
        }

        return null;
    }

    private void moveTouch(float x, float y) {

        if (currentPoint != null) {
            currentPoint.radius = (float) Math.sqrt(Math.pow(currentPoint.x - x, 2) + Math.pow(currentPoint.y - y, 2));
        }
    }

    private void upTouch() {
        currentPoint = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                break;
        }
        invalidate();
        return true;
    }

    public void setTrilaterationCoordReceiver(TrilaterationCoordReceiver trilaterationCoordReceiver) {
        this.trilaterationCoordReceiver = trilaterationCoordReceiver;
    }

    public void enableTouchCircles(boolean touchCirclesEnabled) {
        this.touchCirclesEnabled = touchCirclesEnabled;
    }

    public void enableTouchTarget(boolean touchTargetEnabled) {
        this.touchTargetEnabled = touchTargetEnabled;
    }

    public void visualizeAccuracy(int pointId, TaskManager.Accuracy accuracy) {
        Paint pointPaint;
        Paint circlePaint;
        if (pointId == 1) {
            pointPaint = point1Paint;
            circlePaint = circle1Paint;
        } else if (pointId == 2) {
            pointPaint = point2Paint;
            circlePaint = circle2Paint;
        } else if (pointId == 3) {
            pointPaint = point3Paint;
            circlePaint = circle3Paint;
        } else {// if (pointId == 4) {
            pointPaint = point4Paint;
            circlePaint = circle4Paint;
        }

        int color = accuracy.color;

        pointPaint.setColor(color);
        circlePaint.setColor(color);
        numberPaint.setColor(Color.BLACK);
    }

    public void renderRadii(boolean doRender) {
        doRenderRadii = doRender;
    }

    public void renderCircles(boolean doRender) {
        doRenderCircles = doRender;
    }

    public void renderTarget(boolean doRender) {
        doRenderTarget = doRender;
    }

    public static class Point {

        public float x;
        public float y;
        public float radius;

        public Point(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public String toString() {
            return x + ";" + y + ";" + radius;
        }

        public static Point fromString(String pointString) {
            String[] tokens = pointString.split(";");
            return new Point(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
        }
    }
}
