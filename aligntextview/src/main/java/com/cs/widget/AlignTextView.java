package com.cs.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cs.widget.R;

/**
 * Created by chenshi on 2017/11/9.
 */

public class AlignTextView  extends TextView {
    private float mLineSpacingMultiplier = 1.0f;
    private float mLineAdditionalVerticalPadding = 0.0f;
    private CharSequence mRealText;
    private Align mAlign = Align.ALIGN_CENTER;
    public AlignTextView(Context context) {
        super(context);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!this.isInEditMode())
            initTypedArray(context, attrs, -1, R.style.AlignTextView_Default);
    }

    public AlignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!this.isInEditMode())

            initTypedArray(context, attrs, defStyle, R.style.AlignTextView_Default);
    }

    @SuppressLint("NewApi")
    public AlignTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!this.isInEditMode())
            initTypedArray(context, attrs, defStyleAttr, defStyleRes);
    }
    public enum Align {
        ALIGN_CENTER,
        ALIGN_LEFT,
        ALIGN_RIGHT,
    }

    private void initTypedArray(Context context, AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AlignTextView, defStyleAttr, defStyleRes);
        int alignStyle = a.getInt(R.styleable.AlignTextView_align, 0);
        a.recycle();
        switch (alignStyle) {
            case 1:
                mAlign = Align.ALIGN_LEFT;
                break;
            case 2:
                mAlign = Align.ALIGN_RIGHT;
                break;
            case 0:
            default:
                mAlign = Align.ALIGN_CENTER;
                break;
        }
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        this.mLineAdditionalVerticalPadding = add;
        this.mLineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        mRealText = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //mAlign == Align.ALIGN_LEFT use default
        if (getMeasuredWidth() > 0 && mAlign != Align.ALIGN_LEFT) {
            CharSequence text = mRealText;
            if (text == null || text.length() == 0) {
                return;
            }
            Layout layout = getLayout();
            if (layout == null) {
                layout = createWorkingLayout(text);
            }
            int linCount = layout.getLineCount();
            CharSequence line = "";
            TextPaint paint = getPaint();
            paint.setColor(getCurrentTextColor());
            paint.drawableState = getDrawableState();
            int contw = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int textW = layout.getWidth() > contw ? contw : layout.getWidth();
            float measureW = 0;
            float gap = 0;
            float x;
            float y;
            int lineCount = 0;
            for (int i = 0; i < linCount; i++) {
                line = text.subSequence(layout.getLineStart(i), layout.getLineEnd(i));
                lineCount = line.length();
                measureW = paint.measureText(line, 0, line.length());
                switch (mAlign) {
                    case ALIGN_RIGHT:
                        gap = textW - measureW;
                        canvas.drawText(line, 0, line.length(), layout.getParagraphLeft(i) + getPaddingLeft() + gap, layout.getLineBottom(i), paint);
                        break;
                    case ALIGN_CENTER:
                    default:
                        gap = lineCount > 1 ? (textW - measureW) / (lineCount - 1) : (textW - measureW);
                        x = layout.getParagraphLeft(i) + getPaddingLeft();
                        y = layout.getLineBottom(i);
                        if (linCount > 1 && i == linCount - 1) { //最后一行保持默认方式
                            canvas.drawText(line, 0, line.length(), layout.getParagraphLeft(i) + getPaddingLeft(), layout.getLineBottom(i), paint);
                        }else {
                            for (int j = 0; j < line.length(); j++) {
                                if (j > 0) {
                                    x = layout.getParagraphLeft(i) + getPaddingLeft() + gap * (j - 1) + paint.measureText(line, 0, j);
                                }
                                canvas.drawText(line, j, j + 1, x, y, paint);
                            }
                        }
                        break;
                }
            }
        }else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private Layout createWorkingLayout(CharSequence workingText) {
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Layout.Alignment.ALIGN_NORMAL, mLineSpacingMultiplier, mLineAdditionalVerticalPadding, false);
    }
}