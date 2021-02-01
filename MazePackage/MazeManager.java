package MazePackage;

import java.util.Random;
import java.util.Stack;
import java.util.*;

public class MazeManager {

    static Random rand = new Random();

    //Generates initial maze based on dimension and probability of obstacles
    public int[][] generateMaze(int dim, double p){
        int [][] maze = new int[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                if((i == 0 && j == 0) || (i == dim-1 && j == dim-1)) continue; // Skip if start or goal state
                if(rand.nextDouble() <= p) maze[i][j] = 1; // 1 - represents obstacle
            }
        }
        return maze;
    }

    //Prints maze passed to function
    public void printMaze(int[][] maze){
        int dim = maze.length;
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                if(j == dim - 1){
                    System.out.println(maze[i][j]);
                }else{
                    System.out.print(maze[i][j] + "\t");
                }
            }
        }
    }

    //Traverses maze in Depth-First Search and returns true if path to goal is possible, false otherwise
    public boolean mazeDFS(int[][]maze, Point start, Point goal){
        Stack<Point> fringe = new Stack<Point>();
        fringe.push(start);

        ArrayList<Point> closedPoints = new ArrayList<Point>();
        while(!fringe.isEmpty()){
            Point curr = fringe.pop();
            if(curr.equals(goal)){
                return true;
            }
            ArrayList<Point> possibleSteps = generateSteps(maze, curr);
            for(Point p : possibleSteps){
                //If possible step is not a path already taken, then add to fringe
                if(!p.existsIn(closedPoints)) fringe.push(p);
            }
            closedPoints.add(curr);
        }
        return false;
    }

    //Traverses maze in Breath-First Search and returns true if path to goal is possible, false otherwise
    public boolean mazeBFS(int[][]maze){
        Point goal = new Point(null, maze.length-1, maze.length-1);
        Queue<Point> fringe = new LinkedList<>();
        fringe.add(new Point(null, 0, 0));

        ArrayList<Point> closedPoints = new ArrayList<Point>();
        while(!fringe.isEmpty()){
            Point curr = fringe.remove();
            if(curr.equals(goal)){
                return true;
            }
            ArrayList<Point> possibleSteps = generateSteps(maze, curr);
            for(Point p : possibleSteps){
                //If possible step is not a path already taken, then add to fringe
                if(!p.existsIn(closedPoints)) fringe.add(p);
            }
            closedPoints.add(curr);
        }
        return false;
    }

    //Returns List of possible non-restricted steps from the current point passed
    private ArrayList<Point> generateSteps(int[][] maze, Point point){
        ArrayList<Point> steps = new ArrayList<>();
        //Check to see if moving 'up' is possible
        if((point.x - 1 >= 0) && (maze[point.x - 1][point.y] != 1) && (maze[point.x - 1][point.y] != 2))
            steps.add(new Point(point, point.x - 1, point.y));
        //Check to see if moving 'down' is possible
        if((point.x + 1 < maze.length) && (maze[point.x + 1][point.y] != 1) && (maze[point.x + 1][point.y] != 2))
            steps.add(new Point(point, point.x + 1, point.y));
        //Check to see if moving 'left' is possible
        if((point.y - 1 >= 0) && (maze[point.x][point.y - 1] != 1) && (maze[point.x][point.y - 1] != 2))
            steps.add(new Point(point, point.x, point.y - 1));
        //Check to see if moving 'right' is possible
        if((point.y + 1 < maze.length) && (maze[point.x][point.y + 1] != 1) && (maze[point.x][point.y + 1] != 2))
            steps.add(new Point(point, point.x, point.y + 1));
        return steps;
    }

}
