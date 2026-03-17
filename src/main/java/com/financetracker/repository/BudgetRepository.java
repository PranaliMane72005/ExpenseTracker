package com.financetracker.repository;

import com.financetracker.model.Budget;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    List<Budget> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, LocalDate endDate, LocalDate startDate);
    Optional<Budget> findByUserAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, Transaction.Category category, LocalDate endDate, LocalDate startDate);
}
