package MazePackage;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

    //Used by priority-queue, chooses to prioritize point with the greatest heuristic
    public int compare(Point a, Point b){
        if(a.heuristic < b.heuristic){
            return 1;
        }else if(a.heuristic > b.heuristic){
            return -1;
        }else{
            return 0;
        }
    }
}
