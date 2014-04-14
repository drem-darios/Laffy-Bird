package cs646.assignment4.laffybird.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Bird extends ImageView {
	private int fallSpeed = 10;
	private int flapSpeed = 30;
	private BirdMover mover = new BirdMover();
	private BirdFlapper flapper = new BirdFlapper();

	public Bird() {
		super(null);
	}

	public Bird(Context context) {
		super(context);
	}

	public Bird(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private AnimationDrawable animation() {
		return (AnimationDrawable) getBackground();
	}

	public void start() {
		animation().start();
		move();
	}

	public void stop() {
		animation().stop();
		removeCallbacks(mover);
	}

	public void move() {
		setTranslationY(this.getY() + fallSpeed);
		postDelayed(mover, 100);
	}
	
	public class BirdMover implements Runnable {
		@Override
		public void run() {
			move();
		}
	}
	
	public class BirdFlapper implements Runnable {
		@Override
		public void run() {
			flap();
		}
	}

	public void onTouch() {
		flap();
	}
	
	public void onRelease() {
		removeCallbacks(flapper);
	}
	
	private void flap() {
		if (this.getY() >  0) {
			setTranslationY(this.getY() - flapSpeed);
			postDelayed(flapper, 150);	
		}
	}

	public void setPosition(Game game) {
		this.setY(game.getPlayableHeight() / 2);
	}

	public boolean intersect(Pipe pipe) {
		int playerX = (int) getX();
		int playerY = (int) getY();
		int pipeX = (int) pipe.getX();
		int pipeY = (int) pipe.getY(); 
		Rect playerRec = new Rect(playerX, playerY, playerX + getWidth(), playerY + getHeight());
		Rect pipeRec = new Rect(pipeX, pipeY, pipeX + pipe.getWidth(), pipeY + pipe.getHeight());
		// not perfect, but good enough estimate of an intersection
		return playerRec.intersect(pipeRec);
	}

}
