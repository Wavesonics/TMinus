package com.darkrockstudios.apps.tminus.misc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adam on 6/30/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Utilities
{
	public static final String DATE_FORMAT = "HH:mm - dd MMM yyy";

	public static String getFormattedTime( final long timeMs )
	{
		final long day = TimeUnit.MILLISECONDS.toDays( timeMs );
		final long hr = TimeUnit.MILLISECONDS.toHours( timeMs - TimeUnit.DAYS.toMillis( day ) );
		final long min = TimeUnit.MILLISECONDS.toMinutes( timeMs - TimeUnit.DAYS
		                                                                   .toMillis( day ) -
		                                                  TimeUnit.HOURS
		                                                          .toMillis( hr ) );

		String timeStr = day + "d " + hr + "h " + min + "m";
		return timeStr;
	}

	public static String getStatusText( final Launch launch, final Context context )
	{
		final String status;
		if( launch != null )
		{
			switch( launch.status )
			{
				case 1:
					status = context.getString( R.string.COUNTDOWN_launch_status_green );
					break;
				case 2:
					status = context.getString( R.string.COUNTDOWN_launch_status_red );
					break;
				case 3:
					status = context.getString( R.string.COUNTDOWN_launch_status_success );
					break;
				case 4:
					status = context.getString( R.string.COUNTDOWN_launch_status_fail );
					break;
				default:
					status = "";
			}
		}
		else
		{
			status = "";
		}

		return status;
	}

	public static String getDateText( final Date date )
	{
		final SimpleDateFormat formatter = new SimpleDateFormat( DATE_FORMAT );
		return formatter.format( date );
	}

	public static Drawable getLaunchTypeDrawable( final Collection<Mission> missions, final Context context )
	{
		final Drawable type;

		Resources resources = context.getResources();

		if( missions != null && missions.size() > 0 )
		{
			if( missions.size() > 1 )
			{
				Drawable[] types = new Drawable[ missions.size() ];

				Iterator<Mission> it = missions.iterator();
				for( int ii = 0; ii < missions.size(); ++ii )
				{
					Mission mission = it.next();
					types[ ii ] = resources.getDrawable( getLaunchTypeResource( mission.type ) );
				}

				CyclicTransitionDrawable transitionDrawable = new CyclicTransitionDrawable( types );

				final int transitionDuration = resources.getInteger( R.integer.transition_duration_ms );
				final int transitionPause = resources.getInteger( R.integer.transition_pause_ms );
				transitionDrawable.startTransition( transitionDuration, transitionPause );

				type = transitionDrawable;
			}
			else
			{
				Mission mission = missions.iterator().next();
				type = resources.getDrawable( getLaunchTypeResource( mission.type ) );
			}
		}
		else
		{
			type = resources.getDrawable( R.drawable.ic_launch_type_unknown );
		}

		return type;
	}

	public static int getLaunchTypeResource( final int type )
	{
		final int resourceId;

		switch( type )
		{
			case 1:
				resourceId = R.drawable.ic_launch_type_earth_science;
				break;
			case 2:
				resourceId = R.drawable.ic_launch_type_planet_science;
				break;
			case 3:
				resourceId = R.drawable.ic_launch_type_astrophysics;
				break;
			case 4:
				resourceId = R.drawable.ic_launch_type_heliophysics;
				break;
			case 5:
				resourceId = R.drawable.ic_launch_type_human_explore;
				break;
			case 6:
				resourceId = R.drawable.ic_launch_type_robotic_explore;
				break;
			case 7:
				resourceId = R.drawable.ic_launch_type_gov_secrete;
				break;
			case 8:
				resourceId = R.drawable.ic_launch_type_tourism;
				break;
			case 9:
				resourceId = R.drawable.ic_launch_type_unknown;
				break;
			default:
				resourceId = R.drawable.ic_launch_type_unknown;
				break;
		}

		return resourceId;
	}

	public static interface ZoomAnimationHandler
	{
		public void setCurrentAnimator( Animator animator );

		public Animator getCurrentAnimator();
	}

	public static void zoomImage( final ImageView thumbnailImage, final ImageView expandedImage, final View containerView, final ZoomAnimationHandler animatorHandler, final int animationDuration )
	{
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if( animatorHandler.getCurrentAnimator() != null )
		{
			animatorHandler.getCurrentAnimator().cancel();
		}

		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbnailImage.getGlobalVisibleRect( startBounds );
		containerView.getGlobalVisibleRect( finalBounds, globalOffset );
		startBounds.offset( -globalOffset.x, -globalOffset.y );
		finalBounds.offset( -globalOffset.x, -globalOffset.y );

		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
		float startScale;
		if( (float) finalBounds.width() / finalBounds.height()
		    > (float) startBounds.width() / startBounds.height() )
		{
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		}
		else
		{
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbnailImage.setAlpha( 0f );
		expandedImage.setVisibility( View.VISIBLE );

		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		expandedImage.setPivotX( 0f );
		expandedImage.setPivotY( 0f );

		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set
				.play( ObjectAnimator.ofFloat( expandedImage, View.X,
				                               startBounds.left, finalBounds.left ) )
				.with( ObjectAnimator.ofFloat( expandedImage, View.Y,
				                               startBounds.top, finalBounds.top ) )
				.with( ObjectAnimator.ofFloat( expandedImage, View.SCALE_X,
				                               startScale, 1f ) )
				.with( ObjectAnimator.ofFloat( expandedImage,
				                               View.SCALE_Y, startScale, 1f ) );
		set.setDuration( animationDuration );
		set.setInterpolator( new DecelerateInterpolator() );
		set.addListener( new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd( final Animator animation )
			{
				animatorHandler.setCurrentAnimator( null );
			}

			@Override
			public void onAnimationCancel( final Animator animation )
			{
				animatorHandler.setCurrentAnimator( null );
			}
		} );
		set.start();
		animatorHandler.setCurrentAnimator( set );

		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		expandedImage.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick( final View view )
			{
				if( animatorHandler.getCurrentAnimator() != null )
				{
					animatorHandler.getCurrentAnimator().cancel();
				}

				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();

				set.play( ObjectAnimator
						          .ofFloat( expandedImage, View.X, startBounds.left ) )
				   .with( ObjectAnimator
						          .ofFloat( expandedImage,
						                    View.Y, startBounds.top ) )
				   .with( ObjectAnimator
						          .ofFloat( expandedImage,
						                    View.SCALE_X, startScaleFinal ) )
				   .with( ObjectAnimator
						          .ofFloat( expandedImage,
						                    View.SCALE_Y, startScaleFinal ) );
				set.setDuration( animationDuration );
				set.setInterpolator( new DecelerateInterpolator() );
				set.addListener( new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd( final Animator animation )
					{
						thumbnailImage.setAlpha( 1f );
						expandedImage.setVisibility( View.GONE );
						animatorHandler.setCurrentAnimator( null );
					}

					@Override
					public void onAnimationCancel( final Animator animation )
					{
						thumbnailImage.setAlpha( 1f );
						expandedImage.setVisibility( View.GONE );
						animatorHandler.setCurrentAnimator( null );
					}
				} );
				set.start();
				animatorHandler.setCurrentAnimator( set );
			}
		} );
	}
}
