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
    
    //Sort of visualizes the path with number 7
    public int[][] newMaze(ArrayList<Point> path, int[][] maze){
    	
    	for(int i=0;i<path.size();i++) {
    		for(int j=0;j<maze.length;j++) {
    			for(int k =0;k<maze.length;k++) {
    				if(path.get(i).x == j && path.get(i).y == k){
    					maze[j][k] = 7; 
    				}
    			}
    		}
    	}
    		return maze;
    }

    //Traverses maze using Depth-First Search algorithm and returns true if path to goal is possible, false otherwise
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

    //Traverses maze using Breath-First Search algorithm and returns list of Points creating minimal path if possible, null otherwise
    public ArrayList<Point> mazeBFS(int[][] maze){
        Point goal = new Point(null, maze.length-1, maze.length-1);
        Queue<Point> fringe = new LinkedList<>();
        Point start = new Point(null, 0, 0);
        fringe.add(start);

        ArrayList<Point> closedPoints = new ArrayList<Point>();
        while(!fringe.isEmpty()){
            Point curr = fringe.remove();
            if(curr.equals(goal)){
                ArrayList<Point> path = tracePath(curr, start);
                return path;
            }
            ArrayList<Point> possibleSteps = generateSteps(maze, curr);
            for(Point p : possibleSteps){
                //If possible step is not a path already taken, then add to fringe
                if(!p.existsIn(closedPoints)) fringe.add(p);
            }
            closedPoints.add(curr);
        }
        return null;
    }

    //Traverses maze using A* algorithm and returns list of Points creating minimal path if possible, null otherwise
    //Queue prioritizes Point's based off heuristic which is determined by de-prioritization of paths with steps 'backward' + greatest 'look-ahead path' + euclidean distance
    //'look-ahead path' - the free spaces ahead that avoids 'fire' or 'obstacle' either in the 'right' or 'down' direction
    public ArrayList<Point> mazeAStar(int[][] maze){
        Point goal = new Point(null, maze.length-1, maze.length-1);

        Comparator<Point> comparator = new PointComparator();
        PriorityQueue<Point> fringe = new PriorityQueue<Point>(comparator);
        Point start = new Point(null, 0, 0);
        fringe.add(start);

        ArrayList<Point> closedPoints = new ArrayList<Point>();
        while(!fringe.isEmpty()){
            Point curr = fringe.remove();
            if(curr.equals(goal)){
                ArrayList<Point> path = tracePath(curr, start);
                return path;
            }
            ArrayList<Point> possibleSteps = generateStepsWithHeuristic(maze, curr, curr.stepsTaken);
            for(Point p : possibleSteps){
                //If possible step is not a path already taken, then add to fringe
                if(!p.existsIn(closedPoints)) fringe.add(p);
            }
            closedPoints.add(curr);
        }
        return null;
    }

    //Method to advance the fire by one step, based on random probability with increased likelihood if neighbor is on fire
    public int[][] advanceFireOneStep(int[][] maze, int length, double q){
        int[][] mazeCopy = copyMaze(maze);
        for(int x=0; x < length; x++){
            for(int y=0; y < length; y++){
                if(maze[x][y] != 1 && maze[x][y] != 2){
                    int k = neighborsOnFire(x, y, maze);
                    double prob = 1 - Math.pow((1 - q), k);
                    if(rand.nextDouble() <= prob)
                        mazeCopy[x][y] = 2;
                }
            }
        }
        return mazeCopy;
    }

    //Helper method to count the number of neighbors on fire for use in advanceFireOneStep() method
    private int neighborsOnFire(int x, int y, int[][] maze){
        int fire = 0;
        if((x - 1 >= 0) && (maze[x - 1][y] == 2)) fire++;
        if((x + 1 < maze.length) && (maze[x + 1][y] == 2)) fire++;
        if((y - 1 >= 0) && (maze[x][y - 1] == 2)) fire++;
        if((y + 1 < maze.length) && (maze[x][y + 1] == 2)) fire++;
        return fire;
    }

    //Duplicates current maze for use in advanceFireOneStep() method
    public int[][] copyMaze(int[][] maze){
        int length = maze.length;
        int[][] copy = new int[length][length];
        for(int i=0; i < length; i++){
            System.arraycopy(maze[i], 0, copy[i], 0, length);
        }
        return copy;
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

    //Returns List of possible non-restricted steps from the current point passed
    private ArrayList<Point> generateStepsWithHeuristic(int[][] maze, Point point, int stepsTaken){
        ArrayList<Point> steps = new ArrayList<>();
        //Check to see if moving 'up' is possible, because 'up' is a backwards movement within the maze '-1' is subtracted to decrease heuristic priority
        if((point.x - 1 >= 0) && (maze[point.x - 1][point.y] != 1) && (maze[point.x - 1][point.y] != 2))
            steps.add(new Point(point, point.x - 1, point.y, stepsTaken + 1,getHeuristic("up", maze, point.x - 1, point.y, stepsTaken + 1)));
        //Check to see if moving 'down' is possible, heuristic determined by steps up to generatedStep + steps possible in the same 'down' direction
        if((point.x + 1 < maze.length) && (maze[point.x + 1][point.y] != 1) && (maze[point.x + 1][point.y] != 2))
            steps.add(new Point(point, point.x + 1, point.y, stepsTaken + 1,getHeuristic("down", maze,point.x + 1, point.y, stepsTaken + 1)));
        //Check to see if moving 'left' is possible, because 'left' is a backwards movement within maze '-1' is subtracted to decrease heuristic priority
        if((point.y - 1 >= 0) && (maze[point.x][point.y - 1] != 1) && (maze[point.x][point.y - 1] != 2))
            steps.add(new Point(point, point.x, point.y - 1, stepsTaken + 1,getHeuristic("left", maze, point.x, point.y - 1, stepsTaken + 1)));
        //Check to see if moving 'right' is possible, heuristic determined by steps up to generatedStep + steps possible in the same 'down' direction
        if((point.y + 1 < maze.length) && (maze[point.x][point.y + 1] != 1) && (maze[point.x][point.y + 1] != 2))
            steps.add(new Point(point, point.x, point.y + 1, stepsTaken + 1, getHeuristic("right", maze, point.x, point.y + 1, stepsTaken + 1)));
        return steps;
    }

    //Helper method to determine determine heuristic based on 3 main factors: steps 'backward', steps with 'open space', 'euclidean' distance from goal
    private int getHeuristic(String direction, int[][] maze, int x, int y, int stepsTaken){
        int stepsToGoal = (maze.length - 1) * 2;
        int stepsInDirection = 0;
        int stepsRemaining = stepsToGoal - (x + y);
        if(direction.equals("down")){
            for(int i = x+1; i < maze.length; i++){
                if(maze[i][y] != 1 && maze[i][y] != 2)
                    stepsInDirection++;
                else
                    break;
            }
        }else if(direction.equals("right")){
            for(int i = y+1; i < maze.length; i++){
                if(maze[x][i] != 1 && maze[x][i] != 2)
                    stepsInDirection++;
                else
                    break;
            }
        }

        //If path found with the minimal steps to goal, no need to keep searching, so highest priority possible is used as heuristic
        if(stepsTaken == stepsToGoal) return stepsToGoal;
        // Otherwise heuristic is calculated by the de-prioritization on paths that required backward movements
        // + added priority for the open spaces that exist either 'down' or 'right' ahead of point
        // + added priority for paths that have more steps taken than those that do not -- this can be tweaked to prioritize points with shortest euclidean distance (same thing)
        return ((stepsToGoal - stepsTaken - stepsRemaining) + stepsInDirection + (stepsTaken - stepsRemaining));
    }

    //Backtraces by referring to parent of each point starting from 'goal' Point to 'start' Point, returns list of Points creating minimal path
    private ArrayList<Point> tracePath(Point goal, Point start){
        Point currPoint = goal;
        ArrayList<Point> path = new ArrayList<>();
        while(!currPoint.equals(start)){
            path.add(0, currPoint);
            currPoint = currPoint.parent;
        }
        path.add(0, currPoint);
        return path;
    }

}
