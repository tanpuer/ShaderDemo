package com.example.templechen.videoshaderdemo.gl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class RoundVideoFrameLayout extends FrameLayout {

	boolean shouldClip = true;
	int cornerRadius = 64;

	public RoundVideoFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
		invalidate();
	}

	public void setShouldClip(boolean shouldClip) {
		this.shouldClip = shouldClip;
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		if (shouldClip && cornerRadius != 0) {
			canvas.save();
			try {
				Path path = new Path();
				path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
				canvas.clipPath(path);
				super.draw(canvas);
			} catch (Exception e) {
				super.draw(canvas);
			} finally {
				canvas.restore();
			}
		} else {
			super.draw(canvas);
		}
	}
}
