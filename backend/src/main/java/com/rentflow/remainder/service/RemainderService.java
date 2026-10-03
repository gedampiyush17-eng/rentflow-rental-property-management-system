package com.rentflow.remainder.service;

import com.rentflow.remainder.entity.Remainder;
import com.rentflow.remainder.enums.RemainderType;
import com.rentflow.remainder.repository.RemainderRepository;
import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import com.rentflow.rentcycle.repository.RentCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemainderService {
    private final RemainderRepository remainderRepository;
    private final RentCycleRepository rentCycleRepository;

    @Transactional
    public void processRemainders(){

        List<RentCycle> rentCycles=rentCycleRepository.findByActiveTrue();

        LocalDateTime startOfDay=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        LocalDateTime endOfDay=LocalDateTime.of(LocalDate.now(),LocalTime.MAX);

        for(RentCycle rentCycle: rentCycles){
            if(rentCycle.getStatus()== RentCycleStatus.PAID){
                continue;
            }

            processRentCycle(rentCycle,startOfDay,endOfDay);
        }
    }

    private void processRentCycle(RentCycle rentCycle,LocalDateTime startOfDay,LocalDateTime endOfDay){

        LocalDate today=LocalDate.now();

        if(rentCycle.getDueDate().isEqual(today)){

            sendRemainderIfNeeded(rentCycle, RemainderType.RENT_DUE_SOON,startOfDay,endOfDay);
        }
    }

    private void sendRemainderIfNeeded(RentCycle rentCycle,RemainderType remainderType, LocalDateTime startOfDay, LocalDateTime endOfDay){

        boolean alreadySentToday=remainderRepository.existsByRentCycleIdAndRemainderTypeAndSentAtBetween(rentCycle.getId(),remainderType,startOfDay,endOfDay);

        if(alreadySentToday){
            return;
        }

        String recipient=rentCycle.getLease()
                .getTenant()
                .getEmail();

        System.out.println(
                "Sending "
                        + remainderType
                        + " remainder to "
                        + recipient
                        + " for RentCycle "
                        + rentCycle.getId()
        );

        Remainder remainder=new Remainder();

        remainder.setRemainderType(remainderType);

        remainder.setSentAt(LocalDateTime.now());

        remainder.setRecipient(recipient);

        remainder.setRentCycle(rentCycle);

        remainderRepository.save(remainder);
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void scheduledRemainderCheck() {

        processRemainders();
    }
}
