package com.financetracker.view;

import com.financetracker.model.User;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PiePlot;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 概览面板 - 显示支出分类和饼图
 */
public class OverviewPanel extends JPanel {

    @SuppressWarnings("unused")
    private User currentUser;
    private JLabel monthLabel;
    private JPanel categoriesPanel;
    private ChartPanel chartPanel;
    private JButton classifyButton;
    private JButton exportButton;
    private JTextField newCategoryField;
    private JTextField newAmountField;
    private JDialog editDialog;

    public OverviewPanel(User user) {
        this.currentUser = user;
        setupUI();
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

        // 添加下拉箭头图标
        JPanel dateSelectorPanel = new JPanel(new BorderLayout(5, 0));
        dateSelectorPanel.setBackground(Color.WHITE);
        dateSelectorPanel.add(monthLabel, BorderLayout.CENTER);
        JLabel downArrowLabel = new JLabel("▼");
        downArrowLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateSelectorPanel.add(downArrowLabel, BorderLayout.EAST);

        datePanel.add(dateSelectorPanel);
        topPanel.add(datePanel, BorderLayout.CENTER);

        // 中心内容面板
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(Color.WHITE);

        // 左侧面板 - 饼图
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建饼图
        JFreeChart chart = createPieChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 400));
        leftPanel.add(chartPanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        classifyButton = new JButton("Classify");
        exportButton = new JButton("Export");

        // 设置按钮样式
        classifyButton.setFont(new Font("Arial", Font.BOLD, 14));
        classifyButton.setBackground(Color.BLACK);
        classifyButton.setForeground(Color.WHITE);
        classifyButton.setFocusPainted(false);

        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.setBackground(Color.BLACK);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);

        buttonPanel.add(classifyButton);
        buttonPanel.add(exportButton);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 右侧面板 - 分类明细
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        // 分类明细面板
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(Color.WHITE);

        // 添加模拟分类数据
        addMockCategories();

        JScrollPane categoriesScrollPane = new JScrollPane(categoriesPanel);
        categoriesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        categoriesScrollPane.getViewport().setBackground(Color.WHITE);

        // 添加"添加分类"按钮
        JButton addCategoryButton = new JButton("+ Add Classification");
        addCategoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addCategoryButton.setFont(new Font("Arial", Font.BOLD, 14));
        addCategoryButton.setBackground(Color.BLACK);
        addCategoryButton.setForeground(Color.WHITE);
        addCategoryButton.setFocusPainted(false);

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.setBackground(Color.WHITE);
        addButtonPanel.add(addCategoryButton);

        rightPanel.add(categoriesScrollPane, BorderLayout.CENTER);
        rightPanel.add(addButtonPanel, BorderLayout.SOUTH);

        // 添加左右面板到中心面板
        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        // 添加所有面板到主面板
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // 添加按钮事件监听器
        addCategoryButton.addActionListener(e -> showAddCategoryDialog());
        classifyButton.addActionListener(e -> classifyTransactions());
        exportButton.addActionListener(e -> exportData());
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // 模拟支出分类数据
        dataset.setValue("Shopping", 62.9);
        dataset.setValue("Food", 13.9);
        dataset.setValue("Transport", 7.0);
        dataset.setValue("Recreation", 7.2);
        dataset.setValue("Study Article", 9.1);

        // 创建饼图
        JFreeChart chart = ChartFactory.createPieChart(
                "2025/03", // 图表标题
                dataset, // 数据集
                true, // 是否包含图例
                true, // 是否生成工具提示
                false // 是否生成URL链接
        );

        // 设置饼图样式
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 20));
        chart.getTitle().setPaint(Color.BLACK);

        // 设置图例
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(Color.WHITE);
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

        // 获取饼图绘图区
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 220));
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(null);

        // 设置各分类的颜色
        plot.setSectionPaint("Shopping", new Color(0, 86, 91));
        plot.setSectionPaint("Food", new Color(105, 190, 171));
        plot.setSectionPaint("Transport", new Color(43, 151, 147));
        plot.setSectionPaint("Recreation", new Color(65, 182, 196));
        plot.setSectionPaint("Study Article", new Color(34, 113, 146));

        // 设置分隔线
        plot.setSectionOutlinesVisible(true);
        // 为每个部分单独设置描边颜色
        for (Object key : dataset.getKeys()) {
            @SuppressWarnings("unchecked")
            Comparable<String> keyComp = (Comparable<String>) key;
            plot.setSectionOutlinePaint(keyComp, Color.WHITE);
            plot.setSectionOutlineStroke(keyComp, new BasicStroke(2.0f));
        }

        return chart;
    }

    private void addMockCategories() {
        // 模拟分类数据
        addCategoryWithSubcategories("Food", 1500.00, createFoodSubcategories());
        addCategoryWithSubcategories("Transport", 800.00, createTransportSubcategories());
        addCategoryWithSubcategories("Shopping", 6900.00, createShoppingSubcategories());
        addCategoryWithSubcategories("Recreation", 800.00, createRecreationSubcategories());
        addCategoryWithSubcategories("Study Article", 1000.00, createStudySubcategories());
    }

    private Map<String, Double> createFoodSubcategories() {
        Map<String, Double> subCategories = new HashMap<>();
        subCategories.put("KFC", 50.00);
        subCategories.put("McDonald's", 33.00);
        return subCategories;
    }

    private Map<String, Double> createTransportSubcategories() {
        Map<String, Double> subCategories = new HashMap<>();
        subCategories.put("Railway", 794.00);
        subCategories.put("Subway", 6.00);
        return subCategories;
    }

    private Map<String, Double> createShoppingSubcategories() {
        Map<String, Double> subCategories = new HashMap<>();
        subCategories.put("iPad", 4000.00);
        subCategories.put("Clothes", 1000.00);
        return subCategories;
    }

    private Map<String, Double> createRecreationSubcategories() {
        Map<String, Double> subCategories = new HashMap<>();
        subCategories.put("Concert", 800.00);
        return subCategories;
    }

    private Map<String, Double> createStudySubcategories() {
        Map<String, Double> subCategories = new HashMap<>();
        subCategories.put("Books", 1000.00);
        return subCategories;
    }

    private void addCategoryWithSubcategories(String categoryName, double totalAmount,
                                              Map<String, Double> subCategories) {
        // 创建主面板
        JPanel categoryPanel = new JPanel(new BorderLayout(10, 5));
        categoryPanel.setBackground(Color.WHITE);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 左侧图标和名称
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setBackground(Color.WHITE);

        // 图标选择
        Color iconColor = getCategoryColor(categoryName);
        JPanel iconPanel = createCircleIcon(iconColor, 24);

        // 分类名称和编辑按钮
        JPanel namePanel = new JPanel(new BorderLayout(5, 0));
        namePanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(categoryName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton editButton = new JButton("✏️");
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);

        namePanel.add(nameLabel, BorderLayout.CENTER);
        namePanel.add(editButton, BorderLayout.EAST);

        leftPanel.add(iconPanel, BorderLayout.WEST);
        leftPanel.add(namePanel, BorderLayout.CENTER);

        // 金额和删除按钮
        JPanel rightPanel = new JPanel(new BorderLayout(5, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel amountLabel = new JLabel("-RMB " + String.format("%.2f", totalAmount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(new Color(220, 20, 60));

        JButton deleteButton = new JButton("×");
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));

        rightPanel.add(amountLabel, BorderLayout.CENTER);
        rightPanel.add(deleteButton, BorderLayout.EAST);

        // 添加到主面板
        categoryPanel.add(leftPanel, BorderLayout.WEST);
        categoryPanel.add(rightPanel, BorderLayout.EAST);

        // 子分类面板，包含所有子分类
        JPanel subCategoriesPanel = new JPanel();
        subCategoriesPanel.setLayout(new BoxLayout(subCategoriesPanel, BoxLayout.Y_AXIS));
        subCategoriesPanel.setBackground(Color.WHITE);
        subCategoriesPanel.setBorder(BorderFactory.createEmptyBorder(5, 34, 0, 0));

        // 添加子分类
        for (Map.Entry<String, Double> entry : subCategories.entrySet()) {
            JPanel subCategoryPanel = new JPanel(new BorderLayout(10, 0));
            subCategoryPanel.setBackground(Color.WHITE);
            subCategoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            // 子分类图标和名称
            JPanel subLeftPanel = new JPanel(new BorderLayout(10, 0));
            subLeftPanel.setBackground(Color.WHITE);

            JPanel pencilIcon = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(5, 5, 10, 10);
                }
            };
            pencilIcon.setPreferredSize(new Dimension(20, 20));
            pencilIcon.setOpaque(false);

            JLabel subNameLabel = new JLabel(entry.getKey());
            subNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            subLeftPanel.add(pencilIcon, BorderLayout.WEST);
            subLeftPanel.add(subNameLabel, BorderLayout.CENTER);

            // 子分类金额
            JLabel subAmountLabel = new JLabel("-RMB " + String.format("%.2f", entry.getValue()));
            subAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            subAmountLabel.setForeground(new Color(220, 20, 60));

            subCategoryPanel.add(subLeftPanel, BorderLayout.WEST);
            subCategoryPanel.add(subAmountLabel, BorderLayout.EAST);

            subCategoriesPanel.add(subCategoryPanel);
        }

        // 创建一个包含主分类和子分类的面板
        JPanel completePanel = new JPanel(new BorderLayout());
        completePanel.setBackground(Color.WHITE);
        completePanel.add(categoryPanel, BorderLayout.NORTH);
        completePanel.add(subCategoriesPanel, BorderLayout.CENTER);

        // 添加到分类面板
        categoriesPanel.add(completePanel);

        // 添加分隔线
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(230, 230, 230));
        categoriesPanel.add(separator);

        // 添加按钮事件
        final String catName = categoryName;
        editButton.addActionListener(e -> showEditCategoryDialog(catName));
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(OverviewPanel.this,
                    "Are you sure you want to delete " + catName + " category?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                categoriesPanel.remove(completePanel);
                categoriesPanel.remove(separator);
                categoriesPanel.revalidate();
                categoriesPanel.repaint();
            }
        });
    }

    private JPanel createCircleIcon(Color color, int size) {
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(0, 0, size, size);
            }
        };
        iconPanel.setPreferredSize(new Dimension(size, size));
        iconPanel.setOpaque(false);
        return iconPanel;
    }

    private Color getCategoryColor(String category) {
        switch (category) {
            case "Food":
                return new Color(30, 144, 255);
            case "Transport":
                return new Color(106, 90, 205);
            case "Shopping":
                return new Color(60, 179, 113);
            case "Recreation":
                return new Color(255, 165, 0);
            case "Study Article":
                return new Color(128, 0, 128);
            default:
                return new Color(100, 100, 100);
        }
    }

    private void showAddCategoryDialog() {
        JDialog addCategoryDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Classification",
                true);
        addCategoryDialog.setSize(400, 250);
        addCategoryDialog.setLocationRelativeTo(this);
        addCategoryDialog.setLayout(new BorderLayout());
        addCategoryDialog.getContentPane().setBackground(Color.WHITE);

        // 表单面板
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        newCategoryField = new JTextField(15);
        newCategoryField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        newAmountField = new JTextField(15);
        newAmountField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBackground(Color.WHITE);

        JPanel selectedColorPanel = new JPanel();
        selectedColorPanel.setBackground(new Color(60, 179, 113));
        selectedColorPanel.setPreferredSize(new Dimension(30, 30));
        selectedColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton chooseColorButton = new JButton("Choose...");
        chooseColorButton.setFont(new Font("Arial", Font.PLAIN, 14));

        colorPanel.add(selectedColorPanel);
        colorPanel.add(chooseColorButton);

        formPanel.add(nameLabel);
        formPanel.add(newCategoryField);
        formPanel.add(amountLabel);
        formPanel.add(newAmountField);
        formPanel.add(colorLabel);
        formPanel.add(colorPanel);

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
        addCategoryDialog.add(formPanel, BorderLayout.CENTER);
        addCategoryDialog.add(buttonPanel, BorderLayout.SOUTH);

        // 添加按钮事件
        cancelButton.addActionListener(e -> addCategoryDialog.dispose());

        chooseColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(addCategoryDialog, "Choose Color",
                    selectedColorPanel.getBackground());
            if (newColor != null) {
                selectedColorPanel.setBackground(newColor);
            }
        });

        submitButton.addActionListener(e -> {
            String name = newCategoryField.getText().trim();
            String amountStr = newAmountField.getText().trim();

            if (name.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(addCategoryDialog,
                        "Name and amount are required",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(addCategoryDialog,
                            "Amount must be greater than zero",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 添加新分类
                Map<String, Double> emptySubCategories = new HashMap<>();
                addCategoryWithSubcategories(name, amount, emptySubCategories);

                // 更新UI
                categoriesPanel.revalidate();
                categoriesPanel.repaint();

                addCategoryDialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addCategoryDialog,
                        "Invalid amount format",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        addCategoryDialog.setVisible(true);
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

    private void classifyTransactions() {
        JOptionPane.showMessageDialog(this,
                "Transactions will be automatically classified using AI.",
                "Auto-Classification",
                JOptionPane.INFORMATION_MESSAGE);
        // 在实际应用中，这里将调用AI服务进行分类
    }

    private void exportData() {
        // 创建文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Data");

        // 提供CSV和PDF选项
        String[] exportOptions = { "CSV Format", "PDF Format" };
        String exportType = (String) JOptionPane.showInputDialog(
                this,
                "Select export format:",
                "Export Data",
                JOptionPane.QUESTION_MESSAGE,
                null,
                exportOptions,
                exportOptions[0]);

        if (exportType != null) {
            // 设置文件过滤器
            if (exportType.equals("CSV Format")) {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".csv") || f.isDirectory();
                    }

                    public String getDescription() {
                        return "CSV Files (*.csv)";
                    }
                });
            } else {
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(java.io.File f) {
                        return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
                    }

                    public String getDescription() {
                        return "PDF Files (*.pdf)";
                    }
                });
            }

            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                // 处理导出文件的逻辑
                JOptionPane.showMessageDialog(this,
                        "Exported file: " + selectedFile.getName() + " as " + exportType,
                        "Export Data",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}