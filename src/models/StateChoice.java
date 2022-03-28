package models;

public class StateChoice<T> {
	private T[] values;
	private boolean[] read;
	public StateChoice(T[] values, boolean[] read) {
		this.values = values;
		this.read = read;
	}
	public T[] getValues() {
		return values;
	}
	public boolean[] getRead() {
		return read;
	}
}
