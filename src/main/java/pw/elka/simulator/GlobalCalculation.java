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
        System.out.print("Computing...\n");
        Calculation full = new Calculation();
//        System.out.print("Liczba powtorzen do obliczenia sredniej: " + calculations.size() + "\n");
        for (Calculation c : calculations) {
//            System.out.print("-----------do wgrania--------------" + "\n");
//            System.out.print(c.printStatistics());
            for (Calculation.Keys t : Calculation.Keys.values()) {
                full.getStat().get(t).addAll(c.getStat().get(t));

            }
        }
//        System.out.print("-----------suma--------------" + "\n");
//        System.out.print(full.printStatistics());
        //System.out.print("tp1\n");
        for (Calculation.Keys t : Calculation.Keys.values()) {
            if(t.getId() != Calculation.Keys.BUF_GT.getId()) {
            System.out.print("start -" + t.getKeysText() + "\n");
            double num[] = new double[calculations.get(0).getStat().get(t).size()];
            double avg, sum = 0;
            double averageNum[] = new double[calculations.get(0).getStat().get(t).size()];
            for (int _i = 0; _i < num.length; _i++) {
                
                for (int _j = 0; _j < full.getStat().get(t).size() / num.length; _j++) {
                    if (full.getStat().get(t).get(_i * calculations.size() + _j) != null) {
                        num[_i] += full.getStat().get(t).get(_i * calculations.size() + _j).doubleValue();
                    }
                }
                //System.out.print("tp1 -" + t.getKeysText() + "nr: " + _i + "\n");
                averageNum[_i] = num[_i] / calculations.size();
                finalStat.getStat().get(t).add(averageNum[_i]);
                sum += averageNum[_i];
            }
            
            avg = sum / finalStat.getStat().get(t).size();
            averageStat.getStat().get(t).add(avg);
            System.out.print("end -" + t.getKeysText() + "\n");
        }
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
        return finalStat.printCsv() + "\n" +  averageStat.printCsv();
    }
}
