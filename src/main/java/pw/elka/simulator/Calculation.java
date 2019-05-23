/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.simulator;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Klasa przechowywująca obliczenia
 * @author Jan Jakubik & Oskar Misiewicz
 */
public class Calculation {

    public enum Keys {

        WAITING_TIME("waiting_time", 0),
        PROCESSING_TIME("proceeding_time", 1);

        private final String keysText;
        private final int id;

        private Keys(String keysText, int id) {
            this.keysText = keysText;
            this.id = id;
        }

        public String getKeysText() {
            return keysText;
        }

        public int getId() {
            return id;
        }

        public static Keys fromString(String text) {
            for (Keys b : Keys.values()) {
                if (b.keysText.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        public static Keys fromId(int id) {
            for (Keys b : Keys.values()) {
                if (b.id == id) {
                    return b;
                }
            }
            return null;
        }
    }

    protected LinkedHashMap<Keys, LinkedList<Number>> stat = new LinkedHashMap<>();
    protected double computeWaitTime = 0;

    public Calculation() {
        for (Keys k : Keys.values()) {
            stat.put(k, new LinkedList<>());
        }
    }

    public double waitingTime(double prevEvEndTime, double arrivalTime) {
        double c = Math.max(0, prevEvEndTime - arrivalTime);
        if (!this.stat.get(Keys.WAITING_TIME).isEmpty()) {

            this.stat.get(Keys.WAITING_TIME).add(c);
        } else {
            this.stat.get(Keys.WAITING_TIME).addFirst(c);
        }
        return c;
    }

    public void processingTime(double waitTime, double sojourTime) {
        double c = sojourTime + waitTime;
        if (!this.stat.get(Keys.PROCESSING_TIME).isEmpty()) {
            this.stat.get(Keys.PROCESSING_TIME).add(c);
        } else {
            this.stat.get(Keys.PROCESSING_TIME).addFirst(c);
        }
    }

    public void appendCalculation(Calculation s) {
        for (Keys k : Keys.values()) {
            stat.get(k).addAll(s.getStat().get(k));
        }
    }

    public void addCalculation(Calculation s) {
        this.stat = new LinkedHashMap<>(s.getStat());
    }

    public void clear() {
        stat.clear();
        for (Keys k : Keys.values()) {
            stat.put(k, new LinkedList<>());
        }
    }

    public String printCalculation() {
        String output = "";
        for (Keys k : Keys.values()) {
            output += k.getKeysText() + " ";
            for (Number t : stat.get(k)) {
                output += t + " ";
            }
            output += "\n";
        }
        return output;
    }

    public String printCsv() {
        String output = ";";
        for (int i = 0; i < this.stat.get(Keys.PROCESSING_TIME).size(); i++) {
            output += "" + i + ";";
        }
        output += "\n";
        for (Keys k : Keys.values()) {
            output += k.getKeysText() + "; ";
            for (int i = 0; i < this.stat.get(k).size(); i++) {
                output += "" + this.stat.get(k).get(i) + ";";
            }
            output += "\n";
        }
        return output;
    }

    public Calculation clone() {
        Calculation newCalc = new Calculation();
        for (Keys k : Keys.values()) {
            newCalc.getStat().get(k).addAll(this.stat.get(k));
        }
        return newCalc;
    }

    public double computeWaitTime() {
        double avg = 0, sum = 0;
        for (Number x : stat.get(Keys.WAITING_TIME)) {
            sum += x.doubleValue();
        }
        avg = sum / stat.get(Keys.WAITING_TIME).size();
        return avg;
    }

    public double computeProcessingTime() {
        double avg = 0, sum = 0;
        for (Number x : stat.get(Keys.PROCESSING_TIME)) {
            sum += x.doubleValue();
        }
        avg = sum / stat.get(Keys.PROCESSING_TIME).size();
        return avg;
    }

    public LinkedHashMap<Keys, LinkedList<Number>> getStat() {
        return stat;
    }
}
