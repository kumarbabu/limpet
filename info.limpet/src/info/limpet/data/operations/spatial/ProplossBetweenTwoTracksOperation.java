package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.AcousticStrength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class ProplossBetweenTwoTracksOperation extends TwoTrackOperation
{

	private final class DistanceBetweenOperation extends DistanceOperation
	{
		private DistanceBetweenOperation(String outputName,
				List<IStoreItem> selection, IStore store, String title,
				String description, IBaseTemporalCollection timeProvider)
		{
			super(outputName, selection, store, title, description, timeProvider);
		}

		protected IQuantityCollection<?> getOutputCollection(String title,
				boolean isTemporal)
		{
			final IQuantityCollection<?> res;
			if (isTemporal)
			{
				res = new StockTypes.Temporal.AcousticStrength("Acoustic loss between "
						+ title);
			}
			else
			{
				res = new StockTypes.NonTemporal.AcousticStrength(
						"Acoustic loss between " + title);
			}
			return res;
		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB, Long time)
		{
			final Unit<Dimensionless> outUnits = Dimensionless.UNIT;

			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			double thisDist = calc.getOrthodromicDistance();
			final Measure<Double, Dimensionless> thisRes = Measure.valueOf(thisDist,
					outUnits);

			if (time != null)
			{
				// get the output dataset
				AcousticStrength target2 = (Temporal.AcousticStrength) getOutputs()
						.get(0);
				target2.add(time, thisRes);
			}
			else
			{
				// get the output dataset
				NonTemporal.AcousticStrength target2 = (NonTemporal.AcousticStrength) getOutputs()
						.get(0);
				target2.add(thisRes);
			}
		}
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			// ok, are we doing a tempoarl opeartion?
			if (aTests.suitableForTimeInterpolation(selection))
			{
				// hmm, find the time provider
				final IBaseTemporalCollection timeProvider = aTests
						.getLongestTemporalCollections(selection);

				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination, "Distance between tracks (interpolated)",
						"Calculate distance between two tracks", timeProvider);

				res.add(newC);
			}

			if (aTests.allEqualLengthOrSingleton(selection))
			{
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(null,
						selection, destination,
						"Propagation loss between tracks (indexed)",
						"Calculate distance between two tracks", null);

				res.add(newC);
			}
		}
		return res;
	}
}
