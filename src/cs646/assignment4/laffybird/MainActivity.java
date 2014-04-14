package cs646.assignment4.laffybird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import cs646.assignment4.laffybird.api.Game;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View view = findViewById(R.id.mainView);
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch(event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN: {
						Intent intent = new Intent(getApplicationContext(), Game.class);
						startActivity(intent);
						return true;
					}
					default:
						return false;
				}
				
			}
		});
	}
	

	
}
