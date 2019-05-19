package jmroy;

/**
 * 
 * @author Jessica Roy
 *
 */
class UntimedTask extends Task {

    // Constructors
    
    public UntimedTask(String name) {
        super(name);
    }

    // Methods
    
    @Override
    public void display() {
        System.out.printf("%s\t%s\n", this.getName(), this.getTimeForDisplay());
    }
    
    @Override
    public String getTimeForDisplay() {
        return "(untimed)";
    }

}