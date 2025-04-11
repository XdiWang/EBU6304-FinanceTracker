package com.financetracker.view;

import com.financetracker.model.User;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 仪表板面板 - 显示用户的月收入和支出
 */
public class DashboardPanel extends JPanel {

    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel dateLabel;
    private JLabel incomeLabel;
    private JLabel expensesLabel;
    private JPanel recentTransactionsPanel;
    private JPanel limitPanel;
    private JLabel dailyLimitLabel;
    private JLabel monthlyLimitLabel;
    private ChartPanel chartPanel;

    public DashboardPanel(User user) {
        this.currentUser = user;
        setupUI();
        updateUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // 顶部欢迎面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        welcomeLabel = new JLabel("Hi, " + (currentUser != null ? currentUser.getUsername() : "User") + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // 月份选择下拉框和标签
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        dateLabel = new JLabel(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // 添加下拉箭头图标
        JPanel dateSelectorPanel = new JPanel(new BorderLayout(5, 0));
        dateSelectorPanel.setBackground(Color.WHITE);
        dateSelectorPanel.add(dateLabel, BorderLayout.CENTER);
        JLabel downArrowLabel = new JLabel("▼");
        downArrowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateSelectorPanel.add(downArrowLabel, BorderLayout.EAST);

        datePanel.add(dateSelectorPanel);

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(datePanel, BorderLayout.EAST);

        // 收入和支出面板
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setBackground(Color.WHITE);

        // 收入面板
        JPanel incomePanel = new JPanel(new BorderLayout());
        incomePanel.setBackground(Color.WHITE);
        JLabel incomeTitleLabel = new JLabel("Income", JLabel.LEFT);
        incomeTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        incomeLabel = new JLabel("+RMB 20000.00", JLabel.RIGHT);
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        incomeLabel.setForeground(new Color(0, 128, 0));
        incomePanel.add(incomeTitleLabel, BorderLayout.WEST);
        incomePanel.add(incomeLabel, BorderLayout.EAST);

        // 支出面板
        JPanel expensesPanel = new JPanel(new BorderLayout());
        expensesPanel.setBackground(Color.WHITE);
        JLabel expensesTitleLabel = new JLabel("Expenses", JLabel.LEFT);
        expensesTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        expensesLabel = new JLabel("-RMB 8023.67", JLabel.RIGHT);
        expensesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        expensesLabel.setForeground(new Color(220, 20, 60));
        expensesPanel.add(expensesTitleLabel, BorderLayout.WEST);
        expensesPanel.add(expensesLabel, BorderLayout.EAST);

        summaryPanel.add(incomePanel);
        summaryPanel.add(expensesPanel);

        // 中央内容面板
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(Color.WHITE);

        // 创建趋势图表
        JFreeChart chart = createTrendChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // 右侧面板 - 包含交易记录和预算限制
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // 最近交易记录面板
        recentTransactionsPanel = new JPanel();
        recentTransactionsPanel.setLayout(new BoxLayout(recentTransactionsPanel, BoxLayout.Y_AXIS));
        recentTransactionsPanel.setBackground(Color.WHITE);
        recentTransactionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                "Recent Transactions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        // 添加一些模拟交易
        addMockTransactions();

        // 预算限制面板
        limitPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        limitPanel.setBackground(Color.WHITE);
        limitPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                "Budget Limits",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)));

        // 日限额
        JPanel dailyPanel = new JPanel(new BorderLayout(10, 0));
        dailyPanel.setBackground(Color.WHITE);
        JLabel dailyTitleLabel = new JLabel("Daily Limits");
        dailyTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dailyLimitLabel = new JLabel("RMB 30.00 / 100.00");
        dailyLimitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton editDailyButton = new JButton("✎");
        editDailyButton.setFont(new Font("Arial", Font.PLAIN, 12));
        editDailyButton.setFocusPainted(false);
        editDailyButton.setPreferredSize(new Dimension(20, 20));
        dailyPanel.add(dailyTitleLabel, BorderLayout.WEST);
        dailyPanel.add(dailyLimitLabel, BorderLayout.CENTER);
        dailyPanel.add(editDailyButton, BorderLayout.EAST);

        // 月限额
        JPanel monthlyPanel = new JPanel(new BorderLayout(10, 0));
        monthlyPanel.setBackground(Color.WHITE);
        JLabel monthlyTitleLabel = new JLabel("Monthly Limits");
        monthlyTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        monthlyLimitLabel = new JLabel("RMB 8023.67 / 10000.00");
        monthlyLimitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton editMonthlyButton = new JButton("✎");
        editMonthlyButton.setFont(new Font("Arial", Font.PLAIN, 12));
        editMonthlyButton.setFocusPainted(false);
        editMonthlyButton.setPreferredSize(new Dimension(20, 20));
        monthlyPanel.add(monthlyTitleLabel, BorderLayout.WEST);
        monthlyPanel.add(monthlyLimitLabel, BorderLayout.CENTER);
        monthlyPanel.add(editMonthlyButton, BorderLayout.EAST);

        // AI建议面板
        JPanel aiAdvicePanel = new JPanel(new BorderLayout(10, 0));
        aiAdvicePanel.setBackground(Color.WHITE);
        JPanel aiIconPanel = new JPanel();
        aiIconPanel.setBackground(Color.WHITE);
        aiIconPanel.setPreferredSize(new Dimension(30, 30));
        // 圆形图标
        JLabel aiIconLabel = new JLabel("⚙");
        aiIconLabel.setFont(new Font("Arial", Font.BOLD, 18));
        aiIconPanel.add(aiIconLabel);

        JTextArea aiAdviceText = new JTextArea(
                "This month has seen a slight overspending, please be mindful of your expenses.");
        aiAdviceText.setEditable(false);
        aiAdviceText.setLineWrap(true);
        aiAdviceText.setWrapStyleWord(true);
        aiAdviceText.setBackground(new Color(250, 250, 250));
        aiAdviceText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        aiAdviceText.setFont(new Font("Arial", Font.PLAIN, 12));

        aiAdvicePanel.add(aiIconPanel, BorderLayout.WEST);
        aiAdvicePanel.add(aiAdviceText, BorderLayout.CENTER);

        limitPanel.add(dailyPanel);
        limitPanel.add(monthlyPanel);
        limitPanel.add(aiAdvicePanel);

        // 将交易记录和预算面板添加到右侧面板
        rightPanel.add(recentTransactionsPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(limitPanel);

        // 添加图表和右侧面板到中央面板
        JPanel chartAndRightPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartAndRightPanel.setBackground(Color.WHITE);
        chartAndRightPanel.add(chartPanel);
        chartAndRightPanel.add(rightPanel);
        centerPanel.add(chartAndRightPanel, BorderLayout.CENTER);

        // 添加所有面板到仪表板
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JFreeChart createTrendChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 模拟数据：最近一周的收入和支出
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        double[] incomeData = { 430, 450, 420, 370, 500, 280, 270 };
        double[] outcomeData = { 100, 80, 90, 150, 120, 170, 350 };

        for (int i = 0; i < days.length; i++) {
            dataset.addValue(incomeData[i], "Income", days[i]);
            dataset.addValue(outcomeData[i], "Outcome", days[i]);
        }

        // 创建折线图
        JFreeChart chart = ChartFactory.createLineChart(
                "", // 图表标题
                "Date", // x轴标签
                "Amount (¥)", // y轴标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true, // 是否包含图例
                true, // 是否生成工具提示
                false // 是否生成URL链接
        );

        // 自定义图表样式 - 改进以匹配上传的图片样式
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // 设置图例
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);

        // 自定义线条样式
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // 收入线条 - 深绿色系
        renderer.setSeriesPaint(0, new Color(0, 86, 91));
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);

        // 支出线条 - 浅绿色系
        renderer.setSeriesPaint(1, new Color(105, 190, 171));
        renderer.setSeriesStroke(1, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(1, true);

        // 轴线设置
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    private void addMockTransactions() {
        // 模拟交易数据 - 使用图标和更现代的布局
        addTransactionItem("Clothing", "-RMB 600.00", new Color(255, 99, 71));
        addTransactionItem("Railway", "-RMB 1200.00", new Color(106, 90, 205));
        addTransactionItem("Salary", "+RMB 20000.00", new Color(50, 205, 50));
        addTransactionItem("Apple", "-RMB 4000.00", new Color(255, 165, 0));
        addTransactionItem("Transfer", "-RMB 1000.00", new Color(30, 144, 255));
    }

    private void addTransactionItem(String name, String amount, Color iconColor) {
        JPanel transactionPanel = new JPanel(new BorderLayout(10, 0));
        transactionPanel.setBackground(Color.WHITE);
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

        // 创建圆形图标
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(iconColor);
                g.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        iconPanel.setPreferredSize(new Dimension(24, 24));
        iconPanel.setOpaque(false);

        // 交易名称
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // 交易金额
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        if (amount.startsWith("+")) {
            amountLabel.setForeground(new Color(0, 128, 0));
        } else {
            amountLabel.setForeground(new Color(220, 20, 60));
        }

        // 添加到交易面板
        transactionPanel.add(iconPanel, BorderLayout.WEST);
        transactionPanel.add(nameLabel, BorderLayout.CENTER);
        transactionPanel.add(amountLabel, BorderLayout.EAST);

        // 添加分隔线
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(230, 230, 230));

        // 创建包含交易项和分隔线的面板
        JPanel itemWithSeparator = new JPanel(new BorderLayout());
        itemWithSeparator.setBackground(Color.WHITE);
        itemWithSeparator.add(transactionPanel, BorderLayout.CENTER);
        itemWithSeparator.add(separator, BorderLayout.SOUTH);

        recentTransactionsPanel.add(itemWithSeparator);
    }

    /**
     * 更新UI显示
     */
    public void updateUI() {
        // 实际应用中，这里会根据用户数据更新UI
        // 当前使用的是模拟数据
    }
}