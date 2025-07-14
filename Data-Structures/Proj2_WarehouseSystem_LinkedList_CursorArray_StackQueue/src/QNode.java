package application;


public class QNode {
    private Shipment data;
    private QNode next;

    public QNode(Shipment data) {
        this.data = data;
        this.next = null;
    }

    public Shipment getShipment() {
        return data;
    }

    public QNode getNext() {
        return next;
    }

    public void setNext(QNode next) {
        this.next = next;
    }
}

