package sec.fcl.view;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
	Paint paint = new Paint();
	Vector<Float> samples;
	
	int ratio = 100;

	public DrawView(Context context, Vector<Float> samples) {
		super(context);
		this.samples = samples;
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(2);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		canvas.drawLine(0, height / 4, width, height / 4, paint);
		canvas.drawLine(0, 0, 0, height / 2, paint);

		for (int i = 0; i < samples.size(); i++) {
			float x = (float) i * width / samples.size();
			float y = height / 4 - samples.get(i) * ratio * height / 4;
			canvas.drawLine(x, height / 4, x, y, paint);
		}
	}
}