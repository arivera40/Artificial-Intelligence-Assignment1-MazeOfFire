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

    //Generates initial maze of fire based on dimension, probability of obstacles, and probability of fire
    public int[][] generateMazeOfFire(int dim, double p){
        int firePos = rand.nextInt(((dim-1) * 2) - 2) + 1; //random integer in range of indexes between start and goal state
        Point firePoint = new Point(null, 0, 0);
        int [][] maze = new int[dim][dim];
        for(int i=0; i < dim; i++){
            for(int j=0; j < dim; j++){
                firePos--;
                if((i == 0 && j == 0) || (i == dim-1 && j == dim-1)) continue; // Skip if start or goal state
                if(firePos == 0){
                    maze[i][j] = 2; //2 - represents fire
                    firePoint.x = i;
                    firePoint.y = j;
                }else if(rand.nextDouble() <= p){
                    maze[i][j] = 1; // 1 - represents obstacle
                }
            }
        }

        return discardMalformedMaze(maze, new Point(null, 0, 0), firePoint, dim, p);
    }

    //Helper method that will continuously discard mazes until one is found with a path from start to goal and where fire is not closed off
    private int[][] discardMalformedMaze(int[][] maze, Point start, Point firePoint, int dim, double p){
        while(!mazeDFS(maze, start, new Point(null, dim-1, dim-1)) || !mazeDFS(maze, firePoint, start)){
            maze = generateMazeOfFire(dim, p);
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

    //Visual of path taken, path ends in 9 if agent burnt before exit or ends in 7 at goal state (used for fire maze specifically)
    public int[][] pathResult(ArrayList<Point> path, int[][] maze, Point end){
        for(Point p : path){
            if(maze[p.x][p.y] == 2){
                maze[p.x][p.y] = 9;
            }else{
                maze[p.x][p.y] = 7;
            }
            if(p == end) return maze;
        }
        return maze;
    }

    //Visual of path taken marked with 7
    public int[][] pathResult(ArrayList<Point> path, int[][] maze){
        for(Point p : path){
            maze[p.x][p.y] = 7;
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

    //Implementation of Strategy 1 as described in project description
    //Generates path to goal using A* algorithm and follows the path 1 step at a time as the fire advances
    //Returns final state of path after burning in fire or making it to goal state
    public int[][] strategy1(int[][] maze, double q){
        int[][] mazeCopy = copyMaze(maze);  //Copy used so same maze can be reused in driver
        ArrayList<Point> path = mazeAStar(maze);
        Point curr = new Point(null, 0, 0);
        for(int i=1; i < path.size(); i++){
            curr = path.get(i);
            mazeCopy = advanceFireOneStep(mazeCopy, mazeCopy.length, q);
            if(mazeCopy[curr.x][curr.y] == 2) return pathResult(path, mazeCopy, curr);
        }
        return pathResult(path, mazeCopy, curr);
    }

    //method uses modified BFS method to calculate path at every point in the maze
    public int[][] strategy2(int[][] maze,double q){
        int[][] mazeCopy = copyMaze(maze);

        Point curr = new Point(null,0,0);
        Point goal = new Point(null,mazeCopy.length-1,mazeCopy.length-1);

        ArrayList<Point> path = mazeBFS(mazeCopy);
        ArrayList<Point> newPath = new ArrayList<>();
        newPath.add(curr);

        if(q==0) {
            return (pathResult(path,mazeCopy));
        }

        while(!curr.equals(goal)) {
            curr = path.get(1);
            mazeCopy = advanceFireOneStep(mazeCopy,mazeCopy.length,q);
            path = mazeBFSFire(mazeCopy,curr,goal);
            newPath.add(curr);
            if(mazeCopy[curr.x][curr.y] ==2) {
                System.out.println("You died in the fire!");
                return pathResult(newPath,mazeCopy, curr);
            }
            if(path==null) {
                System.out.println("No path found from current point to goal point");
                return pathResult(newPath,mazeCopy, curr);
            }
        }
        System.out.println("Congrats you made it out the fire");
        return pathResult(newPath,mazeCopy, curr);
    }

    //Modified BFS method to traverse between two given points.
    public ArrayList<Point> mazeBFSFire(int[][] maze,Point start, Point goal){
        // Point goal = new Point(null, maze.length-1, maze.length-1);
        Queue<Point> fringe = new LinkedList<>();
        //Point start = new Point(null, 0, 0);
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
        //Check to see if moving 'up' is possible
        if((point.x - 1 >= 0) && (maze[point.x - 1][point.y] != 1) && (maze[point.x - 1][point.y] != 2))
            steps.add(new Point(point, point.x - 1, point.y, stepsTaken + 1,getHeuristic("up", maze, point.x - 1, point.y, stepsTaken + 1)));
        //Check to see if moving 'down' is possible
        if((point.x + 1 < maze.length) && (maze[point.x + 1][point.y] != 1) && (maze[point.x + 1][point.y] != 2))
            steps.add(new Point(point, point.x + 1, point.y, stepsTaken + 1,getHeuristic("down", maze,point.x + 1, point.y, stepsTaken + 1)));
        //Check to see if moving 'left' is possible
        if((point.y - 1 >= 0) && (maze[point.x][point.y - 1] != 1) && (maze[point.x][point.y - 1] != 2))
            steps.add(new Point(point, point.x, point.y - 1, stepsTaken + 1,getHeuristic("left", maze, point.x, point.y - 1, stepsTaken + 1)));
        //Check to see if moving 'right' is possible
        if((point.y + 1 < maze.length) && (maze[point.x][point.y + 1] != 1) && (maze[point.x][point.y + 1] != 2))
            steps.add(new Point(point, point.x, point.y + 1, stepsTaken + 1, getHeuristic("right", maze, point.x, point.y + 1, stepsTaken + 1)));
        return steps;
    }

    //Helper method to determine determine heuristic based on 3 main factors: steps 'backward', steps with 'open space', 'euclidean' distance from goal
    private int getHeuristic(String direction, int[][] maze, int x, int y, int stepsTaken){
        int stepsToGoal = (maze.length - 1) * 2;
        int openSpace = 0;
        int stepsRemaining = stepsToGoal - (x + y);
        if(direction.equals("down")){
            for(int i = x+1; i < maze.length; i++){
                if(maze[i][y] != 1 && maze[i][y] != 2)
                    openSpace++;
                else
                    break;
            }
        }else if(direction.equals("right")){
            for(int i = y+1; i < maze.length; i++){
                if(maze[x][i] != 1 && maze[x][i] != 2)
                    openSpace++;
                else
                    break;
            }
        }

        //If path found with the minimal steps to goal, no need to keep searching, so highest priority possible is used as heuristic
        if(stepsTaken == stepsToGoal) return stepsToGoal;

        //Heuristic calculation
        //deprioritize backward movements so algorithm can search for better paths
        int deprioritizeBackSteps = (stepsToGoal - stepsTaken - stepsRemaining) * 10;
        //prioritize paths that are closer to goal state
        int prioritizeLongerPaths = (stepsTaken - stepsRemaining >= 0) ? (stepsTaken - stepsRemaining) / 2 : 0;
        //Extra prioritization for paths with open space
        return deprioritizeBackSteps + openSpace + prioritizeLongerPaths;
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
