package ru.salad.chatgame.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Node<T>{

	@Expose()
    private String key = null;
	@Expose()
    private T data = null;
	@Expose()
    private List<Node> children = new ArrayList<>();


    public Node(String key,T data) {
        this.data = data;
        this.key = key;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void addChild(String key, T data) {
        Node<T> newChild = new Node<>(key,data);
        children.add(newChild);
    }

    public void addChildren(List<Node> children) {

        this.children.addAll(children);
    }

    public List<Node> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}