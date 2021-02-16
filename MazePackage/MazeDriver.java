package MazePackage;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MazeDriver {

    static MazeManager manager = new MazeManager();

    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        boolean quit = false;
        char command;
        boolean time = false;

        System.out.println("Welcome to Maze on Fire");
        try {
            int maze[][] = null;
            System.out.println("Please enter 'o' for maze of obstacles, 'f' for maze of fire, 'd' for DFS analysis graph, 'b' for BFS vs A* analysis graph");
            command = userInput.next().charAt(0);
            if(command == 'o') {
                while (!quit) {

                    if (!time) {
                        System.out.println("Set dimension 'dim' to create maze");
                        int dim = userInput.nextInt();

                        System.out.println("Set probability 'p' to create barricades");
                        double p = userInput.nextDouble();
                        maze = manager.generateMaze(dim, p);
                        manager.printMaze(maze);
                    }

                    System.out.println("Please enter 'd' to run dfs, 'b' for bfs, or 'a' for A*  or 'q' to exit or 'n' for new maze");
                    command = userInput.next().charAt(0);
                    time = true;


                    if (command == 'b') {
                        final long startTime = System.currentTimeMillis();
                        ArrayList<Point> path = manager.mazeBFS(maze);
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution Time: " + (endTime - startTime) + " milliseconds");
                        if (path != null) {
                            System.out.println("(BFS)Reachable: true");

                            for (int i = 0; i < path.size(); i++) {
                                if (i == 0) {
                                    System.out.print("[(" + path.get(i).x + ", " + path.get(i).y + "), ");
                                } else if (i != path.size() - 1) {
                                    System.out.print("(" + path.get(i).x + ", " + path.get(i).y + "), ");
                                } else {
                                    System.out.print("(" + path.get(i).x + ", " + path.get(i).y + ")]\n");
                                    System.out.println("Steps taken: " + (path.size() - 1));
                                }
                            }
                            int copy[][] = manager.copyMaze(maze);
                            manager.pathResult(path, copy);
                            manager.printMaze(copy);
                        } else {
                            System.out.println("(BFS)Reachable: false");
                        }
                    } else if (command == 'd') {
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
                        final long startTime = System.currentTimeMillis();
                        System.out.println("(DFS)Reachable: " + manager.mazeDFS(maze, start, goal));
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution Time: " + (endTime - startTime) + " milliseconds");
                    } else if (command == 'a') {
                        final long startTime = System.currentTimeMillis();
                        ArrayList<Point> path = manager.mazeAStar(maze);
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution Time: " + (endTime - startTime) + " milliseconds");
                        if (path != null) {
                            System.out.println("(A*)Reachable: true");
                            for (int i = 0; i < path.size(); i++) {
                                if (i == 0) {
                                    System.out.print("[(" + path.get(i).x + ", " + path.get(i).y + "), ");
                                } else if (i != path.size() - 1) {
                                    System.out.print("(" + path.get(i).x + ", " + path.get(i).y + "), ");
                                } else {
                                    System.out.print("(" + path.get(i).x + ", " + path.get(i).y + ")]\n");
                                    System.out.println("Steps taken: " + (path.size() - 1));
                                }
                            }
                            int copy[][] = manager.copyMaze(maze);
                            manager.pathResult(path, copy);
                            manager.printMaze(copy);
                        } else {
                            System.out.println("(A*)Reachable: false");
                        }
                    } else if (command == 'n') {
                        time = false;
                    } else {
                        quit = true;
                    }

                }
            }else if(command == 'f'){
                double q = 0;
                while(!quit){
                    if (!time) {
                        System.out.println("Set dimension 'dim' to create maze");
                        int dim = userInput.nextInt();

                        System.out.println("Set probability 'p' to create barricades");
                        double p = userInput.nextDouble();

                        maze = manager.generateMazeOfFire(dim, p);
                        manager.printMaze(maze);
                    }
                    if(q == 0){
                        System.out.println("Please enter a probability 'q' to grow the fire");
                        q = userInput.nextDouble();
                    }
                    System.out.println("Please enter '1' to run Strategy 1, '2' to run Strategy 2, '3' to run Strategy 3, " +
                            "'c' to change fire spread probability, 'q' to exit or 'n' for new maze");
                    command = userInput.next().charAt(0);
                    time = true;
                    if(command == '1'){
                        int [][] result = manager.strategy1(maze, q);
                        manager.printMaze(result);
                    }else if(command == '2'){
                        int result[][] = manager.strategy2(maze, q);
                        manager.printMaze(result);
                    }else if(command == '3'){
                        System.out.println("Strategy 3 is being tested...");
                        int [][] result = manager.strategy3(maze, q);
                        manager.printMaze(result);
                    }else if(command == 'n') {
                        time = false;
                    }else if(command == 'c'){
                        q = 0;
                    }else{
                        quit = true;
                    }
                }
            }else if(command == 'd'){
                generateDFSAnalysis();
            }else{
                generateAStarVsBFSAnalysis();
            }

        } catch(Exception e) {
            // System.in has been closed
            System.out.println("Error: Exiting...");
        }
        userInput.close();
    }

    //Generates DFS analysis test
    //Each test performs 10 DFS searches for each 'obstacle density p' and records the 'probability S can be reached from G'
    //There are a total of 10 tests for which the average of the 10 results are taken
    //mazeDFS function is performed a total of 100 times for each 'obstacle density p' in order to get an accurate plot
    public static void generateDFSAnalysis(){
        int dim = 50;
        Point start = new Point(null, 49, 49);
        Point goal = new Point(null, 0, 0);

        double[] average = new double[9];  //keeps track of results for each test
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

        DefaultCategoryDataset dfsDataset = new DefaultCategoryDataset();

        //Outputs average results for each test
        for(int i=0; i < average.length; i++) {
            System.out.println("Average results for 0." + (i+1) + " is: " + formatDouble(average[i]/10));
            dfsDataset.addValue(formatDouble(average[i] / 10), "success", "0." + (i + 1));
        }
        GraphGenerator generator = new GraphGenerator("DFS Plot Analysis",
                "Obstacle Density vs Success Rate",
                "probability that S can be reached from G",
                "obstacle density p",
                dfsDataset);
        generator.pack();
        RefineryUtilities.centerFrameOnScreen(generator);
        generator.setVisible(true);
    }

    //Still testing...
    public static void generateAStarVsBFSAnalysis(){
        int dim = 10;
        double[] average = new double[10];
        for(int test = 0; test < 10; test++){
            System.out.println("Test " + (test+1));
            int t = 0;
            for(double p = 0.1; p < 0.91; p += 0.1){
                int results = 0;
                for(int i=0; i < 10; i++){
                    int[][] maze = manager.generateMaze(dim, p);
                    int BFSPointsExplored = manager.mazeBFSPointsExplored(maze);
                    int AStarPointsExplored = manager.mazeAStarPointsExplored(maze);
                    results += (BFSPointsExplored - AStarPointsExplored);
                }
                System.out.println("Average 'number of nodes explored by BFS - number of nodes explored by A*': " + (results/10)
                        + ", when obstacle density p is: " + formatDouble(p));
                average[t] += results;
                t++;
            }
            System.out.println();
        }

        DefaultCategoryDataset dfsDataset = new DefaultCategoryDataset();

        //Outputs average results for each test
        for(int i=0; i < average.length; i++) {
            System.out.println("Average difference between BFS nodes explored - A* nodes explored for 0." + (i+1) + " is: "
                    + formatDouble(average[i]/10));
            dfsDataset.addValue(formatDouble(average[i] / 10), "difference", "0." + (i + 1));
        }
        GraphGenerator generator = new GraphGenerator("Average Points Explored Difference",
                "BFS vs A*",
                "number of nodes explored by BFS - number of nodes explored by A*",
                "obstacle density p",
                dfsDataset);
        generator.pack();
        RefineryUtilities.centerFrameOnScreen(generator);
        generator.setVisible(true);
    }

    private static Double formatDouble(double num){
        String pattern = "0.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String numberStr = decimalFormat.format(num);
        return Double.parseDouble(numberStr);
    }

}
