package com.financetracker.view;

import com.financetracker.model.User;
import com.financetracker.model.Transaction;
import com.financetracker.service.TransactionService;
import com.financetracker.util.LanguageUtil;
import com.financetracker.view.utils.RoundedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.LegendTitle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.EnumMap;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormatSymbols;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;

/**
 * 仪表板面板 - 显示用户的月收入和支出
 */
public class DashboardPanel extends JPanel implements PropertyChangeListener {

        private User currentUser;
        private TransactionService transactionService;
        private JLabel dateLabel;
        private JLabel incomeLabel;
        private JLabel expensesLabel;

        private ChartPanel chartPanel;
        private DefaultCategoryDataset trendDataset;
        private JFreeChart chart;

        private static final Color PANEL_BACKGROUND_COLOR = Color.WHITE;
        private static final Color APP_BACKGROUND_COLOR = new Color(245, 245, 245);
        private static final int CARD_ARC = 8;
        private static final int CARD_BORDER_THICKNESS = 1;
        private static final Color CARD_BORDER_COLOR = new Color(220, 220, 220);
        private static final Border CARD_PADDING = new EmptyBorder(15, 15, 15, 15);

        // 颜色常量
        private static final Color INCOME_TEXT_COLOR = new Color(44, 165, 141); // #2CA58D
        private static final Color EXPENSE_TEXT_COLOR = new Color(231, 111, 81); // #E76F51
        private static final Color CHART_OUTCOME_COLOR = new Color(137, 207, 200); // 图表中的支出线颜色

        public DashboardPanel(User user, TransactionService transactionService) {
                this.currentUser = user;
                this.transactionService = transactionService;
                this.transactionService.addPropertyChangeListener(this);
                setupMainLayout();
                refreshDashboardData();
        }

        private void setupMainLayout() {
                setLayout(new BorderLayout(0, 15));
                setBorder(new EmptyBorder(20, 20, 20, 20));
                setBackground(APP_BACKGROUND_COLOR);

                add(createGreetingPanel(), BorderLayout.NORTH);

                // 只保留左侧图表部分
                JPanel cardPanel = createCardPanel();
                cardPanel.setLayout(new BorderLayout(10, 10));

                // 保留月份显示和收入/支出汇总
                JPanel summaryDisplayPanel = createSummaryDisplayPanel();
                cardPanel.add(summaryDisplayPanel, BorderLayout.NORTH);

                // 图表初始化
                initChartPanel();
                cardPanel.add(chartPanel, BorderLayout.CENTER);

                // 添加到主面板
                add(cardPanel, BorderLayout.CENTER);
        }

        private JPanel createCardPanel() {
                JPanel card = new JPanel();
                card.setBackground(PANEL_BACKGROUND_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                                new RoundedBorder(CARD_BORDER_COLOR, CARD_ARC, CARD_BORDER_THICKNESS),
                                CARD_PADDING));
                return card;
        }

        private JPanel createSummaryDisplayPanel() {
                JPanel summaryPanel = new JPanel(new BorderLayout(0, 20));
                summaryPanel.setOpaque(false);

                // 日期选择器面板 (居中显示)
                JPanel dateSelectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                dateSelectorPanel.setOpaque(false);
                dateLabel = new JLabel(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
                dateLabel.setFont(new Font("Arial", Font.BOLD, 22));
                dateLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel downArrowLabel = new JLabel(" ▼");
                downArrowLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                downArrowLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // 为日期标签和箭头添加月份选择功能
                MouseAdapter monthSelectorMouseAdapter = new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                showMonthYearChooser();
                        }
                };
                dateLabel.addMouseListener(monthSelectorMouseAdapter);
                downArrowLabel.addMouseListener(monthSelectorMouseAdapter);

                dateSelectorPanel.add(dateLabel);
                dateSelectorPanel.add(downArrowLabel);
                summaryPanel.add(dateSelectorPanel, BorderLayout.NORTH);

                // 收入和支出面板 (水平排列)
                JPanel incomeExpensesPanel = new JPanel();
                incomeExpensesPanel.setLayout(new GridLayout(1, 2, 40, 0));
                incomeExpensesPanel.setOpaque(false);
                incomeExpensesPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

                // 收入部分
                JPanel incomeSection = new JPanel();
                incomeSection.setLayout(new BoxLayout(incomeSection, BoxLayout.Y_AXIS));
                incomeSection.setOpaque(false);
                incomeSection.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

                JLabel incomeTitleLabel = new JLabel(LanguageUtil.getText("dashboard.income"));
                incomeTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                incomeTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                incomeLabel = new JLabel("+RMB 0.00");
                incomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
                incomeLabel.setForeground(INCOME_TEXT_COLOR);
                incomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                incomeSection.add(Box.createVerticalGlue());
                incomeSection.add(incomeTitleLabel);
                incomeSection.add(Box.createVerticalStrut(10));
                incomeSection.add(incomeLabel);
                incomeSection.add(Box.createVerticalGlue());

                // 支出部分
                JPanel expenseSection = new JPanel();
                expenseSection.setLayout(new BoxLayout(expenseSection, BoxLayout.Y_AXIS));
                expenseSection.setOpaque(false);

                JLabel expenseTitleLabel = new JLabel(LanguageUtil.getText("dashboard.expenses"));
                expenseTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                expenseTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                expensesLabel = new JLabel("-RMB 0.00");
                expensesLabel.setFont(new Font("Arial", Font.BOLD, 22));
                expensesLabel.setForeground(EXPENSE_TEXT_COLOR);
                expensesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                expenseSection.add(Box.createVerticalGlue());
                expenseSection.add(expenseTitleLabel);
                expenseSection.add(Box.createVerticalStrut(10));
                expenseSection.add(expensesLabel);
                expenseSection.add(Box.createVerticalGlue());

                incomeExpensesPanel.add(incomeSection);
                incomeExpensesPanel.add(expenseSection);

                // 创建一个带有边框的面板来包裹收入和支出
                JPanel borderedPanel = new JPanel(new BorderLayout());
                borderedPanel.setOpaque(false);
                borderedPanel.setBorder(new RoundedBorder(new Color(240, 240, 240), 10, 1));
                borderedPanel.add(incomeExpensesPanel, BorderLayout.CENTER);

                summaryPanel.add(borderedPanel, BorderLayout.CENTER);

                return summaryPanel;
        }

        private void initChartPanel() {
                // 创建数据集
                trendDataset = new DefaultCategoryDataset();

                // 创建图表
                chart = ChartFactory.createLineChart(
                                null, // 图表标题
                                "Date", // X轴标签 - 直接使用字符串
                                "Amount (RMB)", // Y轴标签 - 直接使用字符串
                                trendDataset, // 数据集
                                PlotOrientation.VERTICAL, // 图表方向
                                true, // 是否显示图例
                                true, // 是否使用工具提示
                                false // 是否使用URL链接
                );

                // 设置图表样式
                chart.setBackgroundPaint(Color.WHITE);

                // 获取绘图区域
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                plot.setBackgroundPaint(Color.WHITE);
                plot.setDomainGridlinePaint(new Color(240, 240, 240));
                plot.setRangeGridlinePaint(new Color(240, 240, 240));
                plot.setOutlineVisible(false);
                plot.setDomainGridlinesVisible(true);
                plot.setRangeGridlinesVisible(true);

                // 自定义收入和支出的线条样式
                LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

                // 收入线样式 - 使用绿色，增大线条粗细
                renderer.setSeriesPaint(0, new Color(44, 165, 141));
                renderer.setSeriesStroke(0, new BasicStroke(3.0f));
                renderer.setSeriesShapesVisible(0, true);
                renderer.setSeriesShape(0, new Ellipse2D.Double(-5, -5, 10, 10));
                renderer.setSeriesFillPaint(0, Color.WHITE);
                renderer.setUseFillPaint(true);

                // 支出线样式 - 使用红色，增大线条粗细
                renderer.setSeriesPaint(1, new Color(231, 76, 60));
                renderer.setSeriesStroke(1, new BasicStroke(3.0f));
                renderer.setSeriesShapesVisible(1, true);
                renderer.setSeriesShape(1, new Ellipse2D.Double(-5, -5, 10, 10));
                renderer.setSeriesFillPaint(1, Color.WHITE);

                // 设置X轴样式
                CategoryAxis domainAxis = plot.getDomainAxis();
                domainAxis.setLowerMargin(0.02);
                domainAxis.setUpperMargin(0.02);
                domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 13));

                // 设置Y轴样式
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 13));
                rangeAxis.setNumberFormatOverride(new DecimalFormat("¥#,##0"));
                rangeAxis.setLowerMargin(0.05);
                rangeAxis.setUpperMargin(0.05);

                // 设置图例样式
                LegendTitle legend = chart.getLegend();
                if (legend != null) {
                        legend.setPosition(RectangleEdge.TOP);
                        legend.setFrame(BlockBorder.NONE);
                        legend.setItemFont(new Font("Arial", Font.BOLD, 14));
                }

                // 创建图表面板 - 增大面板尺寸
                chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(800, 400));
                chartPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
                chartPanel.setMouseWheelEnabled(true);
                chartPanel.setBackground(Color.WHITE);
                chartPanel.setFillZoomRectangle(false);
                chartPanel.setMouseZoomable(true, false);
        }

        private void refreshDashboardData() {
                if (transactionService == null) {
                        return;
                }

                YearMonth monthToDisplay;
                // 优先使用dateLabel中用户选择或程序设置的月份
                try {
                        if (dateLabel != null && dateLabel.getText() != null && !dateLabel.getText().isEmpty()) {
                                monthToDisplay = YearMonth.parse(dateLabel.getText(),
                                                DateTimeFormatter.ofPattern("yyyy/MM"));
                        } else {
                                // 如果dateLabel为空，则尝试获取最新交易的月份
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
                                // 确保dateLabel也更新
                                if (dateLabel != null) {
                                        dateLabel.setText(
                                                        monthToDisplay.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                                }
                        }
                } catch (Exception e) {
                        System.err.println("DashboardPanel: Error parsing month from dateLabel, attempting fallback: "
                                        + e.getMessage());
                        // 解析失败时的回退逻辑：显示最新交易的月份或当前月份
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
                        // 确保dateLabel也更新
                        if (dateLabel != null) {
                                dateLabel.setText(monthToDisplay.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                        }
                }

                final YearMonth currentDisplayMonth = monthToDisplay;
                List<Transaction> allTransactions = transactionService.getTransactions(); // 获取所有交易以进行筛选

                double totalIncome = 0;
                double totalExpenses = 0;

                // 收集当前显示月份的交易
                List<Transaction> monthlyTransactions = allTransactions.stream()
                                .filter(t -> YearMonth.from(t.getDate()).equals(currentDisplayMonth))
                                .collect(Collectors.toList());

                // 计算总收入和支出
                for (Transaction t : monthlyTransactions) {
                        if (t.getType() == Transaction.TransactionType.INCOME) {
                                totalIncome += t.getAmount();
                        } else {
                                totalExpenses += t.getAmount();
                        }
                }

                // 更新收入和支出标签
                DecimalFormat df = new DecimalFormat("#,##0.00");
                if (incomeLabel != null) {
                        incomeLabel.setText(String.format("+RMB %s", df.format(totalIncome)));
                }
                if (expensesLabel != null) {
                        expensesLabel.setText(String.format("-RMB %s", df.format(Math.abs(totalExpenses))));
                }

                // 更新图表数据
                updateChartData(monthlyTransactions, currentDisplayMonth);
        }

        /**
         * 更新图表数据，X轴显示日期 (dd)
         */
        private void updateChartData(List<Transaction> monthlyTransactions, YearMonth currentMonth) {
                trendDataset.clear();

                Map<LocalDate, Double> incomeByDate = new HashMap<>();
                Map<LocalDate, Double> expenseByDate = new HashMap<>();

                // 按日期聚合交易数据
                for (Transaction t : monthlyTransactions) {
                        LocalDate date = t.getDate();
                        if (t.getType() == Transaction.TransactionType.INCOME) {
                                incomeByDate.merge(date, t.getAmount(), Double::sum);
                        } else {
                                expenseByDate.merge(date, Math.abs(t.getAmount()), Double::sum);
                        }
                }

                String incomeSeriesKey = LanguageUtil.getText("dashboard.chart.series.income");
                String expenseSeriesKey = LanguageUtil.getText("dashboard.chart.series.outcome");
                DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");

                // 获取月份中的所有日期，并确保它们已排序
                List<LocalDate> datesInMonth = monthlyTransactions.stream()
                                .map(Transaction::getDate)
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());

                // 如果当月没有交易，但仍希望显示X轴（例如从1到月底），则需要额外逻辑
                // 目前，仅为有交易的日期创建数据点
                if (datesInMonth.isEmpty() && !monthlyTransactions.isEmpty()) {
                        // 这个情况理论上不应该发生，如果 monthlyTransactions 非空，datesInMonth 也应该非空
                        // 但作为防御性编程，可以处理
                } else if (datesInMonth.isEmpty() && monthlyTransactions.isEmpty()) {
                        // 如果整个月都没有交易，可以显示一个空图表或特定消息
                        // 为了简单起见，这里将显示一个空的X轴和0值
                        // 例如，可以尝试显示月份中的每一天
                        int daysInMonth = currentMonth.lengthOfMonth();
                        for (int i = 1; i <= daysInMonth; i++) {
                                String dayOfMonthStr = String.format("%02d", i);
                                trendDataset.addValue(0.0, incomeSeriesKey, dayOfMonthStr);
                                trendDataset.addValue(0.0, expenseSeriesKey, dayOfMonthStr);
                        }
                } else {
                        for (LocalDate date : datesInMonth) {
                                String dayOfMonthStr = date.format(dayFormatter);
                                trendDataset.addValue(incomeByDate.getOrDefault(date, 0.0), incomeSeriesKey,
                                                dayOfMonthStr);
                                trendDataset.addValue(expenseByDate.getOrDefault(date, 0.0), expenseSeriesKey,
                                                dayOfMonthStr);
                        }
                }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                if ("transactionsChanged".equals(evt.getPropertyName())) {
                        // 当交易数据发生变化时 (例如CSV导入或手动添加)
                        // 1. 确定应该显示的月份 (通常是最新交易的月份)
                        // 需要确保 transactionService 不是 null，尽管通常 propertyChange 被触发时它应该是有效的
                        if (transactionService == null)
                                return;
                        List<Transaction> allTransactions = transactionService.getTransactions();
                        YearMonth monthToSet;
                        if (allTransactions.isEmpty()) {
                                monthToSet = YearMonth.now(); // 如果没有交易，则为当前月
                        } else {
                                LocalDate latestTransactionDate = allTransactions.stream()
                                                .map(Transaction::getDate)
                                                .max(LocalDate::compareTo)
                                                .orElse(LocalDate.now());
                                monthToSet = YearMonth.from(latestTransactionDate);
                        }

                        // 2. 更新dateLabel以反映这个月份
                        // 确保在UI线程中更新UI组件
                        SwingUtilities.invokeLater(() -> {
                                if (dateLabel != null) {
                                        dateLabel.setText(monthToSet.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                                }
                                // 3. 刷新仪表板数据，refreshDashboardData会读取dateLabel的值
                                refreshDashboardData();
                        });

                } else if ("userSettingsChanged".equals(evt.getPropertyName())) {
                        // 用户设置更改可能影响语言等，也需要刷新
                        // 确保在UI线程中更新UI组件
                        SwingUtilities.invokeLater(this::refreshDashboardData);
                }
        }

        private JPanel createGreetingPanel() {
                JPanel greetingPanel = new JPanel(new BorderLayout());
                greetingPanel.setOpaque(false);
                greetingPanel.setBorder(new EmptyBorder(0, 5, 15, 0));

                // 左侧欢迎信息
                JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
                leftPanel.setOpaque(false);

                String userName = (currentUser != null && currentUser.getUsername() != null
                                && !currentUser.getUsername().isEmpty())
                                                ? currentUser.getUsername()
                                                : "User";
                JLabel greetingLabel = new JLabel("Hi, " + userName + "!");
                greetingLabel.setFont(new Font("Arial", Font.BOLD, 28));
                greetingLabel.setForeground(Color.BLACK);

                // 添加当前日期显示
                JLabel dateInfoLabel = new JLabel(LocalDate.now().format(
                                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)));
                dateInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                dateInfoLabel.setForeground(new Color(100, 100, 100));

                leftPanel.add(greetingLabel);
                leftPanel.add(dateInfoLabel);

                // 添加到主面板
                greetingPanel.add(leftPanel, BorderLayout.WEST);

                return greetingPanel;
        }

        private void showMonthYearChooser() {
                // 创建一个简单的月份年份选择对话框
                JDialog monthYearDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Select Month and Year",
                                Dialog.ModalityType.APPLICATION_MODAL);
                monthYearDialog.setLayout(new BorderLayout(10, 10));
                monthYearDialog.setSize(300, 150);
                monthYearDialog.setLocationRelativeTo(this);

                JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

                // 年份选择
                SpinnerModel yearModel = new SpinnerNumberModel(YearMonth.now().getYear(), 1900, 2100, 1);
                JSpinner yearSpinner = new JSpinner(yearModel);
                yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));

                // 月份选择 (1-12)
                String[] monthNames = new DateFormatSymbols().getMonths();
                // 移除最后一个空字符串 (如果存在)
                int monthCount = 12;
                String[] displayMonthNames = new String[monthCount];
                System.arraycopy(monthNames, 0, displayMonthNames, 0, monthCount);
                JComboBox<String> monthComboBox = new JComboBox<>(displayMonthNames);
                monthComboBox.setSelectedIndex(YearMonth.now().getMonthValue() - 1);

                selectionPanel.add(new JLabel("Month:"));
                selectionPanel.add(monthComboBox);
                selectionPanel.add(new JLabel("Year:"));
                selectionPanel.add(yearSpinner);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
                JButton okButton = new JButton("OK");
                JButton cancelButton = new JButton("Cancel");

                okButton.addActionListener(e -> {
                        int year = (Integer) yearSpinner.getValue();
                        int month = monthComboBox.getSelectedIndex() + 1; // JComboBox索引从0开始
                        YearMonth selectedYearMonth = YearMonth.of(year, month);
                        dateLabel.setText(selectedYearMonth.format(DateTimeFormatter.ofPattern("yyyy/MM")));
                        refreshDashboardData(); // 使用新的月份刷新数据
                        monthYearDialog.dispose();
                });

                cancelButton.addActionListener(e -> monthYearDialog.dispose());

                buttonPanel.add(okButton);
                buttonPanel.add(cancelButton);

                monthYearDialog.add(selectionPanel, BorderLayout.CENTER);
                monthYearDialog.add(buttonPanel, BorderLayout.SOUTH);
                monthYearDialog.setVisible(true);
        }
}
