package com.financetracker.service;

import com.financetracker.dto.SavingsGoalDto;
import com.financetracker.model.SavingsGoal;
import com.financetracker.model.User;
import com.financetracker.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SavingsGoalService {

    @Autowired private SavingsGoalRepository savingsGoalRepository;

    @Transactional
    public SavingsGoal create(User user, SavingsGoalDto.Request req) {
        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .goalName(req.getGoalName())
                .targetAmount(req.getTargetAmount())
                .currentAmount(req.getCurrentAmount() != null ? req.getCurrentAmount() : BigDecimal.ZERO)
                .deadline(req.getDeadline())
                .description(req.getDescription())
                .build();
        return savingsGoalRepository.save(goal);
    }

    @Transactional
    public SavingsGoal update(User user, Long id, SavingsGoalDto.Request req) {
        SavingsGoal goal = getOwned(user, id);
        goal.setGoalName(req.getGoalName());
        goal.setTargetAmount(req.getTargetAmount());
        if (req.getCurrentAmount() != null) goal.setCurrentAmount(req.getCurrentAmount());
        goal.setDeadline(req.getDeadline());
        goal.setDescription(req.getDescription());
        return savingsGoalRepository.save(goal);
    }

    @Transactional
    public SavingsGoal addAmount(User user, Long id, BigDecimal amount) {
        SavingsGoal goal = getOwned(user, id);
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(SavingsGoal.GoalStatus.COMPLETED);
        }
        return savingsGoalRepository.save(goal);
    }

    @Transactional
    public void delete(User user, Long id) {
        SavingsGoal goal = getOwned(user, id);
        savingsGoalRepository.delete(goal);
    }

    public List<SavingsGoal> getAll(User user) {
        return savingsGoalRepository.findByUser(user);
    }

    private SavingsGoal getOwned(User user, Long id) {
        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        return goal;
    }
}
