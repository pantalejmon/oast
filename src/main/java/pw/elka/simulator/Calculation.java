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
        CUSTOMERS_IN_QUEUE("customers_in_queue",0),
        CUSTOMERS_IN_SYSTEM("customers_in_system",1),
        WAITING_TIME("waiting_time",2),
        PROCESSING_TIME("proceeding_time",3),
        REJECTED_COUNTER("rejected_counter",4),
        BUF_GT("buf_gt",5);
        
        private String keysText;
        private int id;
        
        private Keys(String keysText, int id) {
            this.keysText = keysText;
            this.id  = id;
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
        for(Keys k : Keys.values()){
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
        this.stat.get(Keys.CUSTOMERS_IN_QUEUE).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
    }
    
    public void addCasSys(double c) {
        this.stat.get(Keys.CUSTOMERS_IN_SYSTEM).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
    }
    
    public double addWaitTime(double prevEvEndTime, double arrivalTime) {
        double c = Math.max(0,prevEvEndTime-arrivalTime);
        this.stat.get(Keys.WAITING_TIME).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
        return c;
    }
    
    public void addPocTime(double waitTime, double sojourTime) {
        double c = sojourTime + waitTime;
        this.stat.get(Keys.PROCESSING_TIME).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + c);
    }

    public void addRejCount() {
        this.stat.get(Keys.REJECTED_COUNTER).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + 1);
    }
    
     public void addBuffGT() {
        this.stat.get(Keys.BUF_GT).set(0, this.stat.get(Keys.CUSTOMERS_IN_QUEUE).getFirst().doubleValue() + 1);
    }
     
    public void appendStat(Calculation s) {
        for(Keys k:Keys.values()) {
            stat.get(k).addAll(s.getStat().get(k));
        }
    }
     
    public void addStat(Calculation s) {
        this.stat = new LinkedHashMap<>(s.getStat());
    }

    public void clear() {
        stat.clear();
    }
    
    public String printStatistics() {
        String output = "";
        for(Keys k:Keys.values()){
            output += k.getKeysText() + " ";
            for(Number t : stat.get(k)) {
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
