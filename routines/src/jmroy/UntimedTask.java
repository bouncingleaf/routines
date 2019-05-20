package jmroy;

/**
 * 
 * @author Jessica Roy
 *
 */
class UntimedTask extends Task {

    // Constructors
    
    UntimedTask(String name) {
        super(name);
    }

    // Methods
    
    @Override
    public void display() {
        System.out.printf("%s\t%s\n", this.getName(), this.getTimeForDisplay());
    }
    
    @Override
    String getTimeForDisplay() {
        return "(untimed)";
    }

}