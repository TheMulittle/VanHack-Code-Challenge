package com.hackerrank.github.repository;

import com.hackerrank.github.model.Event;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByIdAsc();
    List<Event> findAllByActorIdOrderByIdAsc(Long actorId);
    List<Event> findAllByActorIdOrderByCreatedAtAsc(Long actorId);
    int countByActorId(Long actorId);
    Event findFirstByActorIdOrderByCreatedAtDesc(Long id);
}
