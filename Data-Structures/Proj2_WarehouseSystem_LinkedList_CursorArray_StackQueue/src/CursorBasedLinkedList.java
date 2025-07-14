package application;

public class CursorBasedLinkedList {

    private static final int SIZE = 100;
    private CNode[] list;
    private int head;
    private int free;

    public CursorBasedLinkedList() {
        list = new CNode[SIZE];
        for (int i = 0; i < SIZE; i++) {
            list[i] = new CNode();
        }

        for (int i = 0; i < SIZE - 1; i++) {
            list[i].setNext(i + 1);
        }
        list[SIZE - 1].setNext(-1);

        head = -1;
        free = 0;
    }

    public boolean isEmpty() {
        return head == -1;
    }

    public boolean isFull() {
        return free == -1;
    }

    public void insertAtEnd(Shipment shipment) {
        if (isFull()) {
            System.out.println("List is full.");
            return;
        }

        int newNodeIndex = free;
        free = list[free].getNext();

        list[newNodeIndex].setData(shipment);
        list[newNodeIndex].setNext(-1);

        if (head == -1) {
            head = newNodeIndex;
        } else {
            int current = head;
            while (list[current].getNext() != -1) {
                current = list[current].getNext();
            }
            list[current].setNext(newNodeIndex);
        }
    }

    public boolean removeByShipmentID(String shipmentID) {
        if (isEmpty()) return false;

        int current = head;
        int previous = -1;

        while (current != -1 && list[current].getData() != null && !list[current].getData().getShipmentID().equals(shipmentID)) {
            previous = current;
            current = list[current].getNext();
        }

        if (current == -1) return false;

        if (previous == -1) {
            head = list[current].getNext();
        } else {
            list[previous].setNext(list[current].getNext());
        }

        list[current].setData(null);
        list[current].setNext(free);
        free = current;

        return true;
    }

    public int getHeadIndex() {
        return head;
    }

    public CNode[] getNodeArray() {
        return list;
    }

    public void printList() {
        int current = head;
        while (current != -1) {
            if (list[current].getData() != null) {
                System.out.println(list[current].getData());
            }
            current = list[current].getNext();
        }
    }
    public int count() {
        int count = 0;
        int current = head;
        while (current != -1) {
            if (list[current].getData() != null) {
                count++;
            }
            current = list[current].getNext();
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int current = head;
        while (current != -1) {
            if (list[current].getData() != null) {
                sb.append(list[current].getData()).append("\n");
            }
            current = list[current].getNext();
        }
        return sb.toString();
    }
}