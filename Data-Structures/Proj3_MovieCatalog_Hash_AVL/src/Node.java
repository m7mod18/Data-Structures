package application;

public class Node {
	  Movie movie;
      Node left, right;
      int height;

      Node(Movie movie) {
          this.movie = movie;
          this.height = 1; 
      }
}
