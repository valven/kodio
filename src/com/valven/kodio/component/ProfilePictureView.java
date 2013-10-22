package com.valven.kodio.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.valven.kodio.R;

public class ProfilePictureView extends ImageView {
	Bitmap bitmap;

	public ProfilePictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.image_bg);
	}

	@Override
	public void setImageBitmap(Bitmap bm){
		Bitmap bitmap = getRoundedShape(bm);
		super.setImageBitmap(bitmap);
	}

	private Bitmap getRoundedShape(Bitmap sourceBitmap) {
		int targetWidth = sourceBitmap.getWidth();
		int targetHeight = sourceBitmap.getHeight();
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, targetWidth,
				targetHeight), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}

}
