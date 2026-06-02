package com.example.eventledger.repository;

import com.example.eventledger.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String> {

    List<EventEntity> findByAccountIdOrderByEventTimestampAsc(String accountId);
}
