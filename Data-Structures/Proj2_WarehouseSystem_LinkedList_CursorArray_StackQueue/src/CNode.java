package application;

public class CNode {
	 
	       private  Shipment data;
	       private   int next;

	        public Shipment getData() {
			return data;
		}

		public void setData(Shipment data) {
			this.data = data;
		}

		public int getNext() {
			return next;
		}

		public void setNext(int next) {
			this.next = next;
		}

			CNode() {
	            this.data = null;
	            this.next = -1;
	        }
	    
}
