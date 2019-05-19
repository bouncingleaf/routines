package jmroy;

/**
 * 
 * @author Jessica Roy
 *
 */
abstract class Task implements Displayable {
    
    // Instance variables
    
    private String name;

    Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getTimeForDisplay();

}
