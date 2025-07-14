package application;
public class ShipmentQueue {
    private QNode front;
    private QNode rear;

    public ShipmentQueue() {
        front = rear = null;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public void enqueue(Shipment shipment) {
        QNode newNode = new QNode(shipment);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
    }

    public Shipment dequeue() {
        if (isEmpty()) return null;

        Shipment result = front.getShipment();
        front = front.getNext();
        if (front == null) rear = null;
        return result;
    }

    public Shipment peek() {
        return isEmpty() ? null : front.getShipment();
    }

    public void printQueue() {
        QNode current = front;
        while (current != null) {
            System.out.println(current.getShipment());
            current = current.getNext();
        }
    }
    public void removeByID(String id) {
        ShipmentQueue tempQueue = new ShipmentQueue();

        while (!this.isEmpty()) {
            Shipment current = this.dequeue();
            if (!current.getShipmentID().equalsIgnoreCase(id)) {
                tempQueue.enqueue(current);
            }
        }

        this.front = tempQueue.front;
        this.rear = tempQueue.rear;
    }
    public int count() {
        int c = 0;
        QNode current = front;
        while (current != null) {
            c++;
            current = current.getNext();
        }
        return c;
    }

    public QNode getFront() {
        return front;
    }
} 