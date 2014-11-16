package com.rudd.numeth;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;
import org.matheclipse.parser.client.eval.IDoubleValue;

public class Adams {

    private final int n;
    private final double h;     // step-size
    private final ArrayList<Double> x = new ArrayList<>();
    private final ArrayList<Double> y = new ArrayList<>();
    private final ArrayList<Double> dy = new ArrayList<>();
    private final ArrayList<Double> py = new ArrayList<>();
    private final ArrayList<Double> ey = new ArrayList<>();
    private final ArrayList<Double> ea = new ArrayList<>();

    private int USE_FORMULA_TYPE;
    private int CHOSEN_FORMULA_ID;
    private String DERIVED_FORMULA;
    private String ORIGIN_FORMULA;

    public Adams(double h, double y0, double a, double b) {
        n = (int) Math.floor((b - a) / h);
        this.h = h;
        x.add(0.0);
        y.add(y0);
        py.add(0.0);

        for(int i=1; i<=n; ++i)
            x.add(h + x.get(i-1));
    }

    private double f(double x) {
        if(this.USE_FORMULA_TYPE == FormulaType.BUILT_IN){
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

    private double df(double x, double y) {
        if(this.USE_FORMULA_TYPE == FormulaType.BUILT_IN){
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

    public void compRungeKutta4th() {
        int i = 0;
        dy.add( df(x.get(i), y.get(i)) );

        while(i < 3) {
            double K1 = df(x.get(i), y.get(i));
            double K2 = df(x.get(i) + (h / 2), y.get(i) + (K1 * h / 2));
            double K3 = df(x.get(i) + (h / 2), y.get(i) + (K2 * h / 2));
            double K4 = df(x.get(i) + h, y.get(i) + (K3 * h));

            double NEXT_Y = y.get(i) + ((K1 + 2 * K2 + 2 * K3 + K4) * h / 6);

            y.add(NEXT_Y);
            dy.add( df(x.get(i+1), y.get(i+1)) );
            py.add(0.0);
            ++i;
        }
    }

    public void compCorrPred() {
        int i = 3;
        while(i < this.n) {
            double PREDICTOR = y.get(i) + (h * (55 * dy.get(i) - 59 * dy.get(i-1) + 37 * dy.get(i-2) - 9 * dy.get(i-3)) / 24);
            double CORRECTOR = y.get(i) + (h * (9 * df(x.get(i+1), PREDICTOR) + 19 * dy.get(i) - 5 * dy.get(i-1) + dy.get(i-2)) / 24);

            py.add(PREDICTOR);
            y.add(CORRECTOR);
            dy.add( df(x.get(i+1), y.get(i+1)) );    // y' = y - x
            i++;
        }
    }

    public void compute() {
        compRungeKutta4th();
        compCorrPred();

        for(int i=0; i<=this.n; ++i){
            ey.add(f(x.get(i)));
            ea.add(Math.abs(ey.get(i) - y.get(i)) / ey.get(i) * 100.0);
        }
    }

    public void printResult() {
        System.out.format("%3s %9s %9s %9s %9s\n","Xn", "Py", "Yn(Comp)", "Yn(Exact)", "Ea(%)");
        for(int i=0; i<=this.n; ++i){
            System.out.format("%3.1f %8.7f %8.7f %8.7f %8.7f", x.get(i), py.get(i), y.get(i), ey.get(i), ea.get(i));
            System.out.println();
        }
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
        Adams adams = new Adams(0.1, 2.0, 0.0, 1.0);
        adams.setFormulaType(FormulaType.BUILT_IN);
        adams.setUseFormula(1);
        adams.compute();
        adams.printResult();
    }
}
