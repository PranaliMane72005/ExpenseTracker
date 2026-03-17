package com.financetracker.service;

import com.financetracker.dto.TransactionDto;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;

    @Transactional
    public Transaction create(User user, TransactionDto.Request req) {
        Transaction t = Transaction.builder()
                .user(user)
                .type(req.getType())
                .amount(req.getAmount())
                .category(req.getCategory())
                .description(req.getDescription())
                .date(req.getDate())
                .account(req.getAccount())
                .build();
        return transactionRepository.save(t);
    }

    @Transactional
    public Transaction update(User user, Long id, TransactionDto.Request req) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!t.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        t.setType(req.getType());
        t.setAmount(req.getAmount());
        t.setCategory(req.getCategory());
        t.setDescription(req.getDescription());
        t.setDate(req.getDate());
        t.setAccount(req.getAccount());
        return transactionRepository.save(t);
    }

    @Transactional
    public void delete(User user, Long id) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        if (!t.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        transactionRepository.delete(t);
    }

    public List<Transaction> getAll(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public List<Transaction> getByDateRange(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, start, end);
    }

    public TransactionDto.Summary getMonthlySummary(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal income = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, Transaction.TransactionType.INCOME, start, end);
        BigDecimal expenses = transactionRepository.sumByUserAndTypeAndDateBetween(
                user, Transaction.TransactionType.EXPENSE, start, end);

        List<Object[]> categoryData = transactionRepository.sumExpensesByCategoryAndDateBetween(user, start, end);
        Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
        for (Object[] row : categoryData) {
            expensesByCategory.put(row[0].toString(), (BigDecimal) row[1]);
        }

        TransactionDto.Summary summary = new TransactionDto.Summary();
        summary.setTotalIncome(income);
        summary.setTotalExpenses(expenses);
        summary.setNetBalance(income.subtract(expenses));
        summary.setExpensesByCategory(expensesByCategory);
        return summary;
    }

    public Map<String, BigDecimal> getMonthlyTrend(User user, Transaction.TransactionType type) {
        List<Object[]> rows = transactionRepository.sumByMonthAndType(user, type);
        Map<String, BigDecimal> trend = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = row[1] + "-" + String.format("%02d", ((Number) row[0]).intValue());
            trend.put(key, (BigDecimal) row[2]);
        }
        return trend;
    }
}
