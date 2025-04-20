package com.financetracker.model;

import java.awt.Color;

/**
 * 分类模型类 - 表示交易的分类
 */
public class Category {
    private String id;
    private String name;
    private String description;
    private Color color;
    private boolean isDefault;
    private boolean isUserDefined;
    private static int categoryCounter = 100;

    // 预定义的支出类别
    public static final Category FOOD = new Category("餐饮", "餐馆、外卖和食品杂货", new Color(255, 99, 71), true, false);
    public static final Category TRANSPORT = new Category("交通", "公共交通、出租车和共享单车", new Color(106, 90, 205), true, false);
    public static final Category SHOPPING = new Category("购物", "衣服、电子产品和家居用品", new Color(60, 179, 113), true, false);
    public static final Category ENTERTAINMENT = new Category("娱乐", "电影、音乐会和游戏", new Color(255, 165, 0), true, false);
    public static final Category UTILITIES = new Category("水电煤", "水费、电费、燃气费", new Color(30, 144, 255), true, false);
    public static final Category RENT = new Category("房租", "房租和物业费", new Color(148, 0, 211), true, false);
    public static final Category EDUCATION = new Category("教育", "学费、书籍和课程", new Color(46, 139, 87), true, false);
    public static final Category HEALTH = new Category("医疗健康", "医院、药品和健身", new Color(220, 20, 60), true, false);

    // 预定义的收入类别
    public static final Category SALARY = new Category("工资", "工资和奖金", new Color(50, 205, 50), true, true);
    public static final Category INVESTMENT = new Category("投资", "股息、利息和资本收益", new Color(0, 191, 255), true, true);
    public static final Category GIFT = new Category("礼金", "红包和礼物", new Color(255, 69, 0), true, true);
    public static final Category REFUND = new Category("退款", "退款和报销", new Color(218, 165, 32), true, true);

    public Category(String name, String description, Color color, boolean isDefault, boolean isIncome) {
        this.id = generateId();
        this.name = name;
        this.description = description;
        this.color = color;
        this.isDefault = isDefault;
        this.isUserDefined = false;
    }

    /**
     * 创建用户定义的类别
     */
    public static Category createUserCategory(String name, String description, Color color) {
        Category category = new Category(name, description, color, false, false);
        category.isUserDefined = true;
        return category;
    }

    /**
     * 生成唯一类别ID
     */
    private String generateId() {
        return "CAT" + (++categoryCounter);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isUserDefined() {
        return isUserDefined;
    }

    @Override
    public String toString() {
        return name;
    }
}