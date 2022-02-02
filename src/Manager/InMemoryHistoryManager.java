package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private Map<Long, Node> map = new HashMap<>();

    class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node linkLast(Task task) {
        Node l = tail;
        Node newNode = new Node(l, task, null);
        tail = newNode;
        if (l == null)
            head = newNode;
        else
            l.next = newNode;
        return newNode;
    }

    @Override
    public void add(Task task) {
        if (!map.containsKey(task.getId())) {
            map.put(task.getId(), linkLast(task));
        } else {
            removeNode(map.get(task.getId()));
            map.put(task.getId(), linkLast(task));
        }
    }

    private void removeNode(Node removeNode) {
        Node next = removeNode.next;
        Node prev = removeNode.prev;
        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            removeNode.prev = null;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            removeNode.next = null;
        }
        removeNode.data = null;
    }

    @Override
    public void remove(long id) {
        removeNode(map.get(id));
        map.remove(id);
    }

    public void removeFirstNode() {
        long nodeId = head.data.getId();
        remove(nodeId);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public int size() {
        return map.size();
    }
}
