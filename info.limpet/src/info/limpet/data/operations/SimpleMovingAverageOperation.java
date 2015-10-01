package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.math.SimpleMovingAverage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Quantity;

public class SimpleMovingAverageOperation implements IOperation<ICollection>
{
	public static final String SERIES_NAME_TEMPLATE = "Simple Moving Average ({0})";

	CollectionComplianceTests aTests = new CollectionComplianceTests();

	final protected int windowSize;

	public SimpleMovingAverageOperation(int windowSize)
	{
		this.windowSize = windowSize;
	}

	public Collection<ICommand<ICollection>> actionsFor(
			List<ICollection> selection, IStore destination)
			{
		Collection<ICommand<ICollection>> res = new ArrayList<ICommand<ICollection>>();
		if (appliesTo(selection))
		{
			String name = MessageFormat.format(SERIES_NAME_TEMPLATE, windowSize);
			ICommand<ICollection> newC = new SimpleMovingAverageCommand(
					name, name, selection, destination);
			res.add(newC);
		}

		return res;
	}

	private boolean appliesTo(List<ICollection> selection)
	{
		boolean singleSeries = selection.size() == 1;
		boolean allQuantity = aTests.allQuantity(selection);
		return (singleSeries && allQuantity);
	}

	public class SimpleMovingAverageCommand extends
			AbstractCommand<ICollection>
	{

		public SimpleMovingAverageCommand(String operationName, String outputName,
				List<ICollection> selection, IStore store)
		{
			super(operationName, "Calculates a Simple Moving Average", outputName,
					store, false, false, selection);
		}

		@Override
		public void execute()
		{
			IQuantityCollection<?> input = (IQuantityCollection<?>) _inputs.get(0);

			List<ICollection> outputs = new ArrayList<ICollection>();

			// ok, generate the new series
			IQuantityCollection<?> target = new QuantityCollection<>(input.getName()
					+ getOutputName(), this, input.getUnits());

			outputs.add(target);

			// store the output
			super.addOutput(target);

			// start adding values.
			performCalc(outputs);

			// tell each series that we're a dependent
			Iterator<ICollection> iter = _inputs.iterator();
			while (iter.hasNext())
			{
				ICollection iCollection = iter.next();
				iCollection.addDependent(this);
			}

			// ok, done
			List<ICollection> res = new ArrayList<ICollection>();
			res.add(target);
			getStore().addAll(res);
		}

		@Override
		protected void recalculate()
		{
			// update the results
			performCalc(_outputs);
		}

		/**
		 * wrap the actual operation. We're doing this since we need to separate it
		 * from the core "execute" operation in order to support dynamic updates
		 * 
		 * @param unit
		 * @param outputs
		 */
		private void performCalc(List<ICollection> outputs)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) outputs.iterator().next();

			// clear out the lists, first
			Iterator<ICollection> iter = _outputs.iterator();
			while (iter.hasNext())
			{
				IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
				qC.getValues().clear();
			}

			SimpleMovingAverage sma = new SimpleMovingAverage(windowSize);
			IQuantityCollection<?> input = (IQuantityCollection<?>) _inputs.get(0);

			for (Quantity<?> quantity : input.getValues())
			{
				sma.newNum(quantity.getValue().doubleValue());
				target.add(sma.getAvg());
			}
		}
	}

}
