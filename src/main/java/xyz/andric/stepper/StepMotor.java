package xyz.andric.stepper;

import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class StepMotor {

    static GpioController gpio = GpioFactory.getInstance();

    static GpioPinDigitalOutput[] pins = { gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW) };

    private byte[] single_step_sequence = new byte[4];

    private static GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);

    private StepMotor() {
        gpio.setShutdownOptions(true, PinState.LOW, pins);

        motor.setStepsPerRevolution(48);
        // Bipolar motor
        single_step_sequence[0] = (byte) 0b0101;
        single_step_sequence[1] = (byte) 0b0110;
        single_step_sequence[2] = (byte) 0b1010;
        single_step_sequence[3] = (byte) 0b1001;

        // Unipolar motor
        // single_step_sequence[0] = (byte) 0b0001;
        // single_step_sequence[1] = (byte) 0b0010;
        // single_step_sequence[2] = (byte) 0b0100;
        // single_step_sequence[3] = (byte) 0b1000;
        motor.setStepSequence(single_step_sequence);
        motor.setStepInterval(2);
    }

    /** Create an instance of the class at the time of class loading */
    private static final StepMotor instance = new StepMotor();

    /** Provide a global point of access to the instance */
    public static StepMotor getInstance() {
        return instance;
    }

    public static void stop() {
        motor.stop();
    }

    public static void forward() {
        motor.forward();
    }

    public static void backward() {
        motor.reverse();
    }

    public static void setSpeed(int speed) {
        motor.setStepInterval(speed);
    }
}