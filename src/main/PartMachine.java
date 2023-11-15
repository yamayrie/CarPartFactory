package main;

import java.util.Random;

import data_structures.ListQueue;
import interfaces.Queue;

public class PartMachine {
	
	private int id ;
	private CarPart p1;
	private int period;
	private double weightError;
	private int chanceOfDefective;
	private Queue<Integer> timer;
	private Queue<CarPart> conveyorBelt;
	private int totalPartsProduced;
	private Random random = new Random();
   
    public PartMachine(int id, CarPart p1, int period, double weightError, int chanceOfDefective) {
        this.id = id;
        this.p1 = p1;
        this.period = period;
        this.weightError = weightError;
        this.chanceOfDefective = chanceOfDefective;
        startTimer();
        startConveyorBelt();
    }
    
    public int getId() {
       return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Queue<Integer> getTimer() {
    	return timer;
    }
    
    public void setTimer(Queue<Integer> timer) {
        this.timer = timer;
    }
    
    public CarPart getPart() {
    	return p1;
    }
    
    public void setPart(CarPart part1) {
    	this.p1 = part1;
    }
    
    public Queue<CarPart> getConveyorBelt() {
    	return conveyorBelt;
    }
    
    public void setConveyorBelt(Queue<CarPart> conveyorBelt) {
    	this.conveyorBelt = conveyorBelt;
    }
    
    public int getTotalPartsProduced() {
    	return totalPartsProduced;
    }
    
    public void setTotalPartsProduced(int count) {
    	this.totalPartsProduced = count;
    }
    
    public double getPartWeightError() {
        return weightError;
    }
    
    public void setPartWeightError(double partWeightError) {
        this.weightError = partWeightError;
    }
    public int getChanceOfDefective() {
        return chanceOfDefective;
    }
    
    public void setChanceOfDefective(int chanceOfDefective) {
        this.chanceOfDefective = chanceOfDefective;
    }
    
    public void resetConveyorBelt() {
        while (!conveyorBelt.isEmpty()) {
        	conveyorBelt.dequeue();
        }
        for (int i = 0; i < 10; i++) {
        	conveyorBelt.enqueue(null);
        }
    }
    
    public int tickTimer() {
       int front = timer.dequeue();
       timer.enqueue(front);
       return front;
    }
    
    private void startTimer() {
    	timer = new ListQueue<>();
    	for (int i = period - 1; i >= 0; i--) {
    		timer.enqueue(i);
    	}
    }
    
    private void startConveyorBelt() {
    	conveyorBelt = new ListQueue<>();
    	for (int i = 0; i < 10; i++) {
    		conveyorBelt.enqueue(null);
    	}
    }
    
    public CarPart produceCarPart() {
       if (tickTimer() != 0) {
    	   conveyorBelt.enqueue(null);
       }
       else {
    	   int partID = p1.getId();
    	   String partName = p1.getName();
    	   
    	   double min = p1.getWeight() - p1.getPartWeightError();
    	   double max = p1.getWeight() + p1.getPartWeightError();
    	   double randomWeight = min + random.nextDouble() * (max - min);
    	   
    	   boolean isDefective = p1.getChanceOfDefective() != 0 && (getTotalPartsProduced() % p1.getChanceOfDefective()) == 0;
    	   
    	   CarPart newPart = new CarPart(partID, partName, randomWeight, isDefective);
    	   conveyorBelt.enqueue(newPart);
    	   // getTotalPartsProduced in PartMachine
    	   setTotalPartsProduced(getTotalPartsProduced() + 1); // p1.getID().getTotalPartsProduced() + 1;
       }
       return conveyorBelt.dequeue();
    }
    
    
    

    /**
     * Returns string representation of a Part Machine in the following format:
     * Machine {id} Produced: {part name} {total parts produced}
     */
    @Override
    public String toString() {
        return "Machine " + this.getId() + " Produced: " + this.getPart().getName() + " " + this.getTotalPartsProduced();
    }
    /**
     * Prints the content of the conveyor belt. 
     * The machine is shown as |Machine {id}|.
     * If the is a part it is presented as |P| and an empty space as _.
     */
    public void printConveyorBelt() {
        // String we will print
        String str = "";
        // Iterate through the conveyor belt
        for(int i = 0; i < this.getConveyorBelt().size(); i++){
            // When the current position is empty
            if (this.getConveyorBelt().front() == null) {
                str = "_" + str;
            }
            // When there is a CarPart
            else {
                str = "|P|" + str;
            }
            // Rotate the values
            this.getConveyorBelt().enqueue(this.getConveyorBelt().dequeue());
        }
        System.out.println("|Machine " + this.getId() + "|" + str);
    }
}
