package info.limpet.rcp.editors;

import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.METRES_PER_SQUARE_SECOND;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IOperation;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.DeleteCollectionOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.GenerateDummyDataOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SimpleMovingAverageOperation;
import info.limpet.data.operations.SubtractQuantityOperation;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.data.operations.spatial.BearingBetweenTracksOperation;
import info.limpet.data.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.data.operations.spatial.GenerateCourseOperation;
import info.limpet.rcp.analysis_view.AnalysisView;
import info.limpet.rcp.data_frequency.DataFrequencyView;
import info.limpet.rcp.operations.ShowInNamedView;
import info.limpet.rcp.time_frequency.TimeFrequencyView;
import info.limpet.rcp.xy_plot.XyPlotView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OperationsLibrary
{
	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();

		res.put("Arithmetic", getArithmetic());
		res.put("Conversions", getConversions());
		res.put("Administration", getAdmin());
		res.put("Analysis", getAnalysis());
		res.put("Spatial", getSpatial());
		return res;

	}

	private static List<IOperation<?>> getAnalysis()
	{
		List<IOperation<?>> analysis = new ArrayList<IOperation<?>>();
		analysis.add(new ShowInNamedView("Show in XY Plot View", XyPlotView.ID)
		{
			protected boolean appliesTo(List<IStoreItem> selection)
			{
				return getTests().nonEmpty(selection)
						&& getTests().allQuantity(selection);
			}
		});
		analysis.add(new ShowInNamedView("Show in Time Frequency View",
				TimeFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Data Frequency View",
				DataFrequencyView.ID));
		analysis.add(new ShowInNamedView("Show in Analysis View", AnalysisView.ID));
		return analysis;
	}

	public static List<IOperation<?>> getTopLevel()
	{
		List<IOperation<?>> topLevel = new ArrayList<IOperation<?>>();
		topLevel.add(new DeleteCollectionOperation());
		return topLevel;
	}

	private static List<IOperation<?>> getAdmin()
	{
		List<IOperation<?>> admin = new ArrayList<IOperation<?>>();
		admin.add(new GenerateDummyDataOperation("small", 20));
		admin.add(new GenerateDummyDataOperation("large", 1000));
		admin.add(new GenerateDummyDataOperation("monster", 1000000));

		return admin;
	}

	private static List<IOperation<?>> getArithmetic()
	{
		List<IOperation<?>> arithmetic = new ArrayList<IOperation<?>>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation<>());
		arithmetic.add(new SubtractQuantityOperation<>());
		arithmetic.add(new DivideQuantityOperation());
		arithmetic.add(new SimpleMovingAverageOperation(3));
		return arithmetic;
	}

	private static List<IOperation<?>> getSpatial()
	{
		List<IOperation<?>> spatial = new ArrayList<IOperation<?>>();
		spatial.add(new DistanceBetweenTracksOperation());
		spatial.add(new BearingBetweenTracksOperation());
		spatial.add(new GenerateCourseOperation());
		return spatial;
	}


	private static List<IOperation<?>> getConversions()
	{
		List<IOperation<?>> conversions = new ArrayList<IOperation<?>>();

		// Length
		conversions.add(new UnitConversionOperation(METRE));

		// Time
		conversions.add(new UnitConversionOperation(SECOND));
		conversions.add(new UnitConversionOperation(MINUTE));

		// Speed
		conversions.add(new UnitConversionOperation(METRES_PER_SECOND));

		// Acceleration
		conversions
				.add(new UnitConversionOperation(METRES_PER_SQUARE_SECOND));

		// Temperature
		conversions.add(new UnitConversionOperation(CELSIUS));

		// Angle
		conversions.add(new UnitConversionOperation(RADIAN));
		conversions.add(new UnitConversionOperation(StockTypes.DEGREE_ANGLE));
		return conversions;
	}
}
