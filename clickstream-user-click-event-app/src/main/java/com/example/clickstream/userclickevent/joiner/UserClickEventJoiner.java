package com.example.clickstream.userclickevent.joiner;

import clickstream.Event;
import clickstream.User;
import clickstream.UserClickEvent;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.stereotype.Component;

@Component
public class UserClickEventJoiner implements ValueJoiner<Event, User, UserClickEvent> {
    @Override
    public UserClickEvent apply(Event event, User user) {
        var userClickEvent = new UserClickEvent();
        userClickEvent.setUserid(event.getUserid());
        userClickEvent.setUsername(user.getUsername());
        userClickEvent.setIp(event.getIp());
        userClickEvent.setCity(user.getCity());
        userClickEvent.setRequest(event.getRequest());
        userClickEvent.setStatus(event.getStatus());
        userClickEvent.setBytes(event.getBytes());
        return userClickEvent;
    }
}
