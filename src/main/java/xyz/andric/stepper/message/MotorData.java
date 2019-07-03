package xyz.andric.stepper.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MotorData implements Serializable {
    private int speed;
    private Boolean isMotorStarted;

    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @return the isMotorStarted
     */
    public Boolean isMotorStarted() {
        return isMotorStarted;
    }

    /**
     * @param isMotorStarted the isMotorStarted to set
     */
    public void setMotorStarted(Boolean isMotorStarted) {
        this.isMotorStarted = isMotorStarted;
    }
}
