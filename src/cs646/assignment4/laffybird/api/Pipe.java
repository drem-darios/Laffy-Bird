package cs646.assignment4.laffybird.api;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Pipe extends ImageView {

	private int scrollSpeed = 10;
	private int transition;
	private PipeMover mover = new PipeMover();
	
	public Pipe(Context context) {
		this(context, null);
	}
	
	public Pipe(Context context, AttributeSet attrbs) {
		super(context, attrbs);
	}
	
	public void start() {
		move();
	}
	
	public void stop() {
		removeCallbacks(mover);
	}
	
	public void move() {
		// transition the x coord of the pipe
		this.transition = transition - scrollSpeed;
		setTranslationX(transition);
		postDelayed(mover, 50);
	}

	public class PipeMover implements Runnable {
		@Override
		public void run() {
			move();
		}
	}
	
	public boolean isOutOfBounds() {
		return this.getX() < 0;
	}

}
