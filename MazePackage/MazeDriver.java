package MazePackage;

import java.io.IOException;
import java.util.Scanner;

public class MazeDriver {

    static MazeManager manager = new MazeManager();

    public static void main(String[] args){
        int dim = 10;   //Change dimension
        double p = 0.5; //Change probability

        int[][] maze = manager.generateMaze(dim, p);
        manager.printMaze(maze);

        Scanner userInput = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("Please enter a start point x");
                int startX = userInput.nextInt();
                System.out.println("Please enter a start point y");
                int startY = userInput.nextInt();
                Point start = new Point(null, startX, startY);
                System.out.println("Please enter a goal point x");
                int goalX = userInput.nextInt();
                System.out.println("Please enter a goal point y");
                int goalY = userInput.nextInt();
                Point goal = new Point(null, goalX, goalY);
                System.out.println("Reachable: " + manager.mazeDFS(maze, start, goal));
                break;
            }
        } catch(Exception e) {
            // System.in has been closed
            System.out.println("Error: Exiting...");
        }
    }

}
