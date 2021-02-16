package MazePackage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class GraphGenerator extends ApplicationFrame {
    public GraphGenerator(String applicationTitle, String graphTitle, String xAxis, String yAxis, DefaultCategoryDataset dataset){
        super(applicationTitle);
        JFreeChart graph = ChartFactory.createLineChart(graphTitle, yAxis, xAxis, dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(graph);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }
}
