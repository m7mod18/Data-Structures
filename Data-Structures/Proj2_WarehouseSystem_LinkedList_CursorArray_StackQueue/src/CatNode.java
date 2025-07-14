package application;

public class CatNode {
	 public Category category;
	 private CatNode next;
	 private CatNode prev;

	    public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public CatNode getNext() {
		return next;
	}

	public void setNext(CatNode next) {
		this.next = next;
	}

	public CatNode getPrev() {
		return prev;
	}

	public void setPrev(CatNode prev) {
		this.prev = prev;
	}

		public CatNode(Category category) {
	        this.category = category;
	        this.next = null;
	        this.prev = null;
	    }
}
