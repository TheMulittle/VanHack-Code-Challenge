package com.hackerrank.github.controller;

import com.hackerrank.github.exepctions.AlreadyRegisteredException;
import com.hackerrank.github.exepctions.InvalidUpdate;
import com.hackerrank.github.exepctions.NotFound;
import com.hackerrank.github.model.Actor;
import com.hackerrank.github.model.Event;
import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import com.hackerrank.github.repository.RepoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
public class GithubApiRestController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    RepoRepository repoRepository;

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

        actorRepository.saveAndFlush(event.getActor());
        repoRepository.saveAndFlush(event.getRepo());
        eventRepository.saveAndFlush(event);
    }

    @GetMapping(value = "/events", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> retrieveEvents() {
        return eventRepository.findAllByOrderByIdAsc();
    }

    @GetMapping(value = "/events/actors/{actorID}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Event> retrieveEventsByActor(@PathVariable("actorID") Long actorId) throws NotFound {

        if(actorRepository.findOne(actorId) == null)
            throw new NotFound("Actor with id: [" + actorId + "] not found");

        return eventRepository.findAllByActorIdOrderByIdAsc(actorId);
    }

    @PutMapping(value = "/actors", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateAvatar(@RequestBody Actor actor) throws NotFound, InvalidUpdate {
        Actor actorInDb = actorRepository.findOne(actor.getId());
        if(actorInDb == null)
            throw new NotFound("Actor with id: [" + actor.getId() + "] not found");

        if(!actor.getLogin().equals(actorInDb.getLogin()))
            throw new InvalidUpdate("Actor with id: [" + actor.getId() + "] cannot update fields other than avatar");

        actorRepository.saveAndFlush(actor);

    }

    @GetMapping(value = "/actors", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Actor> retrieveActors() {
        List<Actor> actors = actorRepository.findAll();

        actors.sort(getComparatorByActorsAscTimestampDescLoginAsc());

        return actors;
    }

    @GetMapping(value = "/actors/streak", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Actor> retrieveActorsByStreak() {
        List<Actor> actors = actorRepository.findAll();
        actors.sort(comp());
        return actors;
    }

    Comparator<Actor> getComparatorByActorsAscTimestampDescLoginAsc() {

        Comparator<Actor> comp = (a1, a2) -> {
            int compareByNumEvents = eventRepository.countByActorId(a2.getId()) - eventRepository.countByActorId(a1.getId());
            int compareByDate;
            if (compareByNumEvents == 0) {

                compareByDate = eventRepository.findFirstByActorIdOrderByCreatedAtDesc(a2.getId()).getCreatedAt().
                        compareTo(eventRepository.findFirstByActorIdOrderByCreatedAtDesc(a1.getId()).getCreatedAt());

                if(compareByDate == 0) {
                    return a1.getLogin().compareTo(a2.getLogin());
                }

                return compareByDate;
            }

            return compareByNumEvents;
        };

        return comp;
    }

    Comparator<Actor> comp() {

        Comparator<Actor> comp = (a1, a2) -> {
            List<Event> e1 = eventRepository.findAllByActorIdOrderByCreatedAtAsc(a1.getId());
            List<Event> e2 = eventRepository.findAllByActorIdOrderByCreatedAtAsc(a2.getId());
            int compareByStreak = calcMaximumStreak(e1) - calcMaximumStreak(e2);

            if (compareByStreak == 0) {

                int compareByDate = eventRepository.findFirstByActorIdOrderByCreatedAtDesc(a2.getId()).getCreatedAt().
                        compareTo(eventRepository.findFirstByActorIdOrderByCreatedAtDesc(a1.getId()).getCreatedAt());

                if (compareByDate == 0) {
                    return a1.getLogin().compareTo(a2.getLogin());
                }

                return compareByDate;
            }

            return compareByStreak;
        };

        return comp;
    }

    private int calcMaximumStreak(List<Event> events) {

        int maxStreak = 0;
        int actualStreak = 0;
        for(int i = 1; i < events.size(); i++) {
            LocalDateTime dateEvent1 = events.get(i-1).getCreatedAt().toLocalDateTime().withHour(0).withMinute(0).withSecond(0).plusDays(1);
            LocalDateTime dateEvent2 = events.get(i).getCreatedAt().toLocalDateTime().withHour(0).withMinute(0).withSecond(0);
            if(dateEvent1.isEqual(dateEvent2)) {
                actualStreak += 1;
            } else {
                actualStreak = 0;
            }

            if(actualStreak > maxStreak) {
                maxStreak = actualStreak;
            }
        }

        return maxStreak;
    }
}
