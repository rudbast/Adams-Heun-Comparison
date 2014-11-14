package com.rudd.numeth;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author rudolf
 */
public class ComparisonPlot extends JFrame {

    private JFreeChart lineChart;
    private final String plotTitle;
    private final String xlabel;
    private final String ylabel;
    private final XYSeriesCollection dataset;

    public ComparisonPlot(String plotTitle, String xlabel, String ylabel) {
        super("COMPARISON PLOT");
        this.plotTitle = plotTitle;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
        dataset = new XYSeriesCollection();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void constructPlot() {
        lineChart = ChartFactory.createXYLineChart(
            plotTitle,
            xlabel,
            ylabel,
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = lineChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));

        plot.setRenderer(renderer);
        lineChart.getXYPlot().setRenderer(new XYSplineRenderer());
        setContentPane(chartPanel);
        pack();
    }

    public void addDatasetSeries(ArrayList<Double> xValue, ArrayList<Double> yValue, String title) {
        XYSeries series = new XYSeries(title);
        for(int i=0; i<xValue.size(); ++i){
//            System.out.println(xValue.get(i) + " " + yValue.get(i));
            series.add(xValue.get(i), yValue.get(i));
        }
        this.dataset.addSeries(series);
    }

    public static void main(String[] args) {
        double h = 0.1;
        double y0 = 2.0;
        double a = 0.0;
        double b = 1.0;

        Adams adams = new Adams(h, y0, a, b);
        adams.setFormulaType(FormulaType.BUILT_IN);
        adams.setUseFormula(1);
        adams.compute();

        Heun heun = new Heun(h, y0, a, b);
        heun.setFormulaType(FormulaType.BUILT_IN);
        heun.setUseFormula(1);
        heun.compute();

        ComparisonPlot plot = new ComparisonPlot("dy/dx = y - x ; f'(x) = Exp(x) + x + 1",
            "Xn",
            "Error (%)");
        plot.addDatasetSeries(adams.getXValue(), adams.getError(), "Adams");
        plot.addDatasetSeries(heun.getXValue(), heun.getError(), "Heun");
        plot.constructPlot();
        plot.setVisible(true);
    }
}
