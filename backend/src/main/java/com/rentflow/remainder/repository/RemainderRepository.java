package com.rentflow.remainder.repository;

import com.rentflow.remainder.entity.Remainder;
import com.rentflow.remainder.enums.RemainderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RemainderRepository extends JpaRepository<Remainder, UUID> {

    boolean existsByRentCycleIdAndRemainderTypeAndSentAtBetween(
            UUID rentCycleId,
            RemainderType remainderType,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );


}
