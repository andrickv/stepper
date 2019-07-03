package xyz.andric.stepper;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import com.pi4j.util.ConsoleColor;

/**
 * This example code demonstrates how to setup a listener for GPIO pin state
 * changes on the RaspberryPi.
 *
 * @author Robert Savage
 */
public class Sensor {

    private GpioController gpio = GpioFactory.getInstance();

    private GpioPinDigitalInput sensor;

    public Sensor() {
        sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27);
    }

    /**
     * @return the sensor
     */
    public GpioPinDigitalInput getSensor() {
        return sensor;
    }

}