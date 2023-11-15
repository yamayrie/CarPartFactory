package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import data_structures.BasicHashFunction;
import data_structures.DoublyLinkedList;
import data_structures.HashTableSC;
import data_structures.LinkedStack;
import data_structures.ArrayList;
import interfaces.List;
import interfaces.Map;
import interfaces.Stack;

public class CarPartFactory {
	
	
	private List<CarPart> carParts = new ArrayList<CarPart>();
	private List<PartMachine> machines;
	private List<Order> orders;
	private Map<Integer, CarPart> partCatalog;
	private Map<Integer, List<CarPart>> inventory;
	private Map<Integer, Integer> defectives;
	private Stack<CarPart> productionBin;
	
    public CarPartFactory(String orderPath, String partsPath) throws IOException {
    	
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

    public void setupOrders(String path) throws IOException {
    	this.orders = new DoublyLinkedList<>();
    	
    	try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    		String line;
    		reader.readLine();
    		
    		while ((line = reader.readLine()) != null) {
    			String[] split = line.split(",");
    			if (split.length == 3) {
    				int id = Integer.parseInt(split[0].trim());
    				String customer = split[1].trim();
    				String reqParts = split[2].trim();
    				Map<Integer, Integer> parts = parseReqParts(reqParts);
    				
    				Order order = new Order(id, customer, parts, false);
    				this.orders.add(order);
    			}
    		}
    		
    	}
    }
    
    private Map<Integer, Integer> parseReqParts(String reqParts) {
    	Map<Integer, Integer> parts = new HashTableSC<>(1, new BasicHashFunction()); // fix initialization
//    	reqParts = reqParts.replace("(", "").replace(")", "").split(" ");
    	reqParts = reqParts.replaceAll("[()]", "");
    	String[] tuples = reqParts.split("-");
    	for (String tuple : tuples) {
    		String[] keyValue = tuple.split(" ");
    		if (keyValue.length == 2) {
    			int partID = Integer.parseInt(keyValue[0].trim());
    			int amount = Integer.parseInt(keyValue[1].trim());
   				parts.put(partID, amount);
   			}
   		}
    	return parts;
    }

    public void setupMachines(String path) throws IOException {
//    	DoublyLinkedList<Order> orders = new DoublyLinkedList<>();
    	
    	try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    		String line;
    		reader.readLine();
    		
    		while ((line = reader.readLine()) != null) {
    			String[] split = line.split(",");
    			if (split.length == 6) {
    				int id = Integer.parseInt(split[0].trim());
    				String partName = split[1].trim();
    				double weight = Double.parseDouble(split[2].trim());
    				double weightError = Double.parseDouble(split[3].trim());
    				int period = Integer.parseInt(split[4].trim());
    				int chanceOfDefective = Integer.parseInt(split[5].trim());
    				
    				CarPart part = new CarPart(id, partName, weight, false);
    				carParts.add(part);
    				this.getPartCatalog().put(id, part);
    				
    				if (!this.getInventory().containsKey(id)) {
    					this.getInventory().put(id, new DoublyLinkedList<>());
    				}
    				this.getInventory().get(id).add(part);
    				PartMachine machine = new PartMachine(id, part, period, weightError, chanceOfDefective);
    				this.getMachines().add(machine);
    			}
    		}
    		
    	}
    }
   
    
    public void setupCatalog() {
    	for (int i = 0; i < carParts.size(); i++) {
    		partCatalog.put(carParts.get(i).getId(), carParts.get(i));
    	}
    }
    
    
    
    
    public void setupInventory() {
//        if (this.inventory == null) {
//        	this.inventory = new HashTableSC<>(1, new BasicHashFunction()); // fix initialization
//        }
        	
    	
    	
        for (CarPart carPart : this.partCatalog.getValues()) {
        	int partID = carPart.getId();
        	this.inventory.put(partID, new ArrayList<CarPart>());
        }
        
    }
    
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
        		machine.resetConveyorBelt();
        		this.storeInInventory();
        	}
        }
        this.processOrders();
        this.generateReport();
    }

    public void processOrders() { /////////////// ??? Fix ??? Finish ??? ///////////////
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
        
//        	for (Integer partID : reqParts.getValues()) {
//        		Integer reqCount = reqParts.get(partID);
//        		
//        		if (reqCount == null || !this.inventory.containsKey(partID) || this.inventory.get(partID).isEmpty() || this.inventory.get(partID).size() < reqCount) {
//        			canExecuteOrder = false;
//        			break;
//        		}
//        	}
        	
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
