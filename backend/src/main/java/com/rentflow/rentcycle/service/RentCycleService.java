package com.rentflow.rentcycle.service;

import com.rentflow.common.exception.ResourceNotFoundException;
import com.rentflow.lease.entity.Lease;
import com.rentflow.lease.enums.LeaseStatus;
import com.rentflow.lease.repository.LeaseRepository;
import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import com.rentflow.rentcycle.mapper.RentCycleMapper;
import com.rentflow.rentcycle.repository.RentCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rentflow.rentcycle.dto.RentCycleResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RentCycleService {

    private final RentCycleRepository rentCycleRepository;
    private final RentCycleMapper rentCycleMapper;
    private final LeaseRepository leaseRepository;

    public List<RentCycleResponse> generateCycles(UUID leaseId){

        Lease lease=leaseRepository.findByIdAndActiveTrue(leaseId)
                .orElseThrow(()->new ResourceNotFoundException("Lease not found with id: "+leaseId));

        if(lease.getLeaseStatus()!= LeaseStatus.ACTIVE){
            throw new IllegalStateException("Rent CYcles can only be generated for an active lease");
        }

        List<RentCycle> cycles=new ArrayList<>();

        LocalDate cycleStart=lease.getLeaseStartDate();

        while(!cycleStart.isAfter(lease.getLeaseEndDate())){

            LocalDate cycleEnd=cycleStart.plusMonths(1).minusDays(1);

            if(cycleEnd.isAfter(lease.getLeaseEndDate())){
                cycleEnd=lease.getLeaseEndDate();
            }

            LocalDate dueDate =calculateDueDate(cycleStart,lease.getPaymentDueDay());

            boolean exists=rentCycleRepository.existsByLeaseIdAndPeriodStartAndPeriodEnd(leaseId,cycleStart,cycleEnd);

            if(!exists){
                RentCycle cycle=new RentCycle();

                cycle.setLease(lease);
                cycle.setPeriodStart(cycleStart);
                cycle.setPeriodEnd(cycleEnd);
                cycle.setDueDate(dueDate);

                cycle.setAmountDue(
                        lease.getMonthlyRent()
                );

                cycle.setAmountPaid(
                        BigDecimal.ZERO
                );

                cycle.setBalanceDue(
                        lease.getMonthlyRent()
                );

                cycle.setStatus(
                        RentCycleStatus.PENDING
                );

                cycles.add(cycle);

            }

            cycleStart =cycleStart.plusMonths(1);
        }

        List<RentCycle> savedCycles=rentCycleRepository.saveAll(cycles);

        return savedCycles.stream()
                .map(rentCycleMapper::toResponse)
                .toList();


    }

    @Transactional(readOnly = true)
    public List<RentCycleResponse> getAllCycles() {

        return rentCycleRepository
                .findByActiveTrue()
                .stream()
                .map(rentCycleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentCycleResponse getCycleById(UUID id) {

        RentCycle cycle = rentCycleRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rent cycle not found with id: " + id
                                ));

        return rentCycleMapper.toResponse(cycle);
    }

    @Transactional(readOnly = true)
    public List<RentCycleResponse> getCyclesByLease(
            UUID leaseId) {

        if (!leaseRepository.findByIdAndActiveTrue(leaseId)
                .isPresent()) {

            throw new ResourceNotFoundException(
                    "Lease not found with id: " + leaseId
            );
        }

        return rentCycleRepository
                .findByLeaseIdAndActiveTrue(leaseId)
                .stream()
                .map(rentCycleMapper::toResponse)
                .toList();
    }

    public void updateOverdueCycles() {

        List<RentCycle> pendingCycles =
                rentCycleRepository
                        .findByStatusAndActiveTrue(
                                RentCycleStatus.PENDING
                        );

        LocalDate today = LocalDate.now();

        for (RentCycle cycle : pendingCycles) {

            if (cycle.getDueDate().isBefore(today)) {

                cycle.setStatus(
                        RentCycleStatus.OVERDUE
                );
            }
        }
        rentCycleRepository.saveAll(pendingCycles);
    }

    public void deleteCycle(UUID id) {

        RentCycle cycle = rentCycleRepository.findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rent cycle not found with id: " + id
                                ));

        if (cycle.getStatus() == RentCycleStatus.PAID) {
            throw new IllegalStateException(
                    "Paid rent cycle cannot be deleted"
            );
        }

        cycle.setActive(false);

        rentCycleRepository.save(cycle);
    }

    private LocalDate calculateDueDate(
            LocalDate cycleStart,
            int dueDay) {

        int validDay =
                Math.min(dueDay, cycleStart.lengthOfMonth());

        return cycleStart.withDayOfMonth(validDay);
    }

}
