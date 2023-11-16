package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import data_structures.BasicHashFunction;
import data_structures.DoublyLinkedList;
import data_structures.HashTableSC;
import data_structures.LinkedStack;
import interfaces.List;
import interfaces.Map;
import interfaces.Stack;


/**
 * Class that represents a factory that manages machines, orders, parts, inventory, among others for car parts. Includes methods
 * for setting up orders, setting up machines, setting up and storing in inventory, running the factory, and processing orders.
 * Generates a report showing how many parts were produced, how many are defective, and how many orders were fulfilled.
 * 
*/
public class CarPartFactory {
	
	private List<CarPart> carParts = new DoublyLinkedList<CarPart>();
	private List<PartMachine> machines;
	private List<Order> orders;
	private Map<Integer, CarPart> partCatalog;
	private Map<Integer, List<CarPart>> inventory;
	private Map<Integer, Integer> defectives;
	private Stack<CarPart> productionBin;

	/**
	 * Constructs a new CarPartFactory instance and initializes it by reading data from files.
	 * 
	 * @param orderPath Path for the order file.
	 * @param partsPath Path for the parts file.
	 * @throws IOException if an error occurs while reading the data from the files.
	 * 
	*/
    public CarPartFactory(String orderPath, String partsPath) throws IOException {
    	// Initialization of setup methods, maps, and lists
    	this.partCatalog = new HashTableSC<>(1, new BasicHashFunction());
    	this.inventory = new HashTableSC<>(1, new BasicHashFunction());
    	this.defectives = new HashTableSC<>(1, new BasicHashFunction());
    	
    	this.machines = new DoublyLinkedList<>();
    	this.productionBin = new LinkedStack<>();
    	
    	setupOrders("input/orders.csv");
    	setupMachines("input/parts.csv");
    	
    	setupCatalog();
    	setupInventory();
    }
    
    // Getters and setters for fields
    public List<PartMachine> getMachines() {
    	return machines;
    }
    
    public void setMachines(List<PartMachine> machines) {
        this.machines = machines;
    }
    
    public Stack<CarPart> getProductionBin() {
    	return productionBin;
    }
    
    public void setProductionBin(Stack<CarPart> production) {
    	this.productionBin = production;
    }
    
    public Map<Integer, CarPart> getPartCatalog() {
        return partCatalog;
    }
    
    public void setPartCatalog(Map<Integer, CarPart> partCatalog) {
        this.partCatalog = partCatalog;
    }
    
    public Map<Integer, List<CarPart>> getInventory() {
    	return inventory;
    }
    
    public void setInventory(Map<Integer, List<CarPart>> inventory) {
    	this.inventory = inventory;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    public Map<Integer, Integer> getDefectives() {
    	return defectives;
    }
    
    public void setDefectives(Map<Integer, Integer> defectives) {
    	this.defectives = defectives;
    }

    /**
     * Reads order data from orders.csv. Each line is expected to contain comma-separated values representing
     * order attributes. It skips the first line which contains a header of the file's format for storing.
     * 
     * @param path Path for the order file.
     * @throws IOException if an error occurs while reading the data from the file.
     * 
    */
    public void setupOrders(String path) throws IOException {
    	this.orders = new DoublyLinkedList<>();
    	
    	try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    		String line; // stores each line read from the file
    		reader.readLine(); // skip the first line
    		
    		while ((line = reader.readLine()) != null) {
    			String[] split = line.split(",");
    			if (split.length == 3) {
    				
    				// splits the line in their specific type
    				int id = Integer.parseInt(split[0].trim());
    				String customer = split[1].trim();
    				String reqParts = split[2].trim();
    				
    				// uses the reqParts variable as a parameter for another method to parse the data and add it to a map
    				Map<Integer, Integer> parts = parseReqParts(reqParts);
    				
    				// creates a new order with the split data and adds it to the order list
    				Order order = new Order(id, customer, parts, false);
    				this.orders.add(order);
    			}
    		}
    		
    	}
    	
    }
    
    /**
     * Parses the requested parts and creates a map with the part ID as the key and the amount needed by the customer as the value.
     * 
     * @param reqParts The string to parse that has the requested parts.
     * @return A map containing the parts IDs and amount requested.
     * 
    */
    private Map<Integer, Integer> parseReqParts(String reqParts) {
    	
    	Map<Integer, Integer> parts = new HashTableSC<>(1, new BasicHashFunction());
    	
    	reqParts = reqParts.replaceAll("[()]", "");
    	String[] tuples = reqParts.split("-");
    	
    	for (String tuple : tuples) {
    		String[] keyValue = tuple.split(" ");
    		if (keyValue.length == 2) {
    			
    			// splits the line in their specific type
    			int partID = Integer.parseInt(keyValue[0].trim());
    			int amount = Integer.parseInt(keyValue[1].trim());
    			
    			// adds the partID and amount as a key value in a map
   				parts.put(partID, amount);
   			}
   		}
    	
    	return parts;
    }

    /**
     * Sets up the machine based on parts.csv file. 
     * 
     * Each line is expected to contain comma-separated values representing part attributes. The file's format is
     * ID,PartName,Weight,WeightError,Period,ChanceOfDefective . The method iterates each line, creates PartMachine
     * objects, and adds their information. It skips the first line which contains a header of the file's format for storing.
     * 
     * @param path Path for the parts file.
     * @throws IOException if an error occurs while reading the data from the file.
     * 
    */
    public void setupMachines(String path) throws IOException {
    	
    	try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    		String line; // stores each line read from the file
    		reader.readLine(); // skips the first line
    		
    		while ((line = reader.readLine()) != null) {
    			String[] split = line.split(",");
    			if (split.length == 6) {
    				
    				// splits the line in their specific type
    				int id = Integer.parseInt(split[0].trim());
    				String partName = split[1].trim();
    				double weight = Double.parseDouble(split[2].trim());
    				double weightError = Double.parseDouble(split[3].trim());
    				int period = Integer.parseInt(split[4].trim());
    				int chanceOfDefective = Integer.parseInt(split[5].trim());
    				
    				// creates a new CarPart instance and adds it to the list and catalog
    				CarPart part = new CarPart(id, partName, weight, false);
    				carParts.add(part);
    				this.getPartCatalog().put(id, part);
    				
    				// checks if the inventory has the part ID and if not it adds an empty list
    				if (!this.getInventory().containsKey(id)) {
    					this.getInventory().put(id, new DoublyLinkedList<>());
    				}
    				
    				// adds the part to the inventory
    				this.getInventory().get(id).add(part);
    				
    				// creates a new machine with the split data and adds it to the machine list
    				PartMachine machine = new PartMachine(id, part, period, weightError, chanceOfDefective);
    				this.getMachines().add(machine);
    			}
    		}
    		
    	}
    	
    }
    
    /** Sets up the catalog. */
    public void setupCatalog() {
    	for (int i = 0; i < carParts.size(); i++) {
    		partCatalog.put(carParts.get(i).getId(), carParts.get(i));
    	}
    }
    
    /** Sets up the inventory. */
    public void setupInventory() {    	
        for (CarPart carPart : this.partCatalog.getValues()) {
        	int partID = carPart.getId();
        	this.inventory.put(partID, new DoublyLinkedList<CarPart>());
        }
    }
    
    /**
     * Processes car parts in the production bin and stores them in the inventory. The parts that are
     * not defective are thrown in the inventory and the ones that are defective are counted. Initializes
     * the inventory if null. 
     * 
    */
    public void storeInInventory() {
       if (this.inventory == null) {
    	   this.setupInventory();
       }
       
       while (!this.productionBin.isEmpty()) {
    	   CarPart part = this.productionBin.pop();
    	   if (part.isDefective()) {
    		   int partID = part.getId();
    		   int defectiveCount = this.defectives.containsKey(partID) ? defectives.get(partID) : 0;
    		   defectives.put(partID, defectiveCount + 1);
    	   }
    	   else {
    		   int partID = part.getId();
    		   List<CarPart> partsInventory;
    		   
    		   if (inventory.containsKey(partID)) {
    			   partsInventory = inventory.get(partID);
    		   }
    		   else {
    			   partsInventory = new DoublyLinkedList<>();
    		   }
    		   partsInventory.add(part);
    		   inventory.put(partID, partsInventory);
    	   }
       }
       
    }
    
    /**
     * Simulates the amount of days and minutes per day the factory will operate. If there is something available
     * in the production bin, it gets thrown to the production bin. When the day finishes, it resets the conveyor belt
     * and stores the items in the inventory. Processes the orders when all the days given have passed.
     * 
     * @param days The amount of days the factory will run 
     * @param minutes The amount of minutes per day the factory runs.
     * 
    */
    public void runFactory(int days, int minutes) {
        for (int d = 1; d <= days; d++) {
        	for (int m = 1; m <= minutes; m++) {        		
        		for (PartMachine machine : this.getMachines()) {
        			CarPart partProduced = machine.produceCarPart();
        			if (partProduced != null) {
        				this.productionBin.push(partProduced);
        			}
        		}
        	}
        	
        	for (PartMachine machine : this.getMachines()) {
        		while (!machine.getConveyorBelt().isEmpty()) {
        			if (machine.getConveyorBelt().front() != null) {
        				productionBin.push(machine.getConveyorBelt().dequeue());
        			}
        			else {
        				machine.getConveyorBelt().dequeue();
        			}
        		}
        		machine.resetConveyorBelt();
        	}
        	
        	this.storeInInventory();
        }
        
        this.processOrders();
    }

    /**
     * Checks the inventory and if the parts are available it starts the orders. The orders are fulfilled if
     * all the parts are available and are removed from inventory. Otherwise they are not fulfilled and the
     * parts are kept in inventory.
     * 
    */   
    public void processOrders() {
        for (Order order : this.orders) {
        	if (order.isFulfilled()) {
        		continue;
        	}
        	
        	Map<Integer, Integer> reqParts = order.getRequestedParts();
        	boolean canExecuteOrder = true;
        	
        	List<Integer> partIDs = reqParts.getKeys();
        	List<Integer> reqCounter = reqParts.getValues();
        	
        	for (int i = 0; i < partIDs.size(); i++) {
        		Integer partID = partIDs.get(i);
        		Integer reqCount = reqCounter.get(i);
        		
        		if (reqCount == null || !this.inventory.containsKey(partID) || this.inventory.get(partID).isEmpty() || this.inventory.get(partID).size() < reqCount) {
        			canExecuteOrder = false;
        			break;
        		}
        		
        	}
        	
        	if (canExecuteOrder) {
        		for (int i = 0; i < partIDs.size(); i++) {
        			Integer partID = partIDs.get(i);
            		Integer reqCount = reqCounter.get(i);
        			
        			for (int j = 0; j < reqCount; j++) {
        				if (!this.inventory.get(partID).isEmpty()) {
        					this.inventory.get(partID).remove(this.inventory.get(partID).size() - 1);
        				}
        			}
        			
        		}
        		order.setFulfilled(true);
        	}
        	
        }
    }
    
    /**
     * Generates a report indicating how many parts were produced per machine,
     * how many of those were defective and are still in inventory. Additionally, 
     * it also shows how many orders were successfully fulfilled. 
     */
    public void generateReport() {
        String report = "\t\t\tREPORT\n\n";
        report += "Parts Produced per Machine\n";
        for (PartMachine machine : this.getMachines()) {
            report += machine + "\t(" + 
            this.getDefectives().get(machine.getPart().getId()) +" defective)\t(" + 
            this.getInventory().get(machine.getPart().getId()).size() + " in inventory)\n";
        }
       
        report += "\nORDERS\n\n";
        for (Order transaction : this.getOrders()) {
            report += transaction + "\n";
        }
        System.out.println(report);
    }

   

}
