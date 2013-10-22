package com.valven.kodio.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.valven.kodio.R;

public class CustomTextView extends TextView {
	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs,
				R.styleable.CustomTextView);
		String customFont = a.getString(R.styleable.CustomTextView_customFont);
		setCustomFont(ctx, customFont);
		setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG
				| Paint.DEV_KERN_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		a.recycle();
	}

	public boolean setCustomFont(Context ctx, String asset) {
		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), asset);
		} catch (Exception e) {
			Log.e("customFont", "Could not get typeface: " + e.getMessage(), e);
			return false;
		}

		setTypeface(tf);
		setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG
				| Paint.DEV_KERN_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		return true;
	}
}
