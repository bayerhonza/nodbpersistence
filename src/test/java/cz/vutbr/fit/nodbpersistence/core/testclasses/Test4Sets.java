package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class Test4Sets {

    @ObjectId
    public Long objectId;

    private Set<String> setString;
    private List<Integer> listInteger;
    private Queue<Test4Class1> queueClass1;
    private Set<List<Stack<String>>> setListStack;

    public Set<String> getSetString() {
        return setString;
    }

    public void setSetString(Set<String> setString) {
        this.setString = setString;
    }

    public List<Integer> getListInteger() {
        return listInteger;
    }

    public void setListInteger(List<Integer> listInteger) {
        this.listInteger = listInteger;
    }

    public Queue<Test4Class1> getQueueClass1() {
        return queueClass1;
    }

    public void setQueueClass1(Queue<Test4Class1> queueClass1) {
        this.queueClass1 = queueClass1;
    }

    public Set<List<Stack<String>>> getSetListStack() {
        return setListStack;
    }

    public void setSetListStack(Set<List<Stack<String>>> setListStack) {
        this.setListStack = setListStack;
    }
}
