package com.example.eventledger.repository;

import com.example.eventledger.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String> {

    List<EventEntity> findByAccountIdOrderByEventTimestampAsc(String accountId);

    @Query("""
            SELECT COALESCE(
                SUM(
                    CASE
                        WHEN e.type = 'CREDIT' THEN e.amount
                        ELSE -e.amount
                    END
                ), 0
            )
            FROM EventEntity e
            WHERE e.accountId = :accountId
            """)
    BigDecimal calculateBalance(@Param("accountId") String accountId);
}
