package org.gentar.audit.diff.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node<T>
{
    @EqualsAndHashCode.Include
    private T data = null;

    private Set<Node<T>> children = new HashSet<>();

    @EqualsAndHashCode.Include
    private Node<T> parent = null;

    public Node(T data) {
        this.data = data;
    }

    public Node()
    {
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<Node<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public Set<Node<T>> getChildren() {
        return children;
    }

    public Node<T> find(T element)
    {
        Node<T> foundNode = null;
        if (getData().equals(element))
        {
            foundNode = this;
        }
        else
        {
            for (Node<T> child : getChildren())
            {
                foundNode = child.find(element);
                if (foundNode != null)
                {
                    break;
                }
            }
        }
        return foundNode;
    }

    public boolean anyParentContains(T element)
    {
        boolean result = false;
        Node<T> parent = getParent();


        if (parent != null)
        {
            if (parent.getData().equals(element))
            {
                result = true;
            }
            else
            {
                result = parent.anyParentContains(element);
            }
        }
        return result;

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void print()
    {
        printTree(this, " ");
    }

    private static <T> void printTree(Node<T> node, String appender) {
        System.out.println(appender + node.getData());
        node.getChildren().forEach(each ->  printTree(each, appender + "\t"));
    }
}
