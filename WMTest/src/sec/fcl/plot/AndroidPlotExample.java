package sec.fcl.plot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import java.util.Arrays;

import sec.fcl.R;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class AndroidPlotExample{
	private XYPlot mySimpleXYPlot;

	public AndroidPlotExample(Activity context) {
		// Initialize our XYPlot reference:
//		mySimpleXYPlot = (XYPlot) context.findViewById(R.id.mySimpleXYPlot);
		// Create two arrays of y-values to plot:
		Number[] series1Numbers = { 1, 8, 5, 2, 7, 4 };
		Number[] series2Numbers = { 4, 6, 3, 8, 2, 10 };
		// Turn the above arrays into XYSeries:
		XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), // Turn
																				// array
																				// to
																				// List
																				// for
																				// SimpleXYSeries
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY: use
														// element index as x
														// value
				"Series1"); // Set the display title of the series
		// Same as above, for series2
		XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
		// Formatter draws series using LineAndPointRenderer:
		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(0, 200, 0), // line color
				Color.rgb(0, 100, 0), // point color
				Color.argb(0, 0, 0, 0)); // fill color (optional)
		// Add series1 to the xyplot:
		mySimpleXYPlot.addSeries(series1, series1Format);
		// Same as above, with series2:
		mySimpleXYPlot.addSeries(
				series2,
				new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0,
						100), Color.argb(0, 0, 0, 0)));
		// Reduce the number of range labels
		mySimpleXYPlot.setTicksPerRangeLabel(3);
		// AndroidPlot displays developer guides to aid in plot layout
		// To get rid of them call disableAllMarkup():
		mySimpleXYPlot.disableAllMarkup();
	}
}
