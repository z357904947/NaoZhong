package com.example.z3579.naozhong.until;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.z3579.naozhong.R;

/**
 * 列表分割线，实现简单的自定义功能
 */
public class SimpleDividerDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight=1;
    private Paint dividerPaint;
    private int color= Color.BLACK;//默认黑色
    private int left = -1;
    private int right = -1;
    //**

    public SimpleDividerDecoration(Context context) {
        dividerPaint = new Paint();
        dividerPaint.setColor(color);
    }
    //设置颜色
    public void setColor(int color){
        dividerPaint.setColor(color);
    }
    //设置左边距
    public void setLeft(int left){
        this.left=left;
    }
    //设置右边距
    public void setRight(int right){
        this.right=right;
    }

    public void setDividerHeight(int dividerHeight) {
        this.dividerHeight = dividerHeight;
    }

    /**
     * 范围
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;


    }

    /**
     * 绘制
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if(left==-1){
            left=parent.getPaddingLeft();
        }
        int righttemp;
        if(right==-1){
            righttemp = parent.getWidth() - parent.getPaddingRight();

        }else {
            righttemp= parent.getWidth() - right;

        }
        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left, top, righttemp, bottom, dividerPaint);
        }
    }
}


