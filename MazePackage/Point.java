package MazePackage;

import java.util.ArrayList;

public class Point {
    public Point parent;
    public int x;
    public int y;
    public int stepsTaken;
    public int heuristic;

    public Point(Point parent, int x, int y){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.stepsTaken = 0;
        this.heuristic = 0;

    }

    public Point(Point parent, int x, int y, int stepsTaken, int heuristic){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.stepsTaken = stepsTaken;
        this.heuristic = heuristic;
    }

    //Compares two points to see if they are equal
    public boolean equals(Point p){
        if(p == null){
            return false;
        }else if(this.x == p.x && this.y == p.y){
            return true;
        }
        return false;
    }

    //Traverses list to see if point already exists in list
    public boolean existsIn(ArrayList<Point> list){
        for(Point p : list){
            if(p.equals(this)){
                return true;
            }
        }
        return false;
    }

}
