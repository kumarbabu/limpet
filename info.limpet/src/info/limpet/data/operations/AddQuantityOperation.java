package info.limpet.data.operations;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.ITemporalQuantityCollection.InterpMethod;

import java.util.Collection;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;

public class AddQuantityOperation<Q extends Quantity> extends
		CoreQuantityOperation<Q> implements IOperation<IQuantityCollection<Q>>
{

	@Override
	protected void addInterpolatedCommands(
			List<IQuantityCollection<Q>> selection, IStore destination,
			Collection<ICommand<IQuantityCollection<Q>>> res, IContext context)
	{
		ITemporalQuantityCollection<Q> longest = getLongestTemporalCollections(selection);

		if (longest != null)
		{
			ICommand<IQuantityCollection<Q>> newC = new AddQuantityValues(
					"Add numeric values in provided series (interpolated)", selection,
					destination, longest, context);
			res.add(newC);
		}
	}

	protected void addIndexedCommands(List<IQuantityCollection<Q>> selection,
			IStore destination, Collection<ICommand<IQuantityCollection<Q>>> res,
			IContext context)
	{
		ICommand<IQuantityCollection<Q>> newC = new AddQuantityValues(
				"Add numeric values in provided series (indexed)", selection,
				destination, context);
		res.add(newC);
	}

	protected boolean appliesTo(List<IQuantityCollection<Q>> selection)
	{
		boolean nonEmpty = aTests.nonEmpty(selection);
		boolean allQuantity = aTests.allQuantity(selection);
		boolean suitableLength = aTests.allTemporal(selection)
				|| aTests.allEqualLengthOrSingleton(selection);
		boolean equalDimensions = aTests.allEqualDimensions(selection);
		boolean equalUnits = aTests.allEqualUnits(selection);

		return (nonEmpty && allQuantity && suitableLength && equalDimensions && equalUnits);
	}

	public class AddQuantityValues extends CoreQuantityCommand
	{
		public AddQuantityValues(String name,
				List<IQuantityCollection<Q>> selection, IStore store, IContext context)
		{
			this(name, selection, store, null, context);
		}

		public AddQuantityValues(String name,
				List<IQuantityCollection<Q>> selection, IStore destination,
				ITemporalQuantityCollection<Q> timeProvider, IContext context)
		{
			super(name, "Add datasets", destination, false, false, selection,
					timeProvider, context);
		}

		@Override
		protected Double calcThisElement(int elementCount)
		{
			Double thisResult = null;

			for (int seriesCount = 0; seriesCount < inputs.size(); seriesCount++)
			{
				IQuantityCollection<Q> thisC = inputs.get(seriesCount);
				Measurable<Q> thisV = thisC.size() == 1 ? thisC.getValues().get(0)
						: (Measurable<Q>) thisC.getValues().get(elementCount);

				// is this the first field?
				if (thisResult == null)
				{
					thisResult = thisV.doubleValue(thisC.getUnits());
				}
				else
				{
					thisResult += thisV.doubleValue(thisC.getUnits());
				}
			}
			return thisResult;
		}

		@Override
		protected Double calcThisInterpolatedElement(long time)
		{
			Double thisResult = null;

			for (int seriesCount = 0; seriesCount < inputs.size(); seriesCount++)
			{
				IQuantityCollection<Q> thisC = (IQuantityCollection<Q>) inputs
						.get(seriesCount);
				
				final Measurable<Q> thisV;
				
				if(thisC.isTemporal())
				{
					// find the value to use
					ITemporalQuantityCollection<Q> tq= (ITemporalQuantityCollection<Q>) thisC;
					thisV = tq.interpolateValue(time, InterpMethod.Linear);
					
				}
				else
				{
					if(thisC.size() == 1)
					{
						// ok, it's a singleton that we're applying to all values
						thisV = thisC.getValues().get(0);
					}
					else
					{
						throw new RuntimeException("We should not be adding a non-singleton non-temporal to a temporal");
					}
				}


				if (thisV != null)
				{
					// is this the first field?
					if (thisResult == null)
					{
						thisResult = thisV.doubleValue(thisC.getUnits());
					}
					else
					{
						thisResult += thisV.doubleValue(thisC.getUnits());
					}
				}
			}
			return thisResult;
		}

		@Override
		protected String getOutputName()
		{
			return getContext().getInput("Add datasets",
					"Please provide a name for the dataset",
					"Sum of " + super.getSubjectList());
		}
	}

}
