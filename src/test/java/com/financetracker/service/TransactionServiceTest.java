package com.financetracker.service;

import com.financetracker.model.Account;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService transactionService;
    private Transaction sampleTransaction1;
    private Transaction sampleTransaction2;
    private Category testCategory;
    private Account testAccount;


    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        testCategory = new Category("Groceries");
        testAccount = new Account("Test Account", Account.AccountType.BANK);
        sampleTransaction1 = new Transaction("T1", LocalDate.now(), -50.0, "Milk", testCategory, Transaction.TransactionType.EXPENSE, testAccount);
        sampleTransaction2 = new Transaction("T2", LocalDate.now().minusDays(1), 100.0, "Salary", testCategory, Transaction.TransactionType.INCOME, testAccount);
    }

    @Test
    @DisplayName("Constructor should initialize an empty list of transactions")
    void constructor_initializesEmptyList() {
        assertTrue(transactionService.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("addTransaction should add a transaction to the list")
    void addTransaction_addsTransaction() {
        transactionService.addTransaction(sampleTransaction1);
        assertEquals(1, transactionService.getTransactions().size());
        assertTrue(transactionService.getTransactions().contains(sampleTransaction1));
    }

    @Test
    @DisplayName("addTransaction with null should not add to the list")
    void addTransaction_nullTransaction_notAdded() {
        transactionService.addTransaction(null);
        assertTrue(transactionService.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("removeTransaction should remove an existing transaction")
    void removeTransaction_removesExistingTransaction() {
        transactionService.addTransaction(sampleTransaction1);
        transactionService.addTransaction(sampleTransaction2);
        transactionService.removeTransaction(sampleTransaction1);

        assertEquals(1, transactionService.getTransactions().size());
        assertFalse(transactionService.getTransactions().contains(sampleTransaction1));
        assertTrue(transactionService.getTransactions().contains(sampleTransaction2));
    }

    @Test
    @DisplayName("removeTransaction non-existent transaction should not change list")
    void removeTransaction_nonExistentTransaction_listUnchanged() {
        transactionService.addTransaction(sampleTransaction1);
        Transaction nonExistent = new Transaction("T99", LocalDate.now(), 10.0, "Ghost", testCategory, Transaction.TransactionType.INCOME, testAccount);
        transactionService.removeTransaction(nonExistent);

        assertEquals(1, transactionService.getTransactions().size());
        assertTrue(transactionService.getTransactions().contains(sampleTransaction1));
    }

    @Test
    @DisplayName("removeTransaction with null should not change list")
    void removeTransaction_nullTransaction_listUnchanged() {
        transactionService.addTransaction(sampleTransaction1);
        transactionService.removeTransaction(null);
        assertEquals(1, transactionService.getTransactions().size());
    }

    @Test
    @DisplayName("updateTransaction should replace old with new transaction")
    void updateTransaction_replacesOldWithNew() {
        transactionService.addTransaction(sampleTransaction1);
        Transaction updatedTransaction1 = new Transaction("T1_updated", sampleTransaction1.getDate(), -60.0, "Organic Milk", testCategory, Transaction.TransactionType.EXPENSE, testAccount);

        // Note: The Transaction model currently uses ID for its constructor, not for equals/hashCode.
        // The updateTransaction in TransactionService relies on indexOf, which uses equals().
        // If Transaction.equals() is not properly defined (e.g., based on ID), this test might behave unexpectedly.
        // Assuming default Object.equals() or a proper ID-based equals for Transaction.
        // For this test to work as intended with list.indexOf(oldTransaction), Transaction's equals method needs to be robust.
        // If Transaction.equals is based on object identity, we must pass the exact same instance.

        transactionService.updateTransaction(sampleTransaction1, updatedTransaction1);

        assertEquals(1, transactionService.getTransactions().size());
        assertFalse(transactionService.getTransactions().contains(sampleTransaction1), "Old transaction should be removed.");
        assertTrue(transactionService.getTransactions().contains(updatedTransaction1), "New transaction should be present.");
        assertEquals(-60.0, transactionService.getTransactions().get(0).getAmount());
    }

    @Test
    @DisplayName("updateTransaction non-existent old transaction should not change list")
    void updateTransaction_nonExistentOldTransaction_listUnchanged() {
        transactionService.addTransaction(sampleTransaction2);
        Transaction nonExistentOld = new Transaction("T99", LocalDate.now(), 10.0, "Ghost", testCategory, Transaction.TransactionType.INCOME, testAccount);
        Transaction newTx = new Transaction("T100", LocalDate.now(), 20.0, "Found", testCategory, Transaction.TransactionType.INCOME, testAccount);
        transactionService.updateTransaction(nonExistentOld, newTx);

        assertEquals(1, transactionService.getTransactions().size());
        assertTrue(transactionService.getTransactions().contains(sampleTransaction2));
        assertFalse(transactionService.getTransactions().contains(newTx));
    }

    @Test
    @DisplayName("getTransactions should return a copy of the list")
    void getTransactions_returnsCopy() {
        transactionService.addTransaction(sampleTransaction1);
        List<Transaction> retrievedList = transactionService.getTransactions();
        retrievedList.add(sampleTransaction2); // Modify the copy

        assertEquals(1, transactionService.getTransactions().size(), "Internal list size should not change.");
        assertFalse(transactionService.getTransactions().contains(sampleTransaction2), "Internal list should not contain element added to copy.");
    }

    @Test
    @DisplayName("PropertyChange event for 'transactions' should be fired on add")
    void propertyChange_transactionsFiredOnAdd() {
        AtomicBoolean eventFired = new AtomicBoolean(false);
        AtomicReference<List<Transaction>> oldListRef = new AtomicReference<>();
        AtomicReference<List<Transaction>> newListRef = new AtomicReference<>();

        PropertyChangeListener listener = evt -> {
            if ("transactions".equals(evt.getPropertyName())) {
                eventFired.set(true);
                oldListRef.set((List<Transaction>) evt.getOldValue());
                newListRef.set((List<Transaction>) evt.getNewValue());
            }
        };
        transactionService.addPropertyChangeListener(listener);
        transactionService.addTransaction(sampleTransaction1);

        assertTrue(eventFired.get());
        assertTrue(oldListRef.get().isEmpty());
        assertEquals(1, newListRef.get().size());
        assertTrue(newListRef.get().contains(sampleTransaction1));

        transactionService.removePropertyChangeListener(listener);
    }

    @Test
    @DisplayName("PropertyChange event for 'transactions' should be fired on remove")
    void propertyChange_transactionsFiredOnRemove() {
        transactionService.addTransaction(sampleTransaction1); // Add one first

        AtomicBoolean eventFired = new AtomicBoolean(false);
        AtomicReference<List<Transaction>> oldListRef = new AtomicReference<>();
        AtomicReference<List<Transaction>> newListRef = new AtomicReference<>();
        PropertyChangeListener listener = evt -> {
            if ("transactions".equals(evt.getPropertyName())) {
                eventFired.set(true);
                oldListRef.set((List<Transaction>) evt.getOldValue());
                newListRef.set((List<Transaction>) evt.getNewValue());
            }
        };
        transactionService.addPropertyChangeListener(listener);
        transactionService.removeTransaction(sampleTransaction1);

        assertTrue(eventFired.get());
        assertEquals(1, oldListRef.get().size());
        assertTrue(oldListRef.get().contains(sampleTransaction1));
        assertTrue(newListRef.get().isEmpty());

        transactionService.removePropertyChangeListener(listener);
    }
}
