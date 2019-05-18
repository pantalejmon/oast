/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.simulator;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author janek
 */
public class Calculation {

    enum Keys {
        CUSTOMERS_IN_QUEUE("customers_in_queue", 0),
        CUSTOMERS_IN_SYSTEM("customers_in_system", 1),
        WAITING_TIME("waiting_time", 2),
        PROCESSING_TIME("proceeding_time", 3),
        REJECTED_COUNTER("rejected_counter", 4),
        BUF_GT("buf_gt", 5);

        private String keysText;
        private int id;

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

    LinkedHashMap<Keys, LinkedList<Number>> stat = new LinkedHashMap<>();

    public Calculation() {
        for (Keys k : Keys.values()) {
            stat.put(k, new LinkedList<>());
        }
    }

    // TODO narazie nazwy jak u olka, potem zrobimy refactor
    public double back(String k) {
        Keys ke = Keys.fromString(k);
        return this.stat.get(ke).getLast().doubleValue();
    }

    public void appendCasQueue(int q) {
        this.stat.get(Keys.CUSTOMERS_IN_QUEUE).add(q);
    }

    public void addCasQueue(double c) {
        if (!this.stat.get(Keys.CUSTOMERS_IN_QUEUE).isEmpty()) {
            this.stat.get(Keys.CUSTOMERS_IN_QUEUE).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
        } else {
            this.stat.get(Keys.CUSTOMERS_IN_QUEUE).addFirst(c);
        }
    }

    public void addCasSys(double c) {
        if (!this.stat.get(Keys.CUSTOMERS_IN_SYSTEM).isEmpty()) {
            this.stat.get(Keys.CUSTOMERS_IN_SYSTEM).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
        } else {
            this.stat.get(Keys.CUSTOMERS_IN_SYSTEM).addFirst(c);
        }
    }

    public double addWaitTime(double prevEvEndTime, double arrivalTime) {
        double c = Math.max(0, prevEvEndTime - arrivalTime);
        if (!this.stat.get(Keys.WAITING_TIME).isEmpty()) {
            this.stat.get(Keys.WAITING_TIME).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
        }
        else {
            this.stat.get(Keys.WAITING_TIME).addFirst(c);
        }
        return c;
    }

    public void addPocTime(double waitTime, double sojourTime) {
        double c = sojourTime + waitTime;
        if (!this.stat.get(Keys.PROCESSING_TIME).isEmpty()) {
            this.stat.get(Keys.PROCESSING_TIME).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
        }else {
            this.stat.get(Keys.PROCESSING_TIME).addFirst(c);
        }
    }

    public void addRejCount() {
        if (!this.stat.get(Keys.REJECTED_COUNTER).isEmpty()) {
            this.stat.get(Keys.REJECTED_COUNTER).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + 1);
        }
        else {
            this.stat.get(Keys.REJECTED_COUNTER).addFirst(1);
        }
    }

    public void addBuffGT() {
        if (!this.stat.get(Keys.BUF_GT).isEmpty()) {
            this.stat.get(Keys.BUF_GT).addFirst(this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + 1);
        }
        else {
            this.stat.get(Keys.BUF_GT).addFirst(1);
        }
    }

    public void appendStat(Calculation s) {
        for (Keys k : Keys.values()) {
            stat.get(k).addAll(s.getStat().get(k));
        }
    }

    public void addStat(Calculation s) {
        this.stat = new LinkedHashMap<>(s.getStat());
    }

    public void clear() {
        stat.clear();
        for (Keys k : Keys.values()) {
            stat.put(k, new LinkedList<>());
        }
    }

    public String printStatistics() {
        String output = "";
        for (Keys k : Keys.values()) {
            output += k.getKeysText() + " ";
            for (Number t : stat.get(k)) {
                output += t + " ";
            }
            output += "\n";
        }
        System.out.print(output);
        return output;
    }

    public LinkedHashMap<Keys, LinkedList<Number>> getStat() {
        return stat;
    }

}
