package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    List<Transaction> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate start, LocalDate end);

    List<Transaction> findByUserAndTypeOrderByDateDesc(User user, Transaction.TransactionType type);

    List<Transaction> findByUserAndCategoryOrderByDateDesc(User user, Transaction.Category category);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") Transaction.TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE' AND t.date BETWEEN :start AND :end GROUP BY t.category")
    List<Object[]> sumExpensesByCategoryAndDateBetween(
            @Param("user") User user,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT FUNCTION('MONTH', t.date) as month, FUNCTION('YEAR', t.date) as year, SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.type = :type GROUP BY FUNCTION('YEAR', t.date), FUNCTION('MONTH', t.date) ORDER BY year DESC, month DESC")
    List<Object[]> sumByMonthAndType(
            @Param("user") User user,
            @Param("type") Transaction.TransactionType type);
}
