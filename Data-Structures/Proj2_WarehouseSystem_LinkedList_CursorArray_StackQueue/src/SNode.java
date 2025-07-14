package application;

public  class SNode {
    private ActionRecord record;
    private SNode next;

    public SNode(ActionRecord record) {
        this.record = record;
        this.next = null;
    }

    public ActionRecord getRecord() {
        return record;
    }

    public SNode getNext() {
        return next;
    }

    public void setNext(SNode next) {
        this.next = next;
    }
}

