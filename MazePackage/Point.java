package MazePackage;

import java.util.ArrayList;

public class Point {
    public Point parent;
    public int x;
    public int y;

    public Point(Point parent, int x, int y){
        this.parent = parent;
        this.x = x;
        this.y = y;
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
