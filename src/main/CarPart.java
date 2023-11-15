package main;

public class CarPart {
    
	private int id;
	private String name;
	private double weight;
	private boolean isDefective;
	private double partWeightError;
	private int chanceOfDefective;
	
    public CarPart(int id, String name, double weight, boolean isDefective) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.isDefective = isDefective;
        this.partWeightError = 0.0;
        this.chanceOfDefective = 0;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getWeight() {
    	return weight;
    }
    
    public void setWeight(double weight) {
      this.weight = weight;
    }

    public boolean isDefective() {
    	return isDefective;
    }
    
    public void setDefective(boolean isDefective) {
        this.isDefective = isDefective;
    }
    
    /**
     * Returns the parts name as its string representation
     * @return (String) The part name
     */
    public String toString() {
        return this.getName();
    }

	public double getPartWeightError() {
		return partWeightError;
	}
	
	public void setPartWeightError(double partWeightError) {
		this.partWeightError = partWeightError;
	}

	public int getChanceOfDefective() {
		return chanceOfDefective;
	}
	
	public void setChanceOfDefective(int chanceOfDefective) {
		this.chanceOfDefective = chanceOfDefective;
	}
	
}





