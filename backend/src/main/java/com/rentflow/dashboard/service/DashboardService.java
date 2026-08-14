package com.rentflow.dashboard.service;

import com.rentflow.dashboard.dto.DashboardResponse;
import com.rentflow.lease.enums.LeaseStatus;
import com.rentflow.lease.repository.LeaseRepository;
import com.rentflow.payment.enums.PaymentMethod;
import com.rentflow.payment.repository.PaymentRepository;
import com.rentflow.property.repository.PropertyRepository;
import com.rentflow.rentcycle.entity.RentCycle;
import com.rentflow.rentcycle.enums.RentCycleStatus;
import com.rentflow.rentcycle.repository.RentCycleRepository;
import com.rentflow.unit.enums.OccupancyStatus;
import com.rentflow.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final LeaseRepository leaseRepository;
    private final RentCycleRepository rentCycleRepository;
    private final PaymentRepository paymentRepository;

    public DashboardResponse getDashboard() {

        DashboardResponse response =
                new DashboardResponse();

        /*
         * PROPERTY SUMMARY
         */

        long totalProperties =
                propertyRepository.count();

        long activeProperties =
                propertyRepository.findByActiveTrue().size();

        response.setTotalProperties(totalProperties);
        response.setActiveProperties(activeProperties);


        /*
         * UNIT SUMMARY
         */

        List<com.rentflow.unit.entity.Unit> units =
                unitRepository.findByActiveTrue();

        long totalUnits = units.size();

        long occupiedUnits =
                units.stream()
                        .filter(unit ->
                                unit.getOccupancyStatus()
                                        == OccupancyStatus.OCCUPIED)
                        .count();

        long vacantUnits =
                units.stream()
                        .filter(unit ->
                                unit.getOccupancyStatus()
                                        == OccupancyStatus.VACANT)
                        .count();

        response.setTotalUnits(totalUnits);
        response.setOccupiedUnits(occupiedUnits);
        response.setVacantUnits(vacantUnits);


        /*
         * LEASE SUMMARY
         */

        long activeLeases =
                leaseRepository
                        .findByActiveTrue()
                        .stream()
                        .filter(lease ->
                                lease.getLeaseStatus()
                                        == LeaseStatus.ACTIVE)
                        .count();

        long expiredLeases =
                leaseRepository
                        .findByActiveTrue()
                        .stream()
                        .filter(lease ->
                                lease.getLeaseStatus()
                                        == LeaseStatus.EXPIRED)
                        .count();

        response.setActiveLeases(activeLeases);
        response.setExpiredLeases(expiredLeases);


        /*
         * RENT SUMMARY
         */

        List<RentCycle> rentCycles =
                rentCycleRepository
                        .findByActiveTrue();

        BigDecimal totalRentDue =
                rentCycles.stream()
                        .map(RentCycle::getAmountDue)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalCollected =
                rentCycles.stream()
                        .map(RentCycle::getAmountPaid)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalOutstanding =
                rentCycles.stream()
                        .map(RentCycle::getBalanceDue)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalOverdue =
                rentCycles.stream()
                        .filter(cycle ->
                                cycle.getStatus()
                                        == RentCycleStatus.OVERDUE)
                        .map(RentCycle::getBalanceDue)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        response.setTotalRentDue(totalRentDue);
        response.setTotalCollected(totalCollected);
        response.setTotalOutstanding(totalOutstanding);
        response.setTotalOverdue(totalOverdue);


        /*
         * PAYMENT SUMMARY
         */

        long totalPayments =
                paymentRepository
                        .findByActiveTrue()
                        .size();

        long cashPayments =
                paymentRepository
                        .findByActiveTrue()
                        .stream()
                        .filter(payment ->
                                payment.getPaymentMethod()
                                        == PaymentMethod.CASH)
                        .count();

        long upiPayments =
                paymentRepository
                        .findByActiveTrue()
                        .stream()
                        .filter(payment ->
                                payment.getPaymentMethod()
                                        == PaymentMethod.UPI)
                        .count();

        response.setTotalPayments(totalPayments);
        response.setCashPayments(cashPayments);
        response.setUpiPayments(upiPayments);

        return response;
    }
}