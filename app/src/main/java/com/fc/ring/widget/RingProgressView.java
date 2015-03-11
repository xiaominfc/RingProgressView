package com.fc.ring.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fc.ring.R;

/**
 * Created by xiaominfc on 2/8/15.
 */
public class RingProgressView extends View {
    private static final int CIRCLEWIDTH = 220;
    private static final int HALFCIRCLEWIDTH = CIRCLEWIDTH / 2;

    private static final int LINELEGHT = 240;

    private static final int HALFLINELEGHT = LINELEGHT / 2;
    private static final int PAINTWIDTH = 28;


    private static final float FULLLENGTH = (float) (LINELEGHT * 2 + CIRCLEWIDTH * Math.PI);

    private static final int OFFSET = 10;

    private static final int PADDINGLEFT = 10;
    private static final int PADDINGTOP = 10;

    private static final int LINEOFFSET = 8;

    private static final float CIRCLEQUATTERPROGRESS = (float) (CIRCLEWIDTH * 1.0 * Math.PI / FULLLENGTH / 4.0);
    private static final float LINEPROGRESS = (float) ((1.0 - CIRCLEQUATTERPROGRESS * 4.0) / 2);
    private RectF leftDrawRect;
    private RectF rightDrawRect;

    private int backgroundColor = Color.parseColor("#5f5f5f");
    private int progressColor = Color.parseColor("#5f5f5f");

    private float start = 0.0f;


    private Point touchPoint;


    private TouchThumb startTouchThumb;
    private TouchThumb mTouchThumb;

    private float lastX, lastY;

    public RingProgressView(Context context) {
        super(context);
        init();
    }

    public RingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RingProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        int leftSidePadding = PAINTWIDTH + PADDINGLEFT;
        int topSidePadding = PAINTWIDTH + PADDINGTOP;
        leftDrawRect = new RectF(leftSidePadding, topSidePadding, CIRCLEWIDTH + leftSidePadding, CIRCLEWIDTH + topSidePadding);
        rightDrawRect = new RectF(leftSidePadding + LINELEGHT, topSidePadding, CIRCLEWIDTH + LINELEGHT + leftSidePadding, CIRCLEWIDTH + topSidePadding);
        if (null == startTouchThumb) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.time_horizhon_thumb_image);
            startTouchThumb = new TouchThumb(-(HALFLINELEGHT + HALFCIRCLEWIDTH), 0);
            startTouchThumb.setBitmap(bitmap);
        }
    }

    public void setStartProgress(float progress) {
        if (null != startTouchThumb) {
            startTouchThumb.setProgress(progress);
        }
        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawBackgroundLine(canvas);
        drawProgressLine(canvas);
        drawThumbs(canvas);
    }

    private void drawBackgroundLine(Canvas canvas) {
        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(PAINTWIDTH);
        //draw left side circle
        canvas.drawArc(leftDrawRect, 90, 180, false, paint);


        int topLine_startX = (int) leftDrawRect.left + CIRCLEWIDTH / 2 - LINEOFFSET;
        int topLine_endX = (int) leftDrawRect.left + CIRCLEWIDTH / 2 + LINELEGHT + LINEOFFSET;
        //draw top line
        canvas.drawLine(topLine_startX, leftDrawRect.top, topLine_endX, leftDrawRect.top, paint);
        //draw bottom line
        canvas.drawLine(topLine_startX, leftDrawRect.bottom, topLine_endX, leftDrawRect.bottom, paint);
        //draw left side circle
        canvas.drawArc(rightDrawRect, -90, 180, false, paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(CIRCLEWIDTH + LINELEGHT + 80, CIRCLEWIDTH + 80);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPoint = new Point((int) (event.getX() - (leftDrawRect.left + LINELEGHT / 2 + CIRCLEWIDTH / 2)), (int) (event.getY() - (leftDrawRect.top + CIRCLEWIDTH / 2)));
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (startTouchThumb.containPoint(touchPoint)) {
                mTouchThumb = startTouchThumb;
                lastX = event.getX();
                lastY = event.getY();
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (null != mTouchThumb) {
                updateTouchThumbByOffSet((int) (event.getX() - lastX), (int) (event.getY() - lastY));
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            mTouchThumb = null;
        }
        lastX = event.getX();
        lastY = event.getY();
        invalidate();
        return super.onTouchEvent(event);
    }

    private void updateTouchThumbByOffSet(int offsetX, int offsetY) {
        int newX = mTouchThumb.point.x + offsetX;
        int newY = mTouchThumb.point.y;
        if (mTouchThumb.point.x >= -(LINELEGHT + HALFCIRCLEWIDTH) / 2 && mTouchThumb.point.x <= (LINELEGHT + HALFCIRCLEWIDTH) / 2) {
            if (newX >= -LINELEGHT / 2 && newX < LINELEGHT / 2) {
                if (mTouchThumb.point.y > 0) {
                    newY = HALFCIRCLEWIDTH;
                } else {
                    newY = -HALFCIRCLEWIDTH;
                }

            } else {
                int tmp = getOtherSide(Math.abs(newX) - LINELEGHT / 2);

                if (mTouchThumb.point.y < 0) {
                    newY = -tmp;
                } else {
                    newY = tmp;
                }
            }
        } else {
            newY = mTouchThumb.point.y + offsetY;
            int tmp = getOtherSide(newY);
            if (mTouchThumb.point.x < 0) {
                newX = -(tmp + LINELEGHT / 2);
            } else {
                newX = (tmp + LINELEGHT / 2);
            }
        }
        mTouchThumb.point.x = newX;
        mTouchThumb.point.y = newY;
    }


    /**
     * run other side by the side for circle
     * @param side
     * @return other side width
     */
    private int getOtherSide(int side) {
        return (int) Math.sqrt(Math.pow(CIRCLEWIDTH / 2, 2) - Math.pow(side, 2));
    }


    /**
     * @param convas draw progress background to canvas
     */
    private void drawProgressLine(Canvas convas) {
        Paint paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        paint.setColor(progressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(PAINTWIDTH);
    }

    /**
     * @param canvas draw thumbs to canvas
     */
    private void drawThumbs(Canvas canvas) {
        int save = canvas.save();
        canvas.translate(leftDrawRect.left + LINELEGHT / 2 + CIRCLEWIDTH / 2, leftDrawRect.top + CIRCLEWIDTH / 2);
        startTouchThumb.draw(canvas);
        canvas.restoreToCount(save);
    }


    /**
     *
     */
    private class TouchThumb {
        private Point point;
        private Bitmap mBitmap;
        private float progress;

        public TouchThumb() {
            point = new Point(0, 0);
        }

        public TouchThumb(int x, int y) {
            point = new Point(x, y);
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            progress = Math.abs(progress);
            if (progress > 1) {
                progress = (float) ((progress % 10) / 10.0);
            }
            this.progress = progress;
            updatePointByProgress();
        }




        private void updatePointByProgress() {
            if (progress >= 1.0 - CIRCLEQUATTERPROGRESS) {
                double radian = ((1.0 - progress) * FULLLENGTH) / HALFCIRCLEWIDTH;
                point.y = (int) (Math.sin(radian) * HALFCIRCLEWIDTH);
                point.x = -((int) (Math.cos(radian) * HALFCIRCLEWIDTH) + HALFLINELEGHT);
            } else if (progress >= 0.5 + CIRCLEQUATTERPROGRESS) {
                point.y = HALFCIRCLEWIDTH;
                int tmp = (int) ((progress - 0.75) * FULLLENGTH);
                point.x = -tmp;
            } else if (progress >= 0.5) {
                double radian = ((progress - 0.5) * FULLLENGTH) / HALFCIRCLEWIDTH;
                point.y = (int) (Math.sin(radian) * HALFCIRCLEWIDTH);
                point.x = (int) (Math.cos(radian) * HALFCIRCLEWIDTH) + HALFLINELEGHT;
            } else if (progress >= 0.5 - CIRCLEQUATTERPROGRESS) {
                double radian = ((0.5 - progress) * FULLLENGTH) / HALFCIRCLEWIDTH;
                point.y = -(int) (Math.sin(radian) * HALFCIRCLEWIDTH);
                point.x = (int) (Math.cos(radian) * HALFCIRCLEWIDTH) + HALFLINELEGHT;

            } else if (progress >= CIRCLEQUATTERPROGRESS) {
                point.y = -HALFCIRCLEWIDTH;
                int tmp = (int) ((progress - 0.25) * FULLLENGTH);
                point.x = tmp;
            } else {
                double radian = (progress * FULLLENGTH) / HALFCIRCLEWIDTH;
                point.y = -(int) (Math.sin(radian) * HALFCIRCLEWIDTH);
                point.x = -(int) ((Math.cos(radian) * HALFCIRCLEWIDTH) + HALFLINELEGHT);
            }
        }

        public void setBitmap(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        public void draw(Canvas canvas) {
            if (null != mBitmap) {
                updateProgressByPoint();

                int left = point.x - mBitmap.getWidth() / 2;
                int top = point.y - mBitmap.getHeight() / 2;

                canvas.drawBitmap(mBitmap, left, top, new Paint());
            }
        }

        private void updateProgressByPoint() {
            if (Math.abs(point.y) == HALFCIRCLEWIDTH) {
                float tmp = (float) (Math.abs(point.x) * 1.0 / FULLLENGTH);
                tmp = Math.abs(tmp);
                if (point.x < 0) {
                    if (point.y < 0) {
                        progress = (float) (0.25 - tmp);
                    } else {
                        progress = (float) (0.75 + tmp);
                    }
                } else {
                    if (point.y < 0) {
                        progress = (float) (0.25 + tmp);
                    } else {
                        progress = (float) (0.75 - tmp);
                    }
                }
            } else {
                double tan = Math.abs(point.y) * 1.0 / (Math.abs(point.x) - HALFLINELEGHT);
                float tmp = (float) Math.atan(tan) * HALFCIRCLEWIDTH / FULLLENGTH;
                tmp = Math.abs(tmp);
                if (point.x < 0) {
                    if (point.y < 0) {
                        progress = tmp;
                    } else {
                        progress = (float) (1.0 - tmp);
                    }
                } else {
                    if (point.y < 0) {
                        progress = (float) (0.5 - tmp);
                    } else {
                        progress = (float) (0.5 + tmp);
                    }
                }

            }
        }

        public boolean containPoint(Point touchPoint) {
            if (null != this.mBitmap) {
                double offset = Math.sqrt(Math.pow(touchPoint.x - point.x, 2) + Math.pow(touchPoint.y - point.y, 2));
                if (offset < this.mBitmap.getWidth() * 2) {
                    return true;
                }
            }
            return false;
        }

    }


}
