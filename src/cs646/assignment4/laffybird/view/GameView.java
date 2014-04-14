package cs646.assignment4.laffybird.view;

import cs646.assignment4.laffybird.api.Game;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

	private Game game;
	
	public GameView() {
		super(null);
	}
	
	public GameView(Context context) {
		this(context, null);
	}

	public GameView(Context context, AttributeSet attrbs) {
		super(context, attrbs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (game.isRunning()) {
					game.getBird().onTouch();
					return true;	
				}	
			}
			case MotionEvent.ACTION_UP: {
				game.getBird().onRelease();
			}
			default: 
				return false;
		}
	}

	public void setGame(Game game) {
		this.game = game;
	}

}
