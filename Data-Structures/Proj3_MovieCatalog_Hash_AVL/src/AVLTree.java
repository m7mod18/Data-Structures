package application;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class AVLTree {

    private Node root;

    private int height(Node node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        
        y.left = x;
        x.right = T2;

        
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        
        return y;
    }

    public void insert(Movie movie) {
        root = insert(root, movie);
    }

    private Node insert(Node node, Movie movie) {
        if (node == null) {
            return new Node(movie);
        }

        int comparison = movie.getTitle().compareTo(node.movie.getTitle());
        if (comparison < 0) {
            node.left = insert(node.left, movie);
        } else if (comparison > 0) {
            node.right = insert(node.right, movie);
        } else {
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) < 0) {
            return rotateRight(node);
        }

        if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) > 0) {
            return rotateLeft(node);
        }

        if (balance > 1 && movie.getTitle().compareTo(node.left.movie.getTitle()) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && movie.getTitle().compareTo(node.right.movie.getTitle()) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public Movie search(String title) {
        return search(root, title);
    }

    private Movie search(Node node, String title) {
        if (node == null) {
            return null; 
        }

        int comparison = title.compareTo(node.movie.getTitle());
        if (comparison < 0) {
            return search(node.left, title);
        } else if (comparison > 0) {
            return search(node.right, title);
        } else {
            return node.movie;
        }
    }
    
    public void delete(String title) {
        root = delete(root, title);
    }

    private Node delete(Node node, String title) {
        if (node == null) {
            return node; 
        }

        int comparison = title.compareTo(node.movie.getTitle());
        if (comparison < 0) {
            node.left = delete(node.left, title);
        } else if (comparison > 0) {
            node.right = delete(node.right, title);
        } else {

            if (node.left == null || node.right == null) {
                Node temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else { 
                    node = temp; 
                }
            } else {
                Node temp = minValueNode(node.right);

                node.movie = temp.movie;

                node.right = delete(node.right, temp.movie.getTitle());
            }
        }

        if (node == null) {
            return node;
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node minValueNode(Node node) {
        Node current = node;
                   
        
        while (current.left != null) {
            current = current.left;
        }

        return current;
    }

    public void traverseInOrder(List<Movie> result) {
        traverseInOrder(root, result);
    }
    private void traverseInOrder(Node node, List<Movie> result) {
        if (node != null) {
            traverseInOrder(node.left, result);
            result.add(node.movie);
            traverseInOrder(node.right, result);
        }
    }

    public void collectMovies(ObservableList<Movie> movies) {
        collectMovies(root, movies); 
    }
                                         
    private void collectMovies(Node node, ObservableList<Movie> movies) {
        if (node != null) {
            collectMovies(node.left, movies); 
            movies.add(node.movie);          
            collectMovies(node.right, movies);  
    }
    }
   
    public int getHeight() {
        return getHeight(root);
    }

      private int getHeight(Node node) {
        return (node == null) ? 0 : node.height;
    }
    public boolean isEmpty() {
        return root == null;
    }
 
    public int countNodes() {
        return countNodes(root);
    }

    private int countNodes(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    public Movie getMax() {
        return getMax(root); 
        }

    private Movie getMax(Node node) {
        if (node == null) return null;

        while (node.right != null) {
            node = node.right;
        }
        return node.movie;
    }
    public Movie getMin() {
        return getMin(root); 
    }

    private Movie getMin(Node node) {
        if (node == null) return null;

        while (node.left != null) {
            node = node.left;
        }
        return node.movie;
    }
    public Movie getMaxByRating() {
        return getMaxByRating(root); 
    }

    private Movie getMaxByRating(Node node) {
        if (node == null) return null;

        Movie maxMovie = node.movie; 
        Movie leftMax = getMaxByRating(node.left); 
        Movie rightMax = getMaxByRating(node.right); 

        if (leftMax != null && leftMax.getRating() > maxMovie.getRating()) {
            maxMovie = leftMax;
        }
        if (rightMax != null && rightMax.getRating() > maxMovie.getRating()) {
            maxMovie = rightMax;
        }

        return maxMovie;
    }
    public void clear() {
        root = null; 
    }
    public Movie getMinByRating() {
        return getMinByRating(root);
    }

    private Movie getMinByRating(Node node) {
        if (node == null) return null;

        Movie min = node.movie;
        Movie leftMin = getMinByRating(node.left);
        Movie rightMin = getMinByRating(node.right);

        if (leftMin != null && leftMin.getRating() < min.getRating()) {
            min = leftMin;
        }
        if (rightMin != null && rightMin.getRating() < min.getRating()) {
            min = rightMin;
        }

        return min;
    }

    
    public List<Movie> inOrderList() {
        List<Movie> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Node node, List<Movie> list) {
        if (node == null) return;
        inOrderTraversal(node.left, list);
        list.add(node.movie);
        inOrderTraversal(node.right, list);
    }
 
}
