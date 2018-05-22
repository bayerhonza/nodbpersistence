package cz.vutbr.fit.nodbpersistence.core;

import cz.vutbr.fit.nodbpersistence.core.testclasses.Test4Class1;
import cz.vutbr.fit.nodbpersistence.core.testclasses.Test4Sets;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComplexTest4 extends TestClassNoDbPersistence {

    public static Long objectId2;

    @Test
    public void test1PersistClassSet() {
        Test4Sets t4collections = new Test4Sets();
        Test4Sets t4collections2 = new Test4Sets();

        Set<String> setString = new HashSet<>();
        setString.add("aaa");
        setString.add("bbb");
        t4collections.setSetString(setString);
        t4collections2.setSetString(setString);

        ArrayList<Integer> integerSet = new ArrayList<>();
        integerSet.add(1);
        integerSet.add(2);
        t4collections.setListInteger(integerSet);
        t4collections2.setListInteger(integerSet);

        Test4Class1 test4Class1 = new Test4Class1();
        test4Class1.setString("test4class1");
        Queue<Test4Class1> queue = new PriorityQueue<>();
        queue.add(test4Class1);
        t4collections.setQueueClass1(queue);
        t4collections2.setQueueClass1(queue);

        Stack<String> stack = new Stack<>();
        stack.push("push1");
        stack.push("push2");
        List<Stack<String>> listStack = new ArrayList<>();
        listStack.add(stack);
        Set<List<Stack<String>>> setListStack = new HashSet<>();
        setListStack.add(listStack);
        t4collections.setSetListStack(setListStack);

        objectId = pm.persist(t4collections);
        objectId2 = pm.persist(t4collections2);
        pm.flush();
    }

    @Test
    public void test2SetString() {
        Test4Sets test4Loaded = pm.load(objectId, Test4Sets.class);
        assertTrue(test4Loaded.getSetString() instanceof HashSet);
        assertTrue(test4Loaded.getSetString().contains("aaa"));
        assertTrue(test4Loaded.getSetString().contains("bbb"));
    }

    @Test
    public void test3ListInteger() {
        Test4Sets test4Loaded = pm.load(objectId, Test4Sets.class);
        assertTrue(test4Loaded.getListInteger() instanceof ArrayList);
        assertTrue(test4Loaded.getListInteger().contains(1));
        assertTrue(test4Loaded.getListInteger().contains(2));
        assertEquals(2, test4Loaded.getListInteger().size());
    }

    @Test
    public void test4QueueClass() {
        Test4Sets test4Loaded = pm.load(objectId, Test4Sets.class);
        assertTrue(test4Loaded.getQueueClass1() instanceof PriorityQueue);
        assertTrue(test4Loaded.getQueueClass1().stream()
                .anyMatch(test4Class1 -> Objects.equals(test4Class1.getString(), "test4class1")));

    }

    @Test
    public void test5IfIdentity() {
        Test4Sets test4Loaded1 = pm.load(objectId, Test4Sets.class);
        Test4Sets test4Loaded2 = pm.load(objectId2, Test4Sets.class);
        assertSame(test4Loaded1.getListInteger(), test4Loaded2.getListInteger());
        assertSame(test4Loaded1.getSetString(), test4Loaded2.getSetString());
        assertSame(test4Loaded1.getQueueClass1(), test4Loaded2.getQueueClass1());
    }

    @Test
    public void test6SetListStack() {
        Test4Sets test4Loaded1 = pm.load(objectId, Test4Sets.class);
        assertTrue(test4Loaded1.getSetListStack().stream()
                .anyMatch(stacks -> stacks.stream()
                        .anyMatch(strings -> strings.stream()
                                .anyMatch(s -> s.equals("push1")))));
    }

}
