package application;

public class PNode {
	private Product product;
     private PNode next;

     PNode(Product product) {
         this.product = product;
         this.next = null;
     }

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public PNode getNext() {
		return next;
	}

	public void setNext(PNode next) {
		this.next = next;
	}
     
}
