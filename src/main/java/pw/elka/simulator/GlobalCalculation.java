/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.simulator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;

/**
 *
 * @author janek
 */
public class GlobalCalculation {

    private final List<Calculation> calculations = new ArrayList<>();
    private Calculation finalStat = new Calculation();
    private Calculation averageStat = new Calculation();

    public void addStat(Calculation s) throws CloneNotSupportedException {
//        System.out.print("-----------dodaje--------------" + "\n");
//        System.out.print(s.printStatistics());
        calculations.add((Calculation) s.clone());
    }

    public void compute() {
        Platform.runLater(()->{
             System.out.print("Computing...\n");
        });
       
        Calculation full = new Calculation();
        this.finalStat = new Calculation();
        for (Calculation c : calculations) {
//            System.out.print("-----------do wgrania--------------" + "\n");
//            System.out.print(c.printStatistics());
            for (Calculation.Keys t : Calculation.Keys.values()) {
                if (t.getId() == Calculation.Keys.WAITING_TIME.getId()) {
                    full.getStat().get(t).add(c.computeWaitTime());
                }else if (t.getId() == Calculation.Keys.PROCESSING_TIME.getId()){
                    full.getStat().get(t).add(c.computeProcessingTime());
                }
                else {
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
            //System.out.print("sum:" + sum+ "avg: "+avg);
            finalStat.getStat().get(t).add(avg);

            //System.out.print("end -" + t.getKeysText() + "\n");
        }
        //System.out.print("tp2 \n");
    }

    public void clear() {
        this.calculations.clear();
        this.finalStat.clear();
    }

    public String printStatistics() {
        compute();
        String header = "========= FINAL ===========\n";
        return header + finalStat.printStatistics();
    }

    public String printCSV() {
        return finalStat.printCsv() + "\n";
    }
}
