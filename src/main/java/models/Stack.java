package models;

import java.util.ArrayList;

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
	public void push(T[] t) {
		for(int i=t.length-1;i>=0;i--) {
			top = new Node<T>(t[i], top);
		}
	}
	public T pop() {
		if(top == null) return null;
		else {
			T value = top.value;
			top = top.next;
			return value;
		}
	}
	public ArrayList<T> output() {
		Node<T> temp = top;
		ArrayList<T> result = new ArrayList<>();
		while (temp!=null) {
			result.add(temp.value);
			temp = temp.next;
		}
		return result;
	}
}
