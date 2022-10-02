package com.shezz.sa4kvideoplayer.floatingactionbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.util.AttributeSet;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.mxplayer.paksoft.R;

public class AddFloatingActionButton extends FloatingActionButton {
  int mPlusColor;
  @DrawableRes
  private int mIcon;
  private Drawable mIconDrawable;
  private int mSize;

  public AddFloatingActionButton(Context context) {
    this(context, null);
  }

  public AddFloatingActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AddFloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  void init(Context context, AttributeSet attributeSet) {
    TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.AddFloatingActionButton, 0, 0);
    mPlusColor = attr.getColor(R.styleable.AddFloatingActionButton_fab_plusIconColor, getColor(android.R.color.white));
    mSize = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
    mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0);
    attr.recycle();

    super.init(context, attributeSet);
  }

  /**
   * @return the current Color of plus icon.
   */
  public int getPlusColor() {
    return mPlusColor;
  }

  public void setPlusColorResId(@ColorRes int plusColor) {
    setPlusColor(getColor(plusColor));
  }

  public void setPlusColor(int color) {
    if (mPlusColor != color) {
      mPlusColor = color;
      updateBackground();
    }
  }

  @Override
  public void setIcon(@DrawableRes int icon) {
    throw new UnsupportedOperationException("Use FloatingActionButton if you want to use custom icon");
  }

  @Override
  Drawable getIconDrawable() {
    if (mIconDrawable != null) {
      return mIconDrawable;
    } else if (mIcon != 0) {
      return getResources().getDrawable(R.drawable.hplib_ic_play);
    } else {
      return new ColorDrawable(Color.TRANSPARENT);
    }
  }
}
