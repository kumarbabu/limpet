package info.limpet.data.operations.spatial;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.Angle_Degrees;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.primitive.Point;

public class GenerateCourseOperation implements IOperation<IStoreItem>
{

	protected static abstract class DistanceOperation extends
			AbstractCommand<IStoreItem>
	{

		public DistanceOperation(String outputName, List<IStoreItem> selection,
				IStore store, String title, String description)
		{
			super(title, description, outputName, store, false, false, selection);
		}

		@Override
		public void execute()
		{
			// get the unit
			List<IStoreItem> outputs = new ArrayList<IStoreItem>();

			// ok, generate the new series
			for (int i = 0; i < getInputs().size(); i++)
			{
				IQuantityCollection<?> target = getOutputCollection(getTitle());
				outputs.add(target);
				// store the output
				super.addOutput(target);
			}

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			Iterator<IStoreItem> iter = getInputs().iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = (ICollection) iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			getStore().addAll(outputs);
		}

		abstract protected IQuantityCollection<?> getOutputCollection(
				String trackList);

		@Override
		protected void recalculate()
		{
			// clear out the lists, first
			Iterator<IStoreItem> iter = getOutputs().iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			// update the results
			performCalc(getOutputs());
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<IStoreItem> outputs)
		{
			// get a calculator to use
			final GeodeticCalculator calc = GeoSupport.getCalculator();

			Iterator<IStoreItem> iter = inputs.iterator();
			Iterator<IStoreItem> oIter = outputs.iterator();
			while (iter.hasNext())
			{
				Temporal.Location thisTrack = (Temporal.Location) iter.next();
				IStoreItem thisOut = oIter.next();
				
				// ok, walk through it
				Iterator<Geometry> pITer = thisTrack.getLocations().iterator();
				Iterator<Long> tIter = thisTrack.getTimes().iterator();

				// remember the last value
				long lastTime = 0;
				Point lastLocation = null;

				while (pITer.hasNext())
				{
					Point geometry = (Point) pITer.next();
					long thisTime = tIter.next();

					if (lastLocation != null)
					{
						calcAndStore(thisOut, calc, lastTime, lastLocation, thisTime, geometry);
					}

					// and remember the values
					lastLocation = geometry;
					lastTime = thisTime;

				}
			}
		}

		abstract protected void calcAndStore(IStoreItem thisOut, final GeodeticCalculator calc,
				final long timeA, final Point locA, final long timeB, final Point locB);
	}

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	protected boolean appliesTo(List<IStoreItem> selection)
	{
		boolean allLocation = aTests.allLocation(selection);
		boolean nonEmpty = aTests.nonEmpty(selection);
		boolean allTemporal = aTests.allTemporal(selection);

		return (nonEmpty && allLocation & allTemporal);
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{

			int len = selection.size();
			final String title;
			if (len > 1)
			{
				title = "Generate course for track";
			}
			else
			{
				title = "Generate course for tracks";
			}

			ICommand<IStoreItem> newC = new DistanceOperation(null, selection,
					destination, "Calculated course", title)
			{

				protected IQuantityCollection<?> getOutputCollection(String title)
				{
					return new StockTypes.Temporal.Angle_Degrees("Bearing between "
							+ title);
				}

				protected void calcAndStore(IStoreItem output, final GeodeticCalculator calc,
						long lastTime, final Point locA, long thisTime, final Point locB)
				{
					// get the output dataset
					Temporal.Angle_Degrees target = (Angle_Degrees) output;

					// now find the range between them
					calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0),
							locA.getCentroid().getOrdinate(1));
					calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
							locB.getCentroid().getOrdinate(1));
					double thisDist = Math.random() * 12;
					target.add(thisTime, Measure.valueOf(thisDist, target.getUnits()));
				}
			};

			res.add(newC);
		}

		return res;
	}

}