package com.example.bank.domain.credit.repository;

import com.example.bank.domain.credit.model.Credit;
import com.example.bank.domain.credit.model.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, Long> {

    List<InstallmentPlan> findByCreditOrderByInstallmentNoAsc(Credit credit);

    List<InstallmentPlan> findByCreditAndPaidIsFalse(Credit credit);
}