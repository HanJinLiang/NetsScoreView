package com.hanjinliang.netsscore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by HanJinLiang on 2017-07-04.
 * 网状评分控件
 */
public class NetsScoreView extends View {
    private int mWidth;//宽度
    private int mHeight;//高度
    private float mRadius;//半径  只是网格半径不包括文字部分
    private int mEdgeCount=5;//多边形边数
    private int mMaxScore=100;//最大值
    private float[] scores;//分值
    private Context mContext;

    private Path mPath;
    private Paint mNetsPaint;//网格Paint
    private int mNetsColor;//网格颜色
    private Paint mScoreLinePaint;//覆盖外延线Paint
    private int mScoreLineColor;//覆盖外延线Color
    private Paint mScoreFillPaint;//覆盖填充Paint
    private int mScoreFillColor;//覆盖填充Color

    private Paint mScoreTxtPaint;//分数Color
    private int mScoreTxtColor;//分数Color

    private float mTxtMaxLength;//分数里面最大长度
    private float mTxtHeight;//分数文字高度

    public int mLineWidth;//线的宽度  默认1dp
    public float mTxtSize;//分数Size

    public NetsScoreView(Context context) {
        this(context,null);
    }

    public NetsScoreView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NetsScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        //获取自定义属性
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.NetsScoreView,defStyleAttr,0);
        mNetsColor=ta.getColor(R.styleable.NetsScoreView_NetsColor, Color.GRAY);
        mScoreLineColor=ta.getColor(R.styleable.NetsScoreView_ScoreLineColor, Color.RED);
        mScoreFillColor=ta.getColor(R.styleable.NetsScoreView_ScoreFillColor,Color.argb(100,255,0,0));
        mScoreTxtColor=ta.getColor(R.styleable.NetsScoreView_ScoreTxtColor,Color.GRAY);
        mTxtSize=ta.getDimension(R.styleable.NetsScoreView_TxtSize,sp2px(12));
        mEdgeCount=ta.getInt(R.styleable.NetsScoreView_EdgeCount,5);
        if(mEdgeCount<3){
            mEdgeCount=3;//最少需要3个边
        }
        mMaxScore=ta.getInt(R.styleable.NetsScoreView_MaxScore,100);
        ta.recycle();

        mNetsPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mNetsPaint.setStyle(Paint.Style.STROKE);
        mNetsPaint.setStrokeWidth(mLineWidth);
        mNetsPaint.setColor(mNetsColor);

        mScoreLinePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mScoreLinePaint.setStyle(Paint.Style.STROKE);
        mScoreLinePaint.setStrokeWidth(mLineWidth);
        mScoreLinePaint.setColor(mScoreLineColor);

        mScoreFillPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mScoreFillPaint.setStyle(Paint.Style.FILL);
        mScoreFillPaint.setStrokeWidth(mLineWidth);
        mScoreFillPaint.setColor(mScoreFillColor);

        mScoreTxtPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mScoreTxtPaint.setStyle(Paint.Style.FILL);
        mScoreTxtPaint.setStrokeWidth(mLineWidth);
        mScoreTxtPaint.setColor(mScoreTxtColor);
        mScoreTxtPaint.setTextAlign(Paint.Align.CENTER);
        mScoreTxtPaint.setTextSize(mTxtSize);


        mPath=new Path();
        scores=new float[mEdgeCount];

        mTxtMaxLength=getTxtMaxLength();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int width,height,result;

        //计算宽度
        if(widthMode==MeasureSpec.EXACTLY){//MatchParent
            width=widthSize;
        }else{//默认高度
            width=dp2px(300);//默认宽度300dp
        }

        //计算高度
        if(heightMode==MeasureSpec.EXACTLY){//MatchParent
            height=heightSize;
        }else{//默认高度
            height=dp2px(300);//默认高度300dp
        }
        result=Math.min(width,height);
        setMeasuredDimension(result,result);
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public   int sp2px(float spValue) {
        final float fontScale =mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth=getWidth();
        mHeight=getHeight();
        mRadius=mWidth/2-mTxtMaxLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth/2,mWidth/2);//
        for(int i=1;i<=mEdgeCount;i++){
            drawPolygon(canvas,mEdgeCount,mRadius/mEdgeCount*i);
        }

        drawCenterLine(canvas,mEdgeCount);

        drawScore(canvas,mEdgeCount);

        drawScoreTxt(canvas,mEdgeCount);
        canvas.translate(-mWidth/2,-mWidth/2);
    }

    /**
     * 画文字分数
     * @param canvas
     * @param edgeCount
     */
    private void drawScoreTxt(Canvas canvas, int edgeCount) {
        float radius=mRadius+mTxtMaxLength/2;
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;

        float averageAngle = 360 / edgeCount;
        float offsetAngle = averageAngle > 0 && edgeCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < edgeCount; position++) {
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) ( Math.sin(nextRadians) * radius);
            nextPointY = (float) ( Math.cos(nextRadians) * radius);

            canvas.drawText(getShowTxt(position),nextPointX,nextPointY+mTxtHeight/2,mScoreTxtPaint);
        }
        for (int i=0;i<edgeCount;i++){

        }
    }


    /**
     *画分数覆盖图
     */
    private void drawScore(Canvas canvas, int edgeCount) {
        mPath.reset();
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;

        float averageAngle = 360 / edgeCount;
        float offsetAngle = averageAngle > 0 && edgeCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < edgeCount; position++) {
            float radius=scores[position]/mMaxScore*mRadius;
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) ( Math.sin(nextRadians) * radius);
            nextPointY = (float) ( Math.cos(nextRadians) * radius);
            if(position == 0){
                mPath.moveTo(nextPointX, nextPointY);
            }else{
                mPath.lineTo(nextPointX, nextPointY);
            }
        }

        mPath.close();
        canvas.drawPath(mPath,mScoreLinePaint);
        canvas.drawPath(mPath,mScoreFillPaint);
    }



    /**
     * 画多边形
     * @param canvas
     * @param edgeCount  多边形边数
     * @param radius 半径
     */
    private void drawPolygon(Canvas canvas, int edgeCount, float radius) {
        mPath.reset();
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;

        float averageAngle = 360 / edgeCount;
        float offsetAngle = averageAngle > 0 && edgeCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < edgeCount; position++) {
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) ( Math.sin(nextRadians) * radius);
            nextPointY = (float) ( Math.cos(nextRadians) * radius);

            if(position == 0){
                mPath.moveTo(nextPointX, nextPointY);
            }else{
                mPath.lineTo(nextPointX, nextPointY);
            }
        }

        mPath.close();
        canvas.drawPath(mPath,mNetsPaint);
    }

    /**
     * 画原点到各顶点的连线
     * @param canvas
     * @param edgeCount
     */
    private void drawCenterLine(Canvas canvas, int edgeCount) {
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;

        float averageAngle = 360 / edgeCount;
        float offsetAngle = averageAngle > 0 && edgeCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < edgeCount; position++) {
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) ( Math.sin(nextRadians) * mRadius);
            nextPointY = (float) ( Math.cos(nextRadians) * mRadius);
            //画圆心到个顶点的连线
            canvas.drawLine(0, 0, nextPointX, nextPointY, mNetsPaint);
        }
    }




    /**
     * 分数文字最大的值
     * @return
     */
    private float getTxtMaxLength(){
        Rect rect=new Rect();
        float maxLength=0;
        for(int i=0;i<mEdgeCount;i++){
            mScoreTxtPaint.getTextBounds(getShowTxt(i),0,getShowTxt(i).length(),rect);
            if(rect.width()>maxLength){
                maxLength=rect.width();
            }
        }
        mTxtHeight=rect.height();
        return maxLength;
    }

    /**
     * 根据分数获取要显示的txt
     * @param index
     * @return
     */
    private String getShowTxt(int index){
        if(mTxtFormat==null){
            return scores[index]+"";
        }
        return  mTxtFormat.originalDataFormat(index,scores[index]);
    }

    TxtFormat mTxtFormat;

    /**
     * 设置用户自定义Format
     * @param txtFormat
     */
    public void setTxtFormat(TxtFormat txtFormat) {
        //根据用户自定义format 重新计算最大宽度 以及半径
        mTxtFormat = txtFormat;
        mTxtMaxLength=getTxtMaxLength();
        mRadius=mWidth/2-mTxtMaxLength;
    }

    /**
     * 格式化显示数值
     */
    public interface TxtFormat{
        public String originalDataFormat(int index, float value);
    }

    /**
     * 设置数据源
     * @param edgeCount 多边形变数
     * @param scores  数据源
     */
    public void setData(int edgeCount,float[] scores){
        if(edgeCount<3){
            Log.e("NetsScoreView","最少需要三条边");
            return;
        }
        if(mEdgeCount!=scores.length){
            Log.e("NetsScoreView","边数和分数数组长度必须一致");
            return;
        }
        mEdgeCount=edgeCount;
        this.scores=scores;
        invalidate();
    }

    /**
     * 设置数据源  默认xml里面变数
     * @param scores  数据源
     */
    public void setData(float[] scores){
        setData(mEdgeCount,scores);
    }

    public void setMaxScore(int maxScore) {
        mMaxScore = maxScore;
        invalidate();
    }

}
