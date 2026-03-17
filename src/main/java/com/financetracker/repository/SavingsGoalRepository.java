package com.financetracker.repository;

import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
    List<SavingsGoal> findByUserAndStatus(User user, SavingsGoal.GoalStatus status);
}
