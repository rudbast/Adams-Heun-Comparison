package com.rudd.numeth;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

public class Heun {

    private final int n;
    private final double h;     // step-size
    private final ArrayList<Double> x = new ArrayList<>();
    private final ArrayList<Double> y = new ArrayList<>();
    private final ArrayList<Double> ey = new ArrayList<>();
    private final ArrayList<Double> ea = new ArrayList<>();

    private int USE_FORMULA_TYPE;
    private int CHOSEN_FORMULA_ID;
    private String DERIVED_FORMULA;
    private String ORIGIN_FORMULA;

    public Heun(double h, double y0, double a, double b){
        n = (int) Math.floor((b - a) / h);
        this.h = h;
        x.add(0.0);
        y.add(y0);

        for(int i=1; i<=n; ++i)
            x.add(h + x.get(i-1));
    }

    public double f(double x) {
        if(this.USE_FORMULA_TYPE == 0){
            switch (this.CHOSEN_FORMULA_ID) {
                case 1:
                    return Math.exp(x) + x + 1;
                case 2:
                    return x;
                case 3:
                    return x;
                case 4:
                    return x;
            }
        }

        DoubleEvaluator engine = new DoubleEvaluator();
        IDoubleValue vdX = new DoubleVariable(x);

        engine.defineVariable("x", vdX);

        double result = engine.evaluate(ORIGIN_FORMULA);
        return result;
    }

    public double df(double x, double y) {
        if(this.USE_FORMULA_TYPE == 0){
            switch (this.CHOSEN_FORMULA_ID) {
                case 1:
                    return y - x;
                case 2:
                    return y - x;
                case 3:
                    return y - x;
                case 4:
                    return y - x;
            }
        }

        DoubleEvaluator engine = new DoubleEvaluator();
        IDoubleValue vdX = new DoubleVariable(x);
        IDoubleValue vdY = new DoubleVariable(y);

        engine.defineVariable("x", vdX);
        engine.defineVariable("y", vdY);

        double result = engine.evaluate(DERIVED_FORMULA);
        return result;
    }

    public void compute() {
        int i = 0;
        while(i < n){
            double INIT_SLOPE = df(x.get(i), y.get(i));
            double PREDICTOR = INIT_SLOPE * h + y.get(i);
            double END_SLOPE = df(x.get(i+1), PREDICTOR);
            double SLOPE = (INIT_SLOPE + END_SLOPE) / 2;
            y.add(SLOPE * h + y.get(i));
            ++i;
        }

        for(i=0; i<=this.n; ++i){
            ey.add(f(x.get(i)));
            ea.add(Math.abs(ey.get(i) - y.get(i)) / ey.get(i) * 100.0);
        }
    }

    public void printResult() {
        System.out.format("%3s %9s %9s %9s\n","Xn", "Yn(Comp)", "Yn(Exact)", "Ea(%)");
        for(int i=0; i<=this.n; ++i)
            System.out.format("%3.1f %8.7f %8.7f %8.7f\n", x.get(i), y.get(i), ey.get(i), ea.get(i));
    }

    public void printResult(JTable tabResult) {
        DefaultTableModel model = (DefaultTableModel) tabResult.getModel();
        while(model.getRowCount() > 0)
            model.removeRow(0);

        for(int i=0; i<=this.n; ++i)
            model.addRow(new Object[]{x.get(i), y.get(i), ey.get(i), ea.get(i)});
    }

    public void setOriginFormula(String FORMULA) {
        this.ORIGIN_FORMULA = FORMULA;
    }

    public void setDerivedFormula(String FORMULA) {
        this.DERIVED_FORMULA = FORMULA;
    }

    public void setFormulaType(int FORMULA_TYPE) {
        this.USE_FORMULA_TYPE = FORMULA_TYPE;
    }

    public void setUseFormula(int FORMULA_ID) {
        this.CHOSEN_FORMULA_ID = FORMULA_ID;
    }

    public ArrayList<Double> getXValue() {
        return x;
    }

    public ArrayList<Double> getError() {
        return ea;
    }

    public static void main(String[] args) {
        Heun heun = new Heun(0.1, 2.0, 0.0, 1.0);
        heun.setFormulaType(FormulaType.BUILT_IN);
        heun.setUseFormula(1);
        heun.compute();
        heun.printResult();
    }
}
