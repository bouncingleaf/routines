package jmroy;

/**
 * 
 * @author Jessica Roy
 *
 */
class TimedTask extends Task {

    // Instance variables

    private int minutes;

    // Constructors

    public TimedTask(String name, int minutes) {
        super(name);
        this.minutes = minutes;
    }

    // Methods

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
    
    @Override
    public void display() {
        System.out.printf("%s\t%d min\n", this.getName(), this.minutes);
    }
    
    @Override
    public String getTimeForDisplay() {
        return getMinutes() + " min";
    }

}