package info.limpet.data.operations;

import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes.ILocations;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.TemporalLocation;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.opengis.geometry.Geometry;

public class CollectionComplianceTests
{

	/**
	 * check if the specific number of arguments are supplied
	 * 
	 * @param selection
	 * @param num
	 * @return
	 */
	public boolean exactNumber(final List<? extends IStoreItem> selection,
			final int num)
	{
		return selection.size() == num;
	}

	/**
	 * check if the series are all non locations
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allNonLocation(List<? extends IStoreItem> selection)
	{
		// are they all non location?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				Class<?> theClass = thisC.storedClass();
				if (Geometry.class.equals(theClass))
				{
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all non locations
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allLocation(List<? extends IStoreItem> selection)
	{
		// are they all non location?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				Class<?> theClass = thisC.storedClass();
				if (!Geometry.class.equals(theClass))
				{
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}

		}
		return allValid;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allNonTemporal(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isTemporal())
				{
					// oops, no
					allValid = false;
					break;
				}
				else
				{
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allNonQuantity(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity())
				{
					// oops, no
					allValid = false;
					break;
				}
				else
				{
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * determine if these datasets are suited to a temporal operation - where we
	 * interpolate time values
	 * 
	 * @param selection
	 * @return
	 */
	public boolean suitableForTimeInterpolation(
			List<? extends IStoreItem> selection)
	{
		// are suitable
		boolean suitable = selection.size() >= 2;

		Long startT = null;
		Long endT = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity() || thisC instanceof ILocations)
				{
					if (thisC.isTemporal())
					{
						IBaseTemporalCollection tq = (IBaseTemporalCollection) thisC;
						long thisStart = tq.start();
						long thisEnd = tq.finish();

						if (startT == null)
						{
							startT = thisStart;
							endT = thisEnd;
						}
						else
						{
							// check the overlap
							if (thisStart > endT || thisEnd < startT)
							{
								suitable = false;
								break;
							}
						}
					}
				}
				else
				{
					// oops, no
					suitable = false;
					break;
				}
			}
			else
			{
				suitable = false;
				break;
			}

		}
		return suitable && (startT != null);
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allQuantity(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity())
				{
				}
				else
				{
					// oops, no
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}

		}
		return allValid;
	}

	/**
	 * check if the series are all quantity datasets
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean nonEmpty(List<? extends IStoreItem> selection)
	{
		return selection.size() > 0;
	}

	/**
	 * check if the series are all have equal dimensions
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualDimensions(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = false;
		Dimension theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity())
				{
					IQuantityCollection<?> qc = (IQuantityCollection<?>) thisC;
					Dimension thisD = qc.getDimension();
					if (theD == null)
					{
						theD = thisD;
					}
					else
					{
						if (thisD.equals(theD))
						{
							// all fine.
							allValid = true;
						}
						else
						{
							allValid = false;
							break;
						}
					}
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series all have equal units
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualUnits(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		Unit<?> theD = null;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isQuantity())
				{
					IQuantityCollection<?> qc = (IQuantityCollection<?>) thisC;
					Unit<?> thisD = qc.getUnits();
					if (theD == null)
					{
						theD = thisD;
					}
					else
					{
						if (thisD.equals(theD))
						{
							// all fine.
						}
						else
						{
							allValid = false;
							break;
						}
					}
				}
				else
				{
					// oops, no
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}

		}
		return allValid;
	}


	/**
	 * check if the series are all time series datasets (temporal)
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean hasTemporal(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = false;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isTemporal())
				{
					allValid = true;
					break;
				}
			}
		}
		return allValid;
	}

	
	/**
	 * check if the series are all time series datasets (temporal)
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allTemporal(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.isTemporal())
				{
				}
				else
				{
					// oops, no
					allValid = false;
					break;
				}
			}
			else
			{
				// oops, no
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * check if the series are all of equal length, or singletons
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualLengthOrSingleton(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;

				int thisSize = thisC.size();

				// valid, check the size
				if (size == -1)
				{
					// ok, is this a singleton?
					if (thisSize != 1)
					{
						// nope, it's a real array store it.
						size = thisSize;
					}
				}
				else
				{
					if ((thisSize != size) && (thisSize != 1))
					{
						// oops, no
						allValid = false;
						break;
					}
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}

		return allValid;
	}

	/**
	 * check if the series are all of equal length
	 * 
	 * @param selection
	 * @return true/false
	 */
	public boolean allEqualLength(List<? extends IStoreItem> selection)
	{
		// are they all temporal?
		boolean allValid = true;
		int size = -1;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;

				// valid, check the size
				if (size == -1)
				{
					size = thisC.size();
				}
				else
				{
					if (size != thisC.size())
					{
						// oops, no
						allValid = false;
						break;
					}
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}

		return allValid;
	}

	public boolean allCollections(List<? extends IStoreItem> selection)
	{
		boolean res = true;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (!(storeItem instanceof ICollection))
			{
				res = false;
				break;
			}
		}
		return res;
	}

	public boolean numberOfGroups(List<IStoreItem> selection, final int count)
	{
		int res = 0;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (storeItem instanceof StoreGroup)
			{
				res++;
			}
		}
		return res == count;
	}

	public boolean allGroups(List<IStoreItem> selection)
	{
		boolean res = true;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (!(storeItem instanceof StoreGroup))
			{
				res = false;
				break;
			}
		}
		return res;
	}

	/**
	 * convenience test to verify if children of the supplied item can all be
	 * treated as tracks
	 * 
	 * @param selection
	 *          one or more group objects
	 * @return yes/no
	 */
	public boolean hasNumberOfTracks(List<IStoreItem> selection, final int number)
	{
		int count = 0;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			boolean valid = true;
			if (storeItem instanceof StoreGroup)
			{
				// ok, check the contents
				StoreGroup group = (StoreGroup) storeItem;
				List<IStoreItem> kids = group.children();

				// ok, keep looping through, to check we have the right types
				if (!isPresent(kids, METRE.divide(SECOND).getDimension()))
				{
					valid = false;
				}

				// ok, keep looping through, to check we have the right types
				if (!isPresent(kids, SI.RADIAN.getDimension()))
				{
					valid = false;
				}

				if (!hasLocation(kids))
				{
					valid = false;
				}

				// special case: we can miss out course & speed if there's just a single
				// stationery location
				if (!valid)
				{
					// is there a singleton location?
					if (hasSingletonLocation(kids))
					{
						valid = true;
					}
				}
			}
			else
			{
				valid = false;
			}
			if (valid)
				count++;

		}
		return count == number;
	}

	private boolean hasSingletonLocation(List<IStoreItem> kids)
	{
		boolean res = false;

		Iterator<IStoreItem> iter = kids.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof ILocations)
			{
				ILocations locs = (ILocations) item;
				if (locs.getLocations().size() == 1)
				{
					res = true;
					break;
				}
			}
		}

		return res;
	}

	/**
	 * convenience test to verify if children of the supplied item can all be
	 * treated as tracks
	 * 
	 * @param selection
	 *          one or more group objects
	 * @return yes/no
	 */
	public boolean allChildrenAreTracks(List<IStoreItem> selection)
	{
		boolean res = true;
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (storeItem instanceof StoreGroup)
			{
				// ok, check the contents
				StoreGroup group = (StoreGroup) storeItem;
				List<IStoreItem> kids = group.children();

				// ok, keep looping through, to check we have the right types
				if (!isPresent(kids, METRE.divide(SECOND).getDimension()))
				{
					res = false;
					break;
				}

				// ok, keep looping through, to check we have the right types
				if (!isPresent(kids, SI.RADIAN.getDimension()))
				{
					res = false;
					break;
				}

				if (!hasLocation(kids))
				{
					res = false;
					break;
				}

			}
		}
		return res;
	}

	/**
	 * get any layers that we contain location data
	 * 
	 * @param selection
	 *          one or more group objects
	 * @return yes/no
	 */
	public ArrayList<StoreGroup> getChildTrackGroups(List<IStoreItem> selection)
	{
		ArrayList<StoreGroup> res = new ArrayList<StoreGroup>();
		Iterator<? extends IStoreItem> iter = selection.iterator();
		while (iter.hasNext())
		{
			IStoreItem storeItem = iter.next();
			if (storeItem instanceof StoreGroup)
			{
				// ok, check the contents
				StoreGroup group = (StoreGroup) storeItem;
				List<IStoreItem> kids = group.children();

				Iterator<IStoreItem> kIter = kids.iterator();
				while (kIter.hasNext())
				{
					IStoreItem storeItem2 = (IStoreItem) kIter.next();
					if (storeItem2 instanceof ILocations)
					{
						res.add(group);
					}
				}

			}
		}
		return res;
	}

	/**
	 * see if a collection of the specified dimension is present
	 * 
	 * @param items
	 *          to check
	 * @param dimension
	 *          we're looking for
	 * @return yes/no
	 */
	private boolean isPresent(List<IStoreItem> kids, Dimension dim)
	{
		boolean res = false;

		Iterator<IStoreItem> iter = kids.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> coll = (IQuantityCollection<?>) item;
				if (coll.getDimension().equals(dim))
				{
					res = true;
					break;
				}
			}
		}

		return res;
	}

	/**
	 * see if a collection of the specified dimension is present
	 * 
	 * @param items
	 *          to check
	 * @param dimension
	 *          we're looking for
	 * @return yes/no
	 */
	private boolean hasLocation(List<IStoreItem> kids)
	{
		boolean res = false;

		Iterator<IStoreItem> iter = kids.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof ILocations)
			{
				res = true;
				break;
			}
		}

		return res;
	}

	/**
	 * find the item in the list with the specified dimension
	 * 
	 * @param kids
	 *          items to examine
	 * @param dimension
	 *          dimension we need to be present
	 * @return yes/no
	 */
	public IQuantityCollection<?> collectionWith(List<IStoreItem> kids,
			Dimension dimension, final boolean walkTree)
	{
		IQuantityCollection<?> res = null;

		Iterator<IStoreItem> iter = kids.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof StoreGroup && walkTree)
			{
				StoreGroup group = (StoreGroup) item;
				res = collectionWith(group.children(), dimension, walkTree);
				if (res != null)
				{
					break;
				}

			}
			else if (item instanceof IQuantityCollection<?>)
			{
				IQuantityCollection<?> coll = (IQuantityCollection<?>) item;
				if (coll.getDimension().equals(dimension))
				{
					res = coll;
					break;
				}
			}
		}

		return res;
	}

	/**
	 * check the list has a location collection
	 * 
	 * @param kids
	 *          items to examine
	 * @param dimension
	 *          dimension we need to be present
	 * @return yes/no
	 */
	public IObjectCollection<?> someHaveLocation(List<IStoreItem> kids)
	{
		IObjectCollection<?> res = null;

		Iterator<IStoreItem> iter = kids.iterator();
		while (iter.hasNext())
		{
			IStoreItem item = iter.next();
			if (item instanceof StoreGroup)
			{
				StoreGroup group = (StoreGroup) item;
				res = someHaveLocation(group.children());
				if (res != null)
				{
					break;
				}
			}
			else if (item instanceof IObjectCollection<?>)
			{
				IObjectCollection<?> coll = (IObjectCollection<?>) item;
				if (coll instanceof ILocations)
				{
					res = coll;
					break;
				}
			}
		}

		return res;
	}

	public IBaseTemporalCollection getLongestTemporalCollections(
			List<IStoreItem> selection)
	{
		// find the longest time series.
		Iterator<IStoreItem> iter = selection.iterator();
		IBaseTemporalCollection longest = null;

		while (iter.hasNext())
		{
			ICollection thisC = (ICollection) iter.next();
			if (thisC.isTemporal()
					&& (thisC.isQuantity() || thisC instanceof ILocations))
			{
				IBaseTemporalCollection tqc = (IBaseTemporalCollection) thisC;

				// check it has some data
				if (tqc.getTimes().size() > 0)
				{
					if (longest == null)
					{
						longest = tqc;
					}
					else
					{
						// store the longest one
						ICollection asColl = (ICollection) longest;
						longest = thisC.size() > asColl.size() ? tqc : longest;
					}
				}
			}
		}
		return longest;
	}

	public boolean allHaveData(List<IStoreItem> selection)
	{
		// are they all non location?
		boolean allValid = true;

		for (int i = 0; i < selection.size(); i++)
		{
			IStoreItem thisI = selection.get(i);
			if (thisI instanceof ICollection)
			{
				ICollection thisC = (ICollection) thisI;
				if (thisC.size() == 0)
				{
					allValid = false;
					break;
				}
			}
			else
			{
				allValid = false;
				break;
			}
		}
		return allValid;
	}

	/**
	 * find the time period for overlapping data
	 * 
	 * @param items
	 * @return
	 */
	public TimePeriod getBoundingTime(final Collection<ICollection> items)
	{
		TimePeriod res = null;

		Iterator<ICollection> iter = items.iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();

			// allow for empty value. sometimes our logic allows null objects for some
			// data types
			if (iCollection != null)
			{
				if (iCollection.isTemporal())
				{
					IBaseTemporalCollection timeC = (IBaseTemporalCollection) iCollection;
					// check it has some data
					if (timeC.getTimes().size() > 0)
					{
						if (res == null)
						{
							res = new TimePeriod(timeC.start(), timeC.finish());
						}
						else
						{
							res.startTime = Math.max(res.startTime, timeC.start());
							res.endTime = Math.min(res.endTime, timeC.finish());
						}
					}
				}
			}
		}

		return res;
	}

	/**
	 * find the best collection to use as a time-base. Which collection has the
	 * most values within the specified time period?
	 * 
	 * @param period
	 *          (optional) period in which we count valid times
	 * @param items
	 *          list of datasets we're examining
	 * @return most suited collection
	 */
	public IBaseTemporalCollection getOptimalTimes(TimePeriod period,
			Collection<ICollection> items)
	{
		IBaseTemporalCollection res = null;
		long resScore = 0;

		Iterator<ICollection> iter = items.iterator();
		while (iter.hasNext())
		{
			ICollection iCollection = (ICollection) iter.next();

			// occasionally we may store a null dataset, since it is optional in some
			// circumstances
			if (iCollection != null)
			{
				if (iCollection.isTemporal())
				{
					IBaseTemporalCollection timeC = (IBaseTemporalCollection) iCollection;
					Iterator<Long> times = timeC.getTimes().iterator();
					int score = 0;
					while (times.hasNext())
					{
						long long1 = (long) times.next();
						if ((period == null) || period.contains(long1))
						{
							score++;
						}
					}

					if ((res == null) || (score > resScore))
					{
						res = timeC;
						resScore = score;
					}
				}
			}
		}

		return res;
	}

	/**
	 * retrieve the value at the specified time (even if it's a non-temporal
	 * collection)
	 * 
	 * @param iCollection
	 *          set of locations to use
	 * @param thisTime
	 *          time we're need a location for
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public double valueAt(ICollection iCollection, long thisTime,
			Unit<?> requiredUnits)
	{
		Measurable<Quantity> res;

		// just check it's not an empty set, since we return zero for empty dataset
		if (iCollection == null)
		{
			return 0;
		}
		else if (iCollection.isQuantity())
		{
			IQuantityCollection<?> iQ = (IQuantityCollection<?>) iCollection;

			// just check it's not empty (which can happen during edits)
			if (iQ.size() == 0)
			{
				return 0;
			}

			if (iCollection.isTemporal())
			{
				TemporalQuantityCollection<?> tQ = (TemporalQuantityCollection<?>) iCollection;
				res = (Measurable<Quantity>) tQ.interpolateValue(thisTime,
						InterpMethod.Linear);
			}
			else
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iCollection;
				res = (Measurable<Quantity>) qC.getValues().iterator().next();
			}

			if (res != null)
			{
				UnitConverter converter = iQ.getUnits().getConverterTo(requiredUnits);
				Unit<?> sourceUnits = iQ.getUnits();
				double doubleValue = res.doubleValue((Unit<Quantity>) sourceUnits);
				double result = converter.convert(doubleValue);
				return result;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			throw new RuntimeException("Tried to get value of non quantity data type");
		}
	}

	/**
	 * retrieve the location at the specified time (even if it's a non-temporal
	 * collection)
	 * 
	 * @param iCollection
	 *          set of locations to use
	 * @param thisTime
	 *          time we're need a location for
	 * @return
	 */
	public Geometry locationFor(ICollection iCollection, Long thisTime)
	{
		Geometry res = null;
		if (iCollection.isTemporal())
		{
			TemporalLocation tLoc = (TemporalLocation) iCollection;
			res = tLoc.interpolateValue(thisTime, InterpMethod.Linear);
		}
		else
		{
			NonTemporal.Location tLoc = (info.limpet.data.impl.samples.StockTypes.NonTemporal.Location) iCollection;
			if (tLoc.size() > 0)
			{
				res = tLoc.getValues().iterator().next();
			}
		}
		return res;
	}

	public static class TimePeriod
	{
		public long startTime;
		public long endTime;

		public TimePeriod(final long tStart, final long tEnd)
		{
			startTime = tStart;
			endTime = tEnd;
		}

		public boolean invalid()
		{
			return endTime < startTime;
		}

		public boolean contains(long time)
		{
			return ((startTime <= time) && (endTime >= time));
		}
	}
}
