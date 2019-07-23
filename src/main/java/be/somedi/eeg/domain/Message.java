package be.somedi.eeg.domain;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Message {

    private String messageDateTime, messageID, actionDateTime;

    public Message() {
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        messageDateTime = date.replace(" ", "T").concat("Z");
        messageID = date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + new Random().nextInt(10000);
        actionDateTime = messageDateTime;
    }

    public String getMessageDateTime() {
        return messageDateTime;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getActionDateTime() {
        return actionDateTime;
    }

    @Override
    public String toString() {
        return "Message [messageDateTime=" + messageDateTime + ", messageID=" + messageID + ", actionDateTime="
                + actionDateTime + "]";
    }
}
