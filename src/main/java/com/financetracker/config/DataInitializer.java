package com.financetracker.config;

import com.financetracker.model.*;
import com.financetracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private TransactionRepository txRepo;
    @Autowired private BudgetRepository budgetRepo;
    @Autowired private SavingsGoalRepository goalRepo;
    @Autowired private ForumPostRepository postRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        // Admin user
        User admin = userRepo.save(User.builder()
                .name("Admin User").email("admin@finance.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .monthlyIncome(new BigDecimal("8000"))
                .targetExpenses(new BigDecimal("5000"))
                .savingsTarget(new BigDecimal("2000"))
                .currency("USD").build());

        // Demo user
        User demo = userRepo.save(User.builder()
                .name("Alex Johnson").email("demo@finance.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.USER)
                .monthlyIncome(new BigDecimal("5500"))
                .targetExpenses(new BigDecimal("3500"))
                .savingsTarget(new BigDecimal("1000"))
                .currency("USD").build());

        // Transactions for demo user - last 3 months
        LocalDate today = LocalDate.now();
        Object[][] txData = {
            {Transaction.TransactionType.INCOME,  new BigDecimal("5500"), Transaction.Category.SALARY,        "Monthly salary",         today.withDayOfMonth(1)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("1200"), Transaction.Category.RENT,          "Apartment rent",         today.withDayOfMonth(2)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("340"),  Transaction.Category.FOOD,          "Groceries + dining",     today.withDayOfMonth(5)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("85"),   Transaction.Category.TRANSPORT,     "Monthly commute",        today.withDayOfMonth(6)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("60"),   Transaction.Category.ENTERTAINMENT, "Netflix + Spotify",      today.withDayOfMonth(8)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("220"),  Transaction.Category.SHOPPING,      "Clothing",               today.withDayOfMonth(10)},
            {Transaction.TransactionType.INCOME,  new BigDecimal("800"),  Transaction.Category.FREELANCE,     "Freelance project",      today.withDayOfMonth(12)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("130"),  Transaction.Category.UTILITIES,     "Electric + Internet",    today.withDayOfMonth(14)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("75"),   Transaction.Category.HEALTHCARE,    "Pharmacy",               today.withDayOfMonth(16)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("500"),  Transaction.Category.TRAVEL,        "Weekend trip",           today.withDayOfMonth(18)},
            {Transaction.TransactionType.INCOME,  new BigDecimal("5500"), Transaction.Category.SALARY,        "Monthly salary",         today.minusMonths(1).withDayOfMonth(1)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("1200"), Transaction.Category.RENT,          "Apartment rent",         today.minusMonths(1).withDayOfMonth(2)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("290"),  Transaction.Category.FOOD,          "Groceries",              today.minusMonths(1).withDayOfMonth(7)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("85"),   Transaction.Category.TRANSPORT,     "Transport",              today.minusMonths(1).withDayOfMonth(8)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("150"),  Transaction.Category.EDUCATION,     "Online course",          today.minusMonths(1).withDayOfMonth(10)},
            {Transaction.TransactionType.INCOME,  new BigDecimal("5500"), Transaction.Category.SALARY,        "Monthly salary",         today.minusMonths(2).withDayOfMonth(1)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("1200"), Transaction.Category.RENT,          "Apartment rent",         today.minusMonths(2).withDayOfMonth(2)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("410"),  Transaction.Category.FOOD,          "Groceries + restaurants",today.minusMonths(2).withDayOfMonth(9)},
            {Transaction.TransactionType.EXPENSE, new BigDecimal("300"),  Transaction.Category.SHOPPING,      "Electronics",            today.minusMonths(2).withDayOfMonth(15)},
        };

        for (Object[] row : txData) {
            txRepo.save(Transaction.builder()
                    .user(demo)
                    .type((Transaction.TransactionType) row[0])
                    .amount((BigDecimal) row[1])
                    .category((Transaction.Category) row[2])
                    .description((String) row[3])
                    .date((LocalDate) row[4])
                    .account("Checking")
                    .build());
        }

        // Budgets
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        Object[][] budgets = {
            {Transaction.Category.FOOD,          new BigDecimal("500")},
            {Transaction.Category.TRANSPORT,     new BigDecimal("150")},
            {Transaction.Category.ENTERTAINMENT, new BigDecimal("100")},
            {Transaction.Category.SHOPPING,      new BigDecimal("300")},
        };
        for (Object[] b : budgets) {
            budgetRepo.save(Budget.builder()
                    .user(demo).category((Transaction.Category) b[0])
                    .amount((BigDecimal) b[1])
                    .startDate(monthStart).endDate(monthEnd).build());
        }

        // Savings goals
        goalRepo.save(SavingsGoal.builder().user(demo)
                .goalName("Vacation Fund").targetAmount(new BigDecimal("3000"))
                .currentAmount(new BigDecimal("500")).deadline(today.plusMonths(6))
                .description("Summer vacation in Europe").build());
        goalRepo.save(SavingsGoal.builder().user(demo)
                .goalName("Emergency Fund").targetAmount(new BigDecimal("10000"))
                .currentAmount(new BigDecimal("3200")).deadline(today.plusMonths(12))
                .description("6 months of expenses").build());
        goalRepo.save(SavingsGoal.builder().user(demo)
                .goalName("New Laptop").targetAmount(new BigDecimal("1500"))
                .currentAmount(new BigDecimal("900")).deadline(today.plusMonths(2))
                .description("MacBook Pro for work").build());

        // Forum posts
        User[] authors = {admin, demo};
        Object[][] posts = {
            {demo,  "Just hit a new savings milestone!", "Anyone else struggling with impulse spending? I've been using the 30-day rule and it really helps!"},
            {admin, "Tips for tracking monthly expenses", "I recommend categorizing everything – even small purchases add up. Visibility is key to behavioral change."},
            {demo,  "Best budgeting strategy for freelancers?", "As a freelancer with irregular income, I find percentage-based budgeting much more flexible than fixed amounts."},
        };
        for (Object[] p : posts) {
            postRepo.save(ForumPost.builder()
                    .user((User) p[0]).title((String) p[1]).content((String) p[2])
                    .likes((int)(Math.random() * 20)).build());
        }

        System.out.println("✅ Demo data initialized");
        System.out.println("   👤 Demo login → demo@finance.com / demo123");
        System.out.println("   🔑 Admin login → admin@finance.com / admin123");
    }
}
