package com.financetracker.view;

import com.financetracker.model.User;
import com.financetracker.model.Transaction;
import com.financetracker.model.Category;
import com.financetracker.service.TransactionService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PiePlot;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.LocalDate;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;

/**
 * 概览面板 - 显示支出分类和饼图
 */
public class OverviewPanel extends JPanel implements PropertyChangeListener {

    private User currentUser;
    private TransactionService transactionService;
    private JLabel monthLabel;
    private JPanel categoriesPanel;
    private ChartPanel chartPanel;
    private DefaultPieDataset<String> pieDataset;
    private JDialog editDialog;

    public OverviewPanel(User user, TransactionService transactionService) {
        this.currentUser = user;
        this.transactionService = transactionService;
        this.transactionService.addPropertyChangeListener(this);
        setupUI();
        refreshOverviewData();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // 顶部月份选择面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        // 月份选择下拉框和标签
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        datePanel.setBackground(Color.WHITE);
        monthLabel = new JLabel(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        monthLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 添加下拉箭头图标
        JPanel dateSelectorPanel = new JPanel(new BorderLayout(5, 0));
        dateSelectorPanel.setBackground(Color.WHITE);
        dateSelectorPanel.add(monthLabel, BorderLayout.CENTER);
        JLabel downArrowLabel = new JLabel("▼");
        downArrowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        downArrowLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateSelectorPanel.add(downArrowLabel, BorderLayout.EAST);

        // 为日期标签和箭头添加月份选择功能
        MouseAdapter monthSelectorMouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMonthYearChooser();
            }
        };
        monthLabel.addMouseListener(monthSelectorMouseAdapter);
        downArrowLabel.addMouseListener(monthSelectorMouseAdapter);

        datePanel.add(dateSelectorPanel);
        topPanel.add(datePanel, BorderLayout.CENTER);

        // 中心内容面板
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(Color.WHITE);

        // 左侧面板 - 饼图
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pieDataset = new DefaultPieDataset<>();
        JFreeChart chart = createPieChart(pieDataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 400));
        leftPanel.add(chartPanel, BorderLayout.CENTER);

        // 右侧面板 - 分类明细
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // 分类明细面板
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(Color.WHITE);

        JScrollPane categoriesScrollPane = new JScrollPane(categoriesPanel);
        categoriesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        categoriesScrollPane.getViewport().setBackground(Color.WHITE);

        rightPanel.add(categoriesScrollPane, BorderLayout.CENTER);

        // 添加左右面板到中心面板
        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        // 添加所有面板到主面板
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void refreshOverviewData() {
        if (transactionService == null || pieDataset == null || categoriesPanel == null) {
            return;
        }

        YearMonth monthToDisplay;
        // 优先使用dateLabel中用户选择或程序设置的月份
        try {
            if (monthLabel != null && monthLabel.getText() != null && !monthLabel.getText().isEmpty()) {
                monthToDisplay = YearMonth.parse(monthLabel.getText(), DateTimeFormatter.ofPattern("yyyy/MM"));
            } else {
                List<Transaction> allTransactionsForMonthCheck = transactionService.getTransactions();
                if (allTransactionsForMonthCheck.isEmpty()) {
                    monthToDisplay = YearMonth.now();
                } else {
                    LocalDate latestTransactionDate = allTransactionsForMonthCheck.stream()
                            .map(Transaction::getDate)
                            .max(LocalDate::compareTo)
                            .orElse(LocalDate.now());
                    monthToDisplay = YearMonth.from(latestTransactionDate);
                }
                if (monthLabel != null) { // Ensure monthLabel is updated if it was initially empty/null
                    monthLabel.setText(monthToDisplay.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "OverviewPanel: Error parsing month from monthLabel, attempting fallback: " + e.getMessage());
            List<Transaction> allTransactionsForFallback = transactionService.getTransactions();
            if (allTransactionsForFallback.isEmpty()) {
                monthToDisplay = YearMonth.now();
            } else {
                LocalDate latestTransactionDate = allTransactionsForFallback.stream()
                        .map(Transaction::getDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                monthToDisplay = YearMonth.from(latestTransactionDate);
            }
            if (monthLabel != null) { // Ensure monthLabel is updated after fallback
                monthLabel.setText(monthToDisplay.format(DateTimeFormatter.ofPattern("yyyy/MM")));
            }
        }

        final YearMonth effectiveMonth = monthToDisplay;
        List<Transaction> transactions = transactionService.getTransactions();

        Map<Category, Double> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE &&
                        YearMonth.from(t.getDate()).equals(effectiveMonth) &&
                        t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> Math.abs(t.getAmount()))));

        pieDataset.clear();
        expensesByCategory.forEach((category, total) -> {
            pieDataset.setValue(category.getName(), total);
        });

        if (chartPanel != null && chartPanel.getChart() != null) {
            chartPanel.getChart().getTitle()
                    .setText(effectiveMonth.format(DateTimeFormatter.ofPattern("yyyy/MM")) + " Expenses");
        }

        categoriesPanel.removeAll();
        expensesByCategory.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    Category category = entry.getKey();
                    Double total = entry.getValue();
                    categoriesPanel.add(createCategorySummaryPanel(category, total));
                    categoriesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                });

        categoriesPanel.revalidate();
        categoriesPanel.repaint();
        // It's generally better to revalidate/repaint the specific container that
        // changed,
        // but revalidating the whole panel can be a fallback if needed.
        // this.revalidate();
        // this.repaint();
    }

    private JPanel createCategorySummaryPanel(Category category, double totalAmount) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel nameLabel = new JLabel(category.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel amountLabel = new JLabel(String.format("- ¥%.2f", totalAmount));
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setForeground(Color.RED);

        panel.add(nameLabel, BorderLayout.CENTER);
        panel.add(amountLabel, BorderLayout.EAST);
        return panel;
    }

    private JFreeChart createPieChart(DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
                YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy/MM")) + " Expenses",
                dataset,
                true, true, false);

        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 20));
        chart.getTitle().setPaint(Color.BLACK);
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 220));
        plot.setSectionOutlinesVisible(true);
        plot.setExplodePercent("Shopping", 0.10);

        return chart;
    }

    private void showEditCategoryDialog(String categoryName) {
        if (editDialog != null && editDialog.isVisible()) {
            editDialog.dispose();
        }

        editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit " + categoryName, true);
        editDialog.setSize(400, 200);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(new BorderLayout());
        editDialog.getContentPane().setBackground(Color.WHITE);

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Edit name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField nameField = new JTextField(categoryName);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel detailsLabel = new JLabel("Add details:");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField detailsField = new JTextField();
        detailsField.setFont(new Font("Arial", Font.PLAIN, 14));

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(detailsLabel);
        formPanel.add(detailsField);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setFocusPainted(false);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(0, 102, 102));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        // 添加到对话框
        editDialog.add(formPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 添加按钮事件
        cancelButton.addActionListener(e -> editDialog.dispose());

        submitButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(editDialog,
                        "Name cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 在这里添加更新分类的逻辑
            JOptionPane.showMessageDialog(editDialog,
                    "Category updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            editDialog.dispose();
        });

        editDialog.setVisible(true);
    }

    private void showMonthYearChooser() {
        JDialog monthYearDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Select Month and Year",
                Dialog.ModalityType.APPLICATION_MODAL);
        monthYearDialog.setLayout(new BorderLayout(10, 10));
        monthYearDialog.setSize(300, 150);
        monthYearDialog.setLocationRelativeTo(this);

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        YearMonth currentChoice = YearMonth.now();
        try {
            if (monthLabel != null && monthLabel.getText() != null && !monthLabel.getText().isEmpty()) {
                currentChoice = YearMonth.parse(monthLabel.getText(), DateTimeFormatter.ofPattern("yyyy/MM"));
            }
        } catch (Exception ex) {
            // Keep currentChoice as YearMonth.now() if parsing fails
        }

        SpinnerModel yearModel = new SpinnerNumberModel(currentChoice.getYear(), 1900, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));

        String[] monthNames = new DateFormatSymbols().getMonths();
        int monthCount = 12;
        String[] displayMonthNames = new String[monthCount];
        System.arraycopy(monthNames, 0, displayMonthNames, 0, monthCount);
        JComboBox<String> monthComboBox = new JComboBox<>(displayMonthNames);
        monthComboBox.setSelectedIndex(currentChoice.getMonthValue() - 1);

        selectionPanel.add(new JLabel("Month:"));
        selectionPanel.add(monthComboBox);
        selectionPanel.add(new JLabel("Year:"));
        selectionPanel.add(yearSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            int year = (Integer) yearSpinner.getValue();
            int month = monthComboBox.getSelectedIndex() + 1;
            YearMonth selectedYearMonth = YearMonth.of(year, month);
            if (monthLabel != null) {
                monthLabel.setText(selectedYearMonth.format(DateTimeFormatter.ofPattern("yyyy/MM")));
            }
            refreshOverviewData();
            monthYearDialog.dispose();
        });

        cancelButton.addActionListener(e -> monthYearDialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        monthYearDialog.add(selectionPanel, BorderLayout.CENTER);
        monthYearDialog.add(buttonPanel, BorderLayout.SOUTH);
        monthYearDialog.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("transactionsChanged".equals(evt.getPropertyName())) {
            if (transactionService == null)
                return;
            List<Transaction> allTransactions = transactionService.getTransactions();
            YearMonth monthToSet;
            if (allTransactions.isEmpty()) {
                monthToSet = YearMonth.now();
            } else {
                LocalDate latestTransactionDate = allTransactions.stream()
                        .map(Transaction::getDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                monthToSet = YearMonth.from(latestTransactionDate);
            }

            SwingUtilities.invokeLater(() -> {
                if (monthLabel != null) {
                    monthLabel.setText(monthToSet.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                }
                refreshOverviewData();
            });

        } else if ("userSettingsChanged".equals(evt.getPropertyName())) {
            SwingUtilities.invokeLater(this::refreshOverviewData);
        }
    }
}
