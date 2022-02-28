package models;

public class StateChoice<T> {
	private T[] values;
	public StateChoice(T[] values) {
		this.values = values;
	}
	public T[] getValues() {
		return values;
	}
}
