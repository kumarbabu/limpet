package limpet.prototype.ian_generics.dinko;

import javax.measure.Quantity;

public interface IQuantityCollection<T extends Quantity<T>> extends IObjectCollection<Quantity<T>>
{
	public Quantity<T> min();
	public Quantity<T> max();
	public Quantity<T> mean();
	public Quantity<T> variance();
	public Quantity<T> sd();
}
