package jmroy;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Jessica Roy
 *
 */
class TimedTask extends Task implements Serializable {

    // Instance variables

    private int minutes;

    // Constructors

    TimedTask(String name, int minutes) {
        super(name);
        this.minutes = minutes;
    }

    // Methods

    int getMinutes() {
        return minutes;
    }

    void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Prints the name of the task and the number of minutes followed by "min"
     */
    @Override
    public void display() {
        System.out.printf("%s\t%s\n", this.getName(), this.getTimeForDisplay());
    }

    @Override
    String getTimeForDisplay() {
        return getMinutes() + " min";
    }

    void countdown(Timer timer) {
        timer.schedule(new TimeIt(this.getMinutes(), this.getName()), 0, 1000);
    }

    private class TimeIt extends TimerTask {
        private int minutes;
        private String name;

        TimeIt (int min, String name) {
            this.minutes = min;
            this.name = name;
        }
        public void run() {
            System.out.printf("test %s: %d\n", name, minutes--);
            if (minutes <0 ) {
                cancel();
            }
        }
    }
}