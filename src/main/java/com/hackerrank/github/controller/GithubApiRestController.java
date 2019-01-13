package com.hackerrank.github.controller;

import com.hackerrank.github.exepctions.AlreadyRegisteredException;
import com.hackerrank.github.model.Actor;
import com.hackerrank.github.model.Event;
import com.hackerrank.github.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class GithubApiRestController {

    @Autowired
    EventRepository eventRepository;

    @DeleteMapping("/erase")
    @ResponseStatus(HttpStatus.OK)
    public void eraseAllEvents() {
        eventRepository.deleteAll();
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEvent(@RequestBody Event event) throws AlreadyRegisteredException {

        if(eventRepository.findOne(event.getId()) != null)
            throw new AlreadyRegisteredException("Event id: " + event.getId() + " already registered");

        eventRepository.saveAndFlush(event);
    }

    @GetMapping(value = "/events", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> retrieveEvents(@RequestBody Event event) {
        return eventRepository.findAll(new Sort(Sort.Direction.ASC, "id"));
    }

    @PutMapping(value = "/actors", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateAvatar(@RequestBody Actor actor) {

    }

    @GetMapping(value = "/actors", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveActors() {

    }

    @GetMapping(value = "/actors/streak", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void retrieveActorsByStreak() {

    }
}
