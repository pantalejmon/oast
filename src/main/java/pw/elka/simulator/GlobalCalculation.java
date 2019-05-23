/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.simulator;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

/**
 * Klasa licząca średnią z obliczeń
 *
 * @author Jan Jakubik & Oskar Misiewicz
 */
public class GlobalCalculation {

    private final List<Calculation> calculations = new ArrayList<>();
    private Calculation finalStat = new Calculation();

    public void addStat(Calculation s) throws CloneNotSupportedException {
        calculations.add((Calculation) s.clone());
    }

    public void compute() {
       
        Calculation full = new Calculation();
        this.finalStat = new Calculation();
        for (Calculation c : calculations) {
            for (Calculation.Keys t : Calculation.Keys.values()) {
                if (t.getId() == Calculation.Keys.WAITING_TIME.getId()) {

                    full.getStat().get(t).add(c.computeWaitTime());
                } else if (t.getId() == Calculation.Keys.PROCESSING_TIME.getId()) {

                    full.getStat().get(t).add(c.computeProcessingTime());
                } else {
                    full.getStat().get(t).addAll(c.getStat().get(t));
                }
            }
        }
        for (Calculation.Keys t : Calculation.Keys.values()) {
            double avg, sum = 0;
            for (int _j = 0; _j < full.getStat().get(t).size(); _j++) {
                if (full.getStat().get(t).get(_j) != null) {
                    sum += full.getStat().get(t).get(_j).doubleValue();
                }
            }
            avg = sum / calculations.size();
            finalStat.getStat().get(t).add(avg);
        }
    }

    public void clear() {
        this.calculations.clear();
        this.finalStat.clear();
    }

    public String printStatistics() {
        compute();
        String header = "========= FINAL ===========\n";
        String footer = "=========================\n";
        return header + finalStat.printCalculation() + footer;
    }

    public String printCSV() {
        return finalStat.printCsv() + "\n";
    }
}
