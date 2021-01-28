package MazePackage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class MazeDriver {

    static MazeManager manager = new MazeManager();

    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        try {
            System.out.println("Please enter 'two points' for random testing");
            System.out.println("Please enter 'analysis' for DFS analysis");
            String option = userInput.nextLine();

            //Test DFS by choosing start and goal points
            if(option.equals("two points")) {
                while (true) {
                    int dim = 50;   //Change dimension
                    double p = 0.2; //Change probability

                    int[][] maze = manager.generateMaze(dim, p);
                    manager.printMaze(maze);

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
            //Generate DFS Analysis Report for plotting graph
            }else{
                generateDFSAnalysis();
            }
        } catch(Exception e) {
            // System.in has been closed
            System.out.println("Error: Exiting...");
        }
    }

    //Generates DFS analysis test
    //Each test performs 10 DFS searches for each 'obstacle density p' and records the 'probability S can be reached from G'
    //There are a total of 10 tests for which the average of the 10 results are taken
    //mazeDFS function is performed a total of 100 times for each 'obstacle density p' in order to get an accurate plot
    public static void generateDFSAnalysis(){
        int dim = 175;
        Point start = new Point(null, 0, 0);
        Point goal = new Point(null, 174, 174);

        double[] average = new double[10];  //keeps track of results for each test
        //Loop to perform 10 tests
        for(int test = 0; test < 10; test++) {
            System.out.println("Test " + (test+1));
            int t = 0;
            //Loop for each p (0 < p < 1)
            for (double p = 0.1; p < 0.91; p += 0.1) {
                double results = 0;
                //Loop to perform mazeDFS for each p
                for (int i = 0; i < 10; i++) {
                    int[][] maze = manager.generateMaze(dim, p);
                    if (manager.mazeDFS(maze, start, goal)) results += 0.1;
                }
                System.out.println("Probability that S can be reached from G is: " + formatDouble(results)
                        + ", when obstacle density p is: " + formatDouble(p));
                average[t] += results;
                t++;
            }
            System.out.println();
        }
        //Outputs average results for each test
        for(int i=0; i < average.length; i++){
            System.out.println("Average results for 0." + i + " is: " + formatDouble(average[i]/10));
        }
    }

    private static String formatDouble(double num){
        String pattern = "0.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String numberStr = decimalFormat.format(num);
        return numberStr;
    }

}
