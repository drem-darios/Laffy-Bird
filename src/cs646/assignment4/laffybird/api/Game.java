package cs646.assignment4.laffybird.api;

import java.util.LinkedList;
import java.util.Queue;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cs646.assignment4.laffybird.R;
import cs646.assignment4.laffybird.view.GameView;

public class Game extends Activity {

	private static final int MIN_PIPE_HEIGHT = 50;
	private static final int MAX_PIPES = 5;
	private boolean running;
	private int width, height;
	private GameView view;
	private Bird bird;
	private Queue<Pipe> topObstacles = new LinkedList<Pipe>();
	private Queue<Pipe> bottomObstacles = new LinkedList<Pipe>();
	private Pipe topObstacle;
	private Pipe bottomObstacle;
	private Thread gameThread;
	private RelativeLayout layout;

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		this.bird = (Bird) findViewById(R.id.bird);
		this.view = (GameView) findViewById(R.id.gameView);
		this.layout = (RelativeLayout) findViewById(R.id.relativeLayout);
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
//		layout.postDelayed(new PipeBuilder(), 1000);
	}

	public void endGame() {
		running = false;
		bird.stop();
		topObstacle.stop();
		bottomObstacle.stop();
		final View gameOver = findViewById(R.id.gameOver);
		if (gameOver.getHandler() != null) {
			gameOver.getHandler().post(new Runnable() {
			    public void run() {
			    	gameOver.setVisibility(View.VISIBLE);
			    }
			});	
		}
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
//		buildPipeLists();
	}

//	private void buildPipeLists() {
//		
//		for (int i = 0; i < MAX_PIPES; i++) {
//			Pipe topPipe = new Pipe(this);
//			topPipe.setImageResource(R.drawable.pipe1);
//			topPipe.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
//			
//			RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			topParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
////			topParams.addRule(RelativeLayout.ROTATION);
//			topPipe.setLayoutParams(topParams);
////			new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
////					LayoutParams.WRAP_CONTENT)
//			Pipe bottomPipe = new Pipe(this);
//			bottomPipe.setImageResource(R.drawable.pipe2);
//			bottomPipe.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
//			
//			RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			bottomParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//			bottomParams.addRule(RelativeLayout.ABOVE, R.id.ground);
//			
//			bottomPipe.setLayoutParams(bottomParams);
//			
//			setObstacleHeights(topPipe, bottomPipe);
//			topObstacles.add(topPipe);
//			bottomObstacles.add(bottomPipe);	
//		}
//	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void initView() {
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);

		this.width = screenSize.x;
		this.height = screenSize.y;
		setObstacleHeights();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setObstacleHeights() {
		BitmapDrawable ground = (BitmapDrawable) this.getResources().getDrawable(R.drawable.ground1);
		int groundHeight = ground.getBitmap().getHeight();
		BitmapDrawable bird = (BitmapDrawable) this.getResources().getDrawable(R.drawable.bird1);
		int birdHeight = bird.getBitmap().getHeight();
		
		int combinedHeight = (height / 2) - groundHeight;
		int gap = (birdHeight / 2) + 10; // leave a little room
		// take into consideration the gap for both top and bottom
		int topHeight = MIN_PIPE_HEIGHT + (int)(Math.random() * (combinedHeight - (gap * 2)));
		int difference = combinedHeight - topHeight;
		int bottomHeight = combinedHeight + difference;
		
		topObstacle.getLayoutParams().height = topHeight - gap;
		bottomObstacle.getLayoutParams().height = bottomHeight - gap;
		topObstacle.setX(width);
		bottomObstacle.setX(width);
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
	
	public class PipeBuilder implements Runnable {
		@Override
		public void run() {
			addPipes();
		}
	}
	
	private void addPipes() {
		if (running && !topObstacles.isEmpty() && !bottomObstacles.isEmpty()) {
			Pipe top = topObstacles.remove();
			Pipe bottom = bottomObstacles.remove();
			layout.addView(top);
			layout.addView(bottom);
			
			top.start();
			bottom.start();
			layout.postDelayed(new PipeBuilder(), 1000);	
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void runGame() {
		while(running) {
			if (detectCollission()) {
				endGame();
			}
			if (topObstacle.getTranslationX() < (width * -1) || bottomObstacle.getTranslationX() < (width * -1)) {
//				topObstacle = getNextTopObstacle();
//				bottomObstacle = getNextBottomObstacle();
				endGame();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.e("Game.runGame", "Something bad happened while sleeping");
			}
		}
	}

	private Pipe getNextBottomObstacle() {
		return null;
	}

	private Pipe getNextTopObstacle() {
		return null;
	}

}
