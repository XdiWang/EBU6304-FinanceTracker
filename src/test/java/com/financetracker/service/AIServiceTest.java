package com.financetracker.service;

import com.financetracker.model.Account;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AIServiceTest {

    private AIService aiService;
    private List<Transaction> sampleTransactions;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        aiService = new AIService();
        sampleTransactions = new ArrayList<>();
        testAccount = new Account("Test", Account.AccountType.BANK);

        // Sample transactions for testing advice generation
        sampleTransactions.add(new Transaction("T1", LocalDate.now().minusDays(5), -100.0, "聚餐吃饭", Category.FOOD, Transaction.TransactionType.EXPENSE, testAccount));
        sampleTransactions.add(new Transaction("T2", LocalDate.now().minusDays(3), -50.0, "地铁票", Category.TRANSPORT, Transaction.TransactionType.EXPENSE, testAccount));
        sampleTransactions.add(new Transaction("T3", LocalDate.now().minusDays(1), 2000.0, "月薪", Category.SALARY, Transaction.TransactionType.INCOME, testAccount));
        sampleTransactions.add(new Transaction("T4", LocalDate.now().minusDays(2), -200.0, "买衣服", Category.SHOPPING, Transaction.TransactionType.EXPENSE, testAccount));
    }

    @Test
    @DisplayName("classifyTransaction should return FOOD for food related keywords")
    void classifyTransaction_foodKeywords_returnsFood() {
        assertEquals(Category.FOOD, aiService.classifyTransaction("和朋友餐厅吃饭"));
        assertEquals(Category.FOOD, aiService.classifyTransaction("买了点外卖"));
        assertEquals(Category.FOOD, aiService.classifyTransaction("超市买菜"));
    }

    @Test
    @DisplayName("classifyTransaction should return a default category for unknown description")
    void classifyTransaction_unknownDescription_returnsDefault() {
        Category result = aiService.classifyTransaction("一些随机的描述词语");
        assertNotNull(result);
        // Check if it's one of the fallback categories
        assertTrue(List.of(Category.FOOD, Category.TRANSPORT, Category.SHOPPING, Category.ENTERTAINMENT, Category.UTILITIES).contains(result));
    }

    @Test
    @DisplayName("generateSavingSuggestions should return a list of predefined suggestions")
    void generateSavingSuggestions_returnsPredefinedList() {
        List<String> suggestions = aiService.generateSavingSuggestions(sampleTransactions);
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.contains("尝试每周制定餐饮预算，避免冲动消费"));
    }

    @Test
    @DisplayName("predictMonthlyExpenses should return a map of predicted expenses")
    void predictMonthlyExpenses_returnsPredictionMap() {
        Map<Category, Double> predictions = aiService.predictMonthlyExpenses(sampleTransactions);
        assertNotNull(predictions);
        assertTrue(predictions.containsKey(Category.FOOD));
        assertEquals(1200.0, predictions.get(Category.FOOD));
    }

    @Test
    @DisplayName("recommendBudgetAllocation should return budget based on income percentages")
    void recommendBudgetAllocation_returnsBudgetMap() {
        double monthlyIncome = 5000.0;
        Map<Category, Double> budget = aiService.recommendBudgetAllocation(monthlyIncome, sampleTransactions);
        assertNotNull(budget);
        assertTrue(budget.containsKey(Category.RENT));
        assertEquals(monthlyIncome * 0.30, budget.get(Category.RENT)); // 30% for Rent
    }

    @Test
    @DisplayName("generateFestivalBudgetAdvice should return specific advice for known festivals")
    void generateFestivalBudgetAdvice_knownFestivals_returnsSpecificAdvice() {
        String springAdvice = aiService.generateFestivalBudgetAdvice("春节");
        assertTrue(springAdvice.contains("红包准备专门预算"));

        String midAutumnAdvice = aiService.generateFestivalBudgetAdvice("中秋节");
        assertTrue(midAutumnAdvice.contains("月饼礼盒购买"));
    }

    @Test
    @DisplayName("generateFestivalBudgetAdvice should return generic advice for unknown festivals")
    void generateFestivalBudgetAdvice_unknownFestival_returnsGenericAdvice() {
        String unknownAdvice = aiService.generateFestivalBudgetAdvice("圣诞节");
        assertTrue(unknownAdvice.contains("建议提前规划预算"));
    }

    @Test
    @DisplayName("getPersonalizedChatAdvice with no transactions and '建议' input")
    void getPersonalizedChatAdvice_noTransactions_suggestion() {
        String advice = aiService.getPersonalizedChatAdvice("给我一些省钱建议", new ArrayList<>());
        assertTrue(advice.contains("您目前没有记录任何交易"));
        assertTrue(advice.contains("制定每月预算计划"));
    }

    @Test
    @DisplayName("getPersonalizedChatAdvice for '投资' keyword")
    void getPersonalizedChatAdvice_investmentKeyword() {
        String advice = aiService.getPersonalizedChatAdvice("关于投资有什么建议吗？", sampleTransactions);
        assertTrue(advice.contains("投资前请确保您已有足够的应急资金"));
    }

    @Test
    @DisplayName("getPersonalizedChatAdvice for default response")
    void getPersonalizedChatAdvice_defaultResponse() {
        String advice = aiService.getPersonalizedChatAdvice("你好", sampleTransactions);
        assertTrue(advice.contains("您可以问我关于预算规划"));
    }
}
