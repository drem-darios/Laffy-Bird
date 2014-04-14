package cs646.assignment4.laffybird.api;

import java.util.Queue;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cs646.assignment4.laffybird.R;
import cs646.assignment4.laffybird.view.GameView;

public class Game extends Activity {

	private boolean running;
	private int width, height;
	private Queue<Pipe> queue;
	private GameView view;
	private Bird bird;
	private Pipe topObstacle;
	private Pipe bottomObstacle;
	private Thread gameThread;

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		this.bird = (Bird) findViewById(R.id.bird);
		this.view = (GameView) findViewById(R.id.gameView);
		this.topObstacle = (Pipe) findViewById(R.id.pipe1);
		this.bottomObstacle = (Pipe) findViewById(R.id.pipe2);
		
		initGame();
		startGame();
	}

	public void startGame() {
		running = true;
		bird.start();
		topObstacle.start();
		bottomObstacle.start();
		gameThread.start();
	}

	public void endGame() {
		running = false;
		bird.stop();
		topObstacle.stop();
		bottomObstacle.stop();
		final View gameOver = findViewById(R.id.gameOver);
		gameOver.getHandler().post(new Runnable() {
		    public void run() {
		    	gameOver.setVisibility(View.VISIBLE);
		    }
		});
	}

	public boolean isRunning() {
		return running;
	}

	public GameView getView() {
		return view;
	}

	private void initGame() {

		initView();
		this.bird.setPosition(this);
		this.view.setGame(this);
		this.gameThread = new Thread(new GameRunner());
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void initView() {
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);

		this.width = screenSize.x;
		this.height = screenSize.y;
		
		setObstacleHeights();
	}

	private void setObstacleHeights() {
		BitmapDrawable ground = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ground1);
		int groundHeight = ground.getBitmap().getHeight();
		BitmapDrawable bird = (BitmapDrawable) this.getResources().getDrawable(R.drawable.bird1);
		int birdHeight = bird.getBitmap().getHeight();
		
		int combinedHeight = (height / 2) - groundHeight;
		int gap = (birdHeight / 2) + 10; // leave a little room
		topObstacle.getLayoutParams().height = combinedHeight - gap;
		bottomObstacle.getLayoutParams().height = combinedHeight - gap;
		
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the game from the ground up.
	 * @return
	 */
	public int getPlayableHeight() {
		return height - findViewById(R.id.ground).getHeight();
	}

	public Bird getBird() {
		return bird;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public boolean detectCollission() {
		if (bird.getY() < 0) {
			return true;
		} else if (bird.getY() + (bird.getHeight() / 2) + 100 > this.getPlayableHeight()) {
			return true;
		} else if (bird.intersect(topObstacle)) {
			return true;
		} else if (bird.intersect(bottomObstacle)) {
			return true;
		} else{ 
			return false;
		}
	}
	
	public class GameRunner implements Runnable {
		@Override
		public void run() {
			runGame();
		}
	}

	private void runGame() {
		while(running) {
			if (detectCollission()) {
				endGame();
			}
			if (topObstacle.isOutOfBounds() || bottomObstacle.isOutOfBounds()) {
				this.topObstacle = (Pipe) findViewById(R.id.pipe1);
				this.bottomObstacle = (Pipe) findViewById(R.id.pipe2);
				
				LinearLayout gv = (LinearLayout)findViewById(R.id.gameView);
				LayoutInflater layoutInflater = getLayoutInflater();
				View view;
				for (int i = 1; i < 101; i++){
		            // Add the text layout to the parent layout
		            view = layoutInflater.inflate(R.layout.text_layout, parentLayout, false);
		 
		            // In order to get the view we have to use the new view with text_layout in it
		            Pipe textView = (Pipe)view.findViewById(R.id.pipe1);
		            // Add the text view to the parent layout
		            gv.addView(textView);
		        }      
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.e("Game.runGame", "Something bad happened while sleeping");
			}
		}
	}

}
