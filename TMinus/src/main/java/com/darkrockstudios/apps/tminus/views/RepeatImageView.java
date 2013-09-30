package com.darkrockstudios.apps.tminus.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.darkrockstudios.apps.tminus.R;

/**
 * Created by Adam on 8/9/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RepeatImageView extends ImageView
{
	private int   m_tileAxis;
	private float m_scaleY;

	public RepeatImageView( Context context )
	{
		super( context );
		m_tileAxis = -1;

		setTiling();
	}

	public RepeatImageView( Context context, AttributeSet attrs )
	{
		this( context, attrs, 0 );
	}

	public RepeatImageView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );

		m_tileAxis = -1;

		TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.RepeatImageView );
		if( a != null )
		{
			m_tileAxis = a.getInt( R.styleable.RepeatImageView_tileAxis, -1 );
			a.recycle();

			setTiling();
		}
	}

	private void setTiling()
	{
		Drawable d = getDrawable();

		if( d != null && d instanceof BitmapDrawable )
		{
			BitmapDrawable bd = (BitmapDrawable) d;

			bd.mutate();
			switch( m_tileAxis )
			{
				// XY
				case 0:
					bd.setTileModeXY( TileMode.REPEAT, TileMode.REPEAT );
					//setScaleX( 1.0f );
					//setScaleY( 1.0f );
					break;
				// X
				case 1:
					bd.setTileModeX( TileMode.REPEAT );
					//setScaleX( 1.0f );
					//setScaleY( 1.33f );
					//setScaleX( 1.0f );
					//stretchY();
					break;
				// Y
				case 2:
					bd.setTileModeY( TileMode.REPEAT );
					//setScaleY( 1.0f );
					//stretchX();
					break;
			}


		}
	}

	private void stretchY()
	{
		float scale = 1.0f;

		Drawable d = getDrawable();

		if( d != null && d instanceof BitmapDrawable )
		{
			BitmapDrawable bd = (BitmapDrawable) d;

			Bitmap bitmap = bd.getBitmap();
			if( bitmap != null )
			{
				final float bitmapHeight = bitmap.getHeight();
				final float viewHeight = getMeasuredHeight();

			}
		}

		//m_scaleY = 1.0;
		//setScaleY( m_scaleY );
	}

	private void stretchX()
	{

	}

	@Override
	public void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		super.onMeasure( widthMeasureSpec, heightMeasureSpec );

		//setScaleY( m_scaleY );
		//setTiling();
	}

	@Override
	public void onLayout( boolean changed, int left, int top, int right, int bottom )
	{
		super.onLayout( changed, left, top, right, bottom );

		//setScaleY( m_scaleY );
		//setTiling();
	}

	@Override
	protected void onSizeChanged( int w, int h, int oldw, int oldh )
	{
		super.onSizeChanged( w, h, oldw, oldh );

		setTiling();
	}
}
