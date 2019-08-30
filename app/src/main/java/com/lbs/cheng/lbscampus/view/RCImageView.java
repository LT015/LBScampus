package com.lbs.cheng.lbscampus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class RCImageView extends ImageView {
    float width,height;
    int Radius=12;
    public int getRadius() {
        return Radius;
    }

    public void setRadius(int radius) {
        Radius = radius;
    }
    public RCImageView(Context context) {
        super(context);
    }

    public RCImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RCImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RCImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

        width = getWidth();

        height = getHeight();

    }
    @Override

    protected void onDraw(Canvas canvas) {



        if (width > Radius && height > Radius) {

            Path path = new Path();

            path.moveTo(Radius, 0);

            path.lineTo(width - Radius, 0);

            path.quadTo(width, 0, width, Radius);

            path.lineTo(width, height - Radius);

            path.quadTo(width, height, width - Radius, height);

            path.lineTo(Radius, height);

            path.quadTo(0, height, 0, height - Radius);

            path.lineTo(0, Radius);

            path.quadTo(0, 0, Radius, 0);

            canvas.clipPath(path);

        }



        super.onDraw(canvas);

    }
}
