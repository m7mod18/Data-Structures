package application;

public class ActionStack {

	 private SNode top;

	    public ActionStack() {
	        top = null;
	    }

	    public boolean isEmpty() {
	        return top == null;
	    }

	    public void push(ActionRecord record) {
	        SNode newNode = new SNode(record);
	        newNode.setNext(top);
	        top = newNode;
	    }

	    public ActionRecord pop() {
	        if (isEmpty()) return null;

	        ActionRecord record = top.getRecord();
	        top = top.getNext();
	        return record;
	    }

	    public ActionRecord peek() {
	        return isEmpty() ? null : top.getRecord();
	    }

	    public void printStack() {
	        SNode current = top;
	        while (current != null) {
	            ActionRecord r = current.getRecord();
	            System.out.println(r.getActionType() + " - " + r.getShipment().getShipmentID());
	            current = current.getNext();
	        }
	    }
	}