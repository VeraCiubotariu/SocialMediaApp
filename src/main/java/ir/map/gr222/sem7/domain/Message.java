package ir.map.gr222.sem7.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime date;
    private String replyMessage;
    private static long generatedID = 1;

    public Message(Long from, List<Long> to, String message, LocalDateTime date) {
        this.setId(generatedID++);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyMessage = null;
    }

    public Message(Long id, Long from, List<Long> to, String message, LocalDateTime date) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyMessage = null;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addRecipient(Long user){
        if(!this.to.contains(user)){
            this.to.add(user);
        }
    }

    @Override
    public String toString() {
        return message;
    }
}
