package xyz.andric.stepper.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import xyz.andric.stepper.message.MessageType;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    private MessageType type;
    private String data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
