package com.example.bank.domain.bonus.repository;

import com.example.bank.domain.bonus.model.CashbackRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbackRuleRepository extends JpaRepository<CashbackRule, Long> {

    List<CashbackRule> findByActiveTrue();

    List<CashbackRule> findByCategoryAndActiveTrue(String category);
}
