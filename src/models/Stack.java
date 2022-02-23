package models;

class Node<T> {
	T value;
	Node<T> next;
	public Node(T value, Node<T> next) {
		this.value = value;
		this.next = next;
	}
}
public class Stack<T> {
	Node<T> top;
	public void push(T t) {
		top = new Node<T>(t, top);
	}
	public T pop() {
		if(top == null) return null;
		else {
			T value = top.value;
			top = top.next;
			return value;
		}
	}
}
