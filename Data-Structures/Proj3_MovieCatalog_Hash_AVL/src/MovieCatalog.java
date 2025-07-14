package application;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class MovieCatalog {
	private AVLTree[] hashTable;
	private int collisionCount = 0;
	int count = 0;
	int newSize;
	private int rehashCount = 0;

	public MovieCatalog() {
	    allocate(7); 
	}

	private void allocate(int size) {
	    hashTable = new AVLTree[size];
	    for (int i = 0; i < size; i++) {
	        hashTable[i] = new AVLTree();
	    }
	}


	public int getTableSize() {
	    return hashTable.length;
	}

	public int getRehashCount() {
	    return rehashCount;
	}

	public int hashFunction(String key) {
	    int hash = 7;
	    for (int i = 0; i < key.length(); i++) {
	        hash = hash * 31 + key.charAt(i);
	    }
	    return Math.abs(hash) % hashTable.length;
	}



	public void add(Movie movie) {
	    int index = hashFunction(movie.getTitle());

	    if (hashTable[index] == null) {
	        hashTable[index] = new AVLTree();
	    }

	    Movie existing = hashTable[index].search(movie.getTitle());
	    if (existing != null) {
	        existing.setDescription(movie.getDescription());
	        existing.setReleaseYear(movie.getReleaseYear());
	        existing.setRating(movie.getRating());
	    } else {
	        hashTable[index].insert(movie);
	    }

	    double avgHeight = getAverageAVLHeight();
	    if (avgHeight > 3.0) {
	        rehash();
	    }
	}



	private double getAverageAVLHeight() {
	    int totalHeight = 0;
	    int count = 0;

	    for (AVLTree tree : hashTable) {
	        if (tree != null && !tree.isEmpty()) {
	            totalHeight += tree.getHeight(); 
	            count++;
	        }
	    }

	    if (count == 0) return 0;
	    return (double) totalHeight / count;
	}

  private int nextPrime(int n) {
	if (isPrime(n)) {
		return n;
	}
	n++;
	while (!isPrime(n)) {
		n++;
	}
	return n;
}

  private boolean isPrime(int n) {
	if (n <= 1)
		return false;
	for (int i = 2; i <= Math.sqrt(n); i++) {
		if (n % i == 0) {
			return false;
		}
	}
	return true;
    }




	public Movie get(String title) {
		int index = hashFunction(title);
		return hashTable[index].search(title);
	}

	public void erase(String title) {
		int index = hashFunction(title);
		AVLTree tree = hashTable[index];
		tree.delete(title);
	}

	public ObservableList<Movie> getAllMovies() {
		ObservableList<Movie> movies = FXCollections.observableArrayList();

		for (AVLTree tree : hashTable) {
			if (tree != null) {
				tree.collectMovies(movies);
			}
		}
		return movies;
	}

	

	public AVLTree[] getHashTable() {
		return this.hashTable;
	}

	private void rehash() {
	    rehashCount++; 

	    AVLTree[] oldTable = hashTable;
	    int newSize = nextPrime(hashTable.length * 2);

	    hashTable = new AVLTree[newSize];
	    for (int i = 0; i < newSize; i++) {
	        hashTable[i] = new AVLTree();
	    }

	    for (AVLTree tree : oldTable) {
	        if (tree != null && !tree.isEmpty()) {
	            for (Movie m : tree.inOrderList()) {
	                add(m);
	            }
	        }
	    }
	}





	
	public void deallocate() {
	    for (int i = 0; i < hashTable.length; i++) {
	        if (hashTable[i] != null) {
	            hashTable[i].clear();  
	            hashTable[i] = null;   
	        }
	    }
	    hashTable = null; 
	}

}
