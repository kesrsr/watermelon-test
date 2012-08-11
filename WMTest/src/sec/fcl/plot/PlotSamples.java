package sec.fcl.plot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

public class PlotSamples implements Runnable {
	private XYPlot plot;
	Float[] x;
	Float[] y;
	XYSeries series;
	String name;

	public PlotSamples(XYPlot plot, Float[] x, Float[] y, String name) {
		this.plot = plot;
		this.x = x;
		this.y = y;
		this.name = name;
		series = new SimpleXYSeries(Arrays.asList(x), Arrays.asList(y), name);
	}

	public void plot(int line_color, int point_color) {
		LineAndPointFormatter format = new LineAndPointFormatter(line_color,
				point_color, Color.argb(0, 0, 0, 0));

		plot.addSeries(series, format);

		plot.setDomainStepMode(XYStepMode.SUBDIVIDE);
		plot.setDomainStepValue(series.size());

		// thin out domain/range tick labels so they dont overlap each other:
		plot.setTicksPerDomainLabel(100);
		plot.setTicksPerRangeLabel(3);
		plot.disableAllMarkup();

		plot.setRangeBoundaries(min(y), max(y), BoundaryMode.FIXED);
	}

	@Override
	public void run() {
		try {
			while (true) {
				series = new SimpleXYSeries(Arrays.asList(x), Arrays.asList(y),
						name);
				Thread.sleep(100);

				plot.clear();

//				plot.setRangeBoundaries(min(y), max(y), BoundaryMode.FIXED);
				plot.setRangeBoundaries(-2, 2, BoundaryMode.FIXED);
				plot.setDomainBoundaries(0, 1000, BoundaryMode.FIXED);
				plot.addSeries(series, new LineAndPointFormatter(Color.BLUE,
						null, null));
				plot.postRedraw();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("Plot", "Erros");
		}
	}

	private Float max(Float[] array) {
		if(array == null)
			return 0f;
		Float max = array[0];
		for (int ktr = 0; ktr < array.length; ktr++) {
			if (array[ktr] > max) {
				max = array[ktr];
			}
		}
		return max;
	}

	private Float min(Float[] array) {
		if(array == null)
			return 0f;
		Float min = array[0];
		for (int ktr = 0; ktr < array.length; ktr++) {
			if (array[ktr] < min) {
				min = array[ktr];
			}
		}
		return min;
	}

}
