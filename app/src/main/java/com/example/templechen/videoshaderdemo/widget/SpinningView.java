package com.example.templechen.videoshaderdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;


public class SpinningView extends View {
	Paint paint;
	RectF rectf;
	int style;
	int size;
	int color;
	int time;

	public SpinningView(Context context, AttributeSet attrs) {
		super(context, attrs);

		style = 2;
		size = 200;
		color = Color.RED;
		time = 600;

		rectf = new RectF();
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setSpinColor(int c) {
		color = c;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = getWidth();
		int height = getHeight();
		long t = AnimationUtils.currentAnimationTimeMillis();

		if (style == 0 || style == 1) {

			int size = this.size == 0 ? width / 9 : this.size / 9;

			paint.setStyle(Style.FILL);
			paint.setColor(color);
			canvas.save();
			canvas.translate(width / 2, height / 2);

			float f = (float) Math.sin(Math.PI * (0 + (double) t / time));
			canvas.drawCircle(-size * 3f, 0, size * f, paint);

			f = (float) Math.sin(Math.PI * (-0.3 + (double) t / time));
			canvas.drawCircle(0, 0, size * f, paint);

			f = (float) Math.sin(Math.PI * (-0.6 + (double) t / time));
			canvas.drawCircle(size * 3f, 0, size * f, paint);

			canvas.restore();

		} else if (style == 2) {

			int r;
			if (this.size == 0) {
				r = Math.min(width, height) / 2;
			} else {
				r = this.size / 2;
			}

			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(r * 15 / 100);
			paint.setStrokeCap(Cap.ROUND);
			paint.setColor(color);

			canvas.save();
			canvas.translate(width / 2, height / 2);

			int r1 = r * 85 / 100;
			rectf.left = -r1;
			rectf.right = r1;
			rectf.top = -r1;
			rectf.bottom = r1;
			canvas.drawArc(rectf, (float) ((180. * t / time) % 360.), 60, false, paint);
			canvas.drawArc(rectf, (float) ((180. * t / time + 180.) % 360.), 60, false,
					paint);

			int r2 = r * 60 / 100;
			rectf.left = -r2;
			rectf.right = r2;
			rectf.top = -r2;
			rectf.bottom = r2;
			canvas.drawArc(rectf, (float) ((210.0 * t / time) % 360.), 60, false, paint);
			canvas.drawArc(rectf, (float) ((210.0 * t / time + 180) % 360.), 60, false,
					paint);

			canvas.restore();

		}

		invalidate();
	}

}
