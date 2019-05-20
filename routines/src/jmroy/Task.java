package jmroy;

/**
 * 
 * @author Jessica Roy
 *
 */
abstract class Task implements Displayable {

    // Instance variables

    private String name;

    // Constructors

    Task(String name) {
        this.name = name;
    }

    // Methods

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    abstract String getTimeForDisplay();

}
