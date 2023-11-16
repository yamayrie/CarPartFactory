package main;

import java.util.Random;

import data_structures.ListQueue;
import interfaces.Queue;


/**
 * Class that represents a machine in a car part factory that produces car parts. Includes methods for reseting the conveyor belt,
 * creating a timer, starting the timer, starting the conveyor belt and producing car parts. 
 * 
*/
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
   
	/**
	 * Creates a constructor for PartMachine.
	 * 
	 * @param id ID of the machine.
	 * @param p1 The car part that the machine produces.
	 * @param period How often a part is produced.
	 * @param weightError Error of the weight of the part being produced.
	 * @param chanceOfDefective Chances of the part coming out defective.
	 * 
	*/
    public PartMachine(int id, CarPart p1, int period, double weightError, int chanceOfDefective) {
        this.id = id;
        this.p1 = p1;
        this.period = period;
        this.weightError = weightError;
        this.chanceOfDefective = chanceOfDefective;
        startTimer();
        startConveyorBelt();
    }
    
    // Getters and setters for fields
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
    
    
    /** Empties the conveyor belt. Sets all values to null.*/
    public void resetConveyorBelt() {
        while (!conveyorBelt.isEmpty()) {
        	conveyorBelt.dequeue();
        }
        for (int i = 0; i < 10; i++) {
        	conveyorBelt.enqueue(null);
        }
    }
    
    /** Updates the timer by one.*/
    public int tickTimer() {
       int front = timer.dequeue();
       timer.enqueue(front);
       return front;
    }
    
    /** Initializes the timer queue.*/
    private void startTimer() {
    	timer = new ListQueue<>();
    	for (int i = period - 1; i >= 0; i--) {
    		timer.enqueue(i);
    	}
    }
    
    /** Starts the conveyor belt queue.*/
    private void startConveyorBelt() {
    	conveyorBelt = new ListQueue<>();
    	for (int i = 0; i < 10; i++) {
    		conveyorBelt.enqueue(null);
    	}
    }
    
    /**
     * Generates a new part. If the time is 0, it produces a part. If it isn't, a null space is added to the
     * conveyor belt. We create a new part which all of them have the same name and ID. We generate a random
     * weight based on the part's weight error for the machine. We also calculate the amount of parts that
     * are defective and count all the parts produced.
     * 
     * @return The part in front of the conveyor belt.
     * 
    */
    public CarPart produceCarPart() {
       if (tickTimer() != 0) {
    	   conveyorBelt.enqueue(null);
       }
       else {
    	   int partID = p1.getId();
    	   String partName = p1.getName();
    	   
    	   double min = p1.getWeight() - getPartWeightError();
    	   double max = p1.getWeight() + getPartWeightError();
    	   double randomWeight = min + random.nextDouble() * (max - min);
    	   
    	   boolean isDefective = getChanceOfDefective() != 0 && (getTotalPartsProduced() % getChanceOfDefective()) == 0;
    	   
    	   CarPart newPart = new CarPart(partID, partName, randomWeight, isDefective);
    	   conveyorBelt.enqueue(newPart);
    	   setTotalPartsProduced(getTotalPartsProduced() + 1);
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
