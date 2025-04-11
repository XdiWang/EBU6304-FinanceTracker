package com.financetracker.service;

import com.financetracker.model.Category;
import com.financetracker.model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.LocalDate;

/**
 * AI服务 - 处理AI相关功能
 * 注意：这是一个模拟AI功能的简化实现
 */
public class AIService {

    private Random random = new Random();

    /**
     * 自动对交易进行分类
     * 
     * @param description 交易描述
     * @return 推荐的分类
     */
    public Category classifyTransaction(String description) {
        // 在实际应用中，这里应该使用机器学习模型进行分类
        // 现在使用简单的关键词匹配

        String lowerDesc = description.toLowerCase();

        if (lowerDesc.contains("餐厅") || lowerDesc.contains("饭") ||
                lowerDesc.contains("菜") || lowerDesc.contains("超市") ||
                lowerDesc.contains("食品") || lowerDesc.contains("外卖")) {
            return Category.FOOD;
        } else if (lowerDesc.contains("公交") || lowerDesc.contains("地铁") ||
                lowerDesc.contains("出租") || lowerDesc.contains("机票") ||
                lowerDesc.contains("火车") || lowerDesc.contains("高铁")) {
            return Category.TRANSPORT;
        } else if (lowerDesc.contains("衣") || lowerDesc.contains("鞋") ||
                lowerDesc.contains("包") || lowerDesc.contains("电器") ||
                lowerDesc.contains("手机") || lowerDesc.contains("电脑")) {
            return Category.SHOPPING;
        } else if (lowerDesc.contains("电影") || lowerDesc.contains("游戏") ||
                lowerDesc.contains("门票") || lowerDesc.contains("演唱会") ||
                lowerDesc.contains("KTV")) {
            return Category.ENTERTAINMENT;
        } else if (lowerDesc.contains("水费") || lowerDesc.contains("电费") ||
                lowerDesc.contains("煤气") || lowerDesc.contains("物业")) {
            return Category.UTILITIES;
        } else if (lowerDesc.contains("房租") || lowerDesc.contains("房贷")) {
            return Category.RENT;
        } else if (lowerDesc.contains("书") || lowerDesc.contains("学费") ||
                lowerDesc.contains("培训") || lowerDesc.contains("课程")) {
            return Category.EDUCATION;
        } else if (lowerDesc.contains("医院") || lowerDesc.contains("药") ||
                lowerDesc.contains("诊所") || lowerDesc.contains("体检")) {
            return Category.HEALTH;
        } else if (lowerDesc.contains("工资") || lowerDesc.contains("薪水") ||
                lowerDesc.contains("奖金") || lowerDesc.contains("年终奖")) {
            return Category.SALARY;
        } else if (lowerDesc.contains("股票") || lowerDesc.contains("基金") ||
                lowerDesc.contains("分红") || lowerDesc.contains("利息")) {
            return Category.INVESTMENT;
        } else if (lowerDesc.contains("红包") || lowerDesc.contains("礼金") ||
                lowerDesc.contains("礼物")) {
            return Category.GIFT;
        } else if (lowerDesc.contains("退款") || lowerDesc.contains("报销")) {
            return Category.REFUND;
        }

        // 如果没有匹配到，随机返回一个类别
        Category[] categories = {
                Category.FOOD, Category.TRANSPORT, Category.SHOPPING,
                Category.ENTERTAINMENT, Category.UTILITIES
        };

        return categories[random.nextInt(categories.length)];
    }

    /**
     * 根据交易数据生成省钱建议
     * 
     * @param transactions 交易列表
     * @return 省钱建议列表
     */
    public List<String> generateSavingSuggestions(List<Transaction> transactions) {
        // 在实际应用中，这里应基于机器学习分析用户的消费模式
        // 现在返回一些通用建议
        return List.of(
                "尝试每周制定餐饮预算，避免冲动消费",
                "对于经常性的小额支出，考虑使用自动储蓄功能",
                "比较不同商家的价格，寻找最优惠的选择",
                "考虑使用公共交通工具代替打车，可以节省大量费用",
                "对于大额购物，尝试等待促销季节再购买");
    }

    /**
     * 预测未来月度支出
     * 
     * @param transactions 历史交易数据
     * @return 各类别的预测支出
     */
    public Map<Category, Double> predictMonthlyExpenses(List<Transaction> transactions) {
        // 实际应用中，这里应该使用时间序列分析或其他预测算法
        // 现在返回模拟数据
        Map<Category, Double> predictions = new HashMap<>();
        predictions.put(Category.FOOD, 1200.0);
        predictions.put(Category.TRANSPORT, 500.0);
        predictions.put(Category.SHOPPING, 2000.0);
        predictions.put(Category.ENTERTAINMENT, 800.0);
        predictions.put(Category.UTILITIES, 400.0);
        predictions.put(Category.RENT, 3000.0);
        predictions.put(Category.EDUCATION, 500.0);
        predictions.put(Category.HEALTH, 300.0);

        return predictions;
    }

    /**
     * 根据历史数据推荐每月预算分配
     * 
     * @param monthlyIncome 月收入
     * @param transactions  历史交易数据
     * @return 各类别的建议预算
     */
    public Map<Category, Double> recommendBudgetAllocation(double monthlyIncome, List<Transaction> transactions) {
        // 实际应用中，应该基于用户的历史消费数据和个人情况调整预算
        // 现在使用常见的预算分配比例

        Map<Category, Double> budgetAllocation = new HashMap<>();
        budgetAllocation.put(Category.FOOD, monthlyIncome * 0.15); // 15% 食品
        budgetAllocation.put(Category.TRANSPORT, monthlyIncome * 0.10); // 10% 交通
        budgetAllocation.put(Category.SHOPPING, monthlyIncome * 0.10); // 10% 购物
        budgetAllocation.put(Category.ENTERTAINMENT, monthlyIncome * 0.05); // 5% 娱乐
        budgetAllocation.put(Category.UTILITIES, monthlyIncome * 0.10); // 10% 水电煤
        budgetAllocation.put(Category.RENT, monthlyIncome * 0.30); // 30% 房租
        budgetAllocation.put(Category.EDUCATION, monthlyIncome * 0.05); // 5% 教育
        budgetAllocation.put(Category.HEALTH, monthlyIncome * 0.05); // 5% 健康
        // 剩余10%用于储蓄

        return budgetAllocation;
    }

    /**
     * 检测异常消费
     * 
     * @param transaction  待检测的交易
     * @param transactions 历史交易数据
     * @return 是否为异常消费
     */
    public boolean detectAbnormalSpending(Transaction transaction, List<Transaction> transactions) {
        // 实际应用中，应该使用统计方法或异常检测算法
        // 现在使用简单的阈值检测

        if (transaction.getAmount() > 5000) {
            // 大额支出可能是异常
            return true;
        }

        // 更多复杂的检测逻辑

        return false;
    }

    /**
     * 生成中国特定节日的预算建议
     * 
     * @param festivalName 节日名称（如春节、中秋节等）
     * @return 预算建议
     */
    public String generateFestivalBudgetAdvice(String festivalName) {
        if ("春节".equals(festivalName)) {
            return "春节预算建议：\n"
                    + "1. 为红包准备专门预算，大约为月收入的10-15%\n"
                    + "2. 购买年货提前规划，可节省约20%费用\n"
                    + "3. 考虑提前1-2个月购买返乡车票，避免高峰期涨价\n"
                    + "4. 家庭聚餐预算约为平时的2倍，提前做好准备";
        } else if ("中秋节".equals(festivalName)) {
            return "中秋节预算建议：\n"
                    + "1. 月饼礼盒购买可提前比较不同品牌价格\n"
                    + "2. 走亲访友的礼品预算约为月收入的5%\n"
                    + "3. 如有出游计划，建议避开高峰期，可节省30%左右";
        } else if ("国庆节".equals(festivalName)) {
            return "国庆节预算建议：\n"
                    + "1. 旅游旺季价格上涨，建议提前3个月规划行程\n"
                    + "2. 考虑短途自驾或周边游，避免长途旅行高成本\n"
                    + "3. 餐饮和住宿费用约为平时的1.5倍，做好预算准备";
        }

        return "对于" + festivalName + "，建议提前规划预算，控制不必要的支出，重点关注礼品和聚会方面的花销。";
    }

    /**
     * 根据交易历史和用户问题提供个性化的聊天建议
     * 
     * @param userInput    用户的问题或请求
     * @param transactions 用户的交易历史
     * @return 个性化的回复
     */
    public String getPersonalizedChatAdvice(String userInput, List<Transaction> transactions) {
        // 这里在实际应用中应该使用NLP模型来理解用户问题并提供个性化回复
        // 现在使用简单的关键词匹配和一些基本的交易分析逻辑

        userInput = userInput.toLowerCase();

        // 如果交易历史为空，提供通用回复
        if (transactions == null || transactions.isEmpty()) {
            if (userInput.contains("建议") || userInput.contains("省钱") || userInput.contains("节约")) {
                return "您目前没有记录任何交易。开始记录您的日常收支可以帮助您更好地了解自己的财务状况。以下是一些通用建议：\n\n"
                        + "1. 制定每月预算计划\n"
                        + "2. 记录每一笔支出，无论金额大小\n"
                        + "3. 区分必要支出和非必要支出\n"
                        + "4. 建立应急资金，至少覆盖3-6个月的基本生活开支";
            } else if (userInput.contains("预测") || userInput.contains("趋势") || userInput.contains("分析")) {
                return "您需要先记录一些交易，才能进行有效的财务分析和预测。建议您开始记录日常收支，至少1-2个月的数据就可以开始看到一些基本模式了。";
            }
        } else {
            // 简单分析交易历史
            double totalIncome = 0;
            double totalExpense = 0;
            Map<Category, Double> categoryExpenses = new HashMap<>();

            // 找出最近一个月的交易
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            List<Transaction> recentTransactions = transactions.stream()
                    .filter(t -> t.getDate().isAfter(oneMonthAgo))
                    .collect(java.util.stream.Collectors.toList());

            // 计算收入和支出
            for (Transaction t : recentTransactions) {
                if (t.isIncome()) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();

                    // 累计各类别支出
                    categoryExpenses.putIfAbsent(t.getCategory(), 0.0);
                    categoryExpenses.put(t.getCategory(), categoryExpenses.get(t.getCategory()) + t.getAmount());
                }
            }

            // 找出最大支出类别
            Category maxExpenseCategory = null;
            double maxExpenseAmount = 0;
            for (Map.Entry<Category, Double> entry : categoryExpenses.entrySet()) {
                if (entry.getValue() > maxExpenseAmount) {
                    maxExpenseAmount = entry.getValue();
                    maxExpenseCategory = entry.getKey();
                }
            }

            // 根据不同问题提供个性化回复
            if (userInput.contains("建议") || userInput.contains("省钱") || userInput.contains("节约")) {
                if (maxExpenseCategory != null) {
                    String categoryName = maxExpenseCategory.getName();
                    double percentage = maxExpenseAmount / totalExpense * 100;

                    return String.format("根据您的消费记录，%s是您最大的支出类别，占总支出的%.1f%%。以下是一些针对性建议：\n\n",
                            categoryName, percentage) +
                            getAdviceForCategory(maxExpenseCategory) +
                            "\n\n总体来说，您的消费结构" + (totalExpense > totalIncome ? "不太合理，支出超过了收入" : "相对合理，但还可以进一步优化") + "。";
                }
            } else if (userInput.contains("预测") || userInput.contains("趋势") || userInput.contains("分析")) {
                double savingRate = (totalIncome - totalExpense) / totalIncome * 100;

                return String.format("根据您过去一个月的消费数据：\n\n" +
                        "总收入：¥%.2f\n" +
                        "总支出：¥%.2f\n" +
                        "节省率：%.1f%%\n\n", totalIncome, totalExpense, savingRate) +
                        "预计下个月您的消费趋势：\n" +
                        (maxExpenseCategory != null ? "- " + maxExpenseCategory.getName() + "仍将是主要支出\n" : "") +
                        (savingRate < 10 ? "- 建议增加储蓄比例至少达到10%\n" : "- 您的储蓄习惯良好，请继续保持\n") +
                        "- " + (random.nextBoolean() ? "可能会有季节性支出增加\n" : "整体支出可能会保持稳定\n");
            }
        }

        // 通用回复
        if (userInput.contains("投资") || userInput.contains("基金") || userInput.contains("股票")) {
            return "关于投资，我有以下建议：\n\n" +
                    "1. 投资前请确保您已有足够的应急资金\n" +
                    "2. 了解自己的风险承受能力\n" +
                    "3. 分散投资以降低风险\n" +
                    "4. 长期投资通常比短期投机更稳健\n" +
                    "5. 定期投资可以平摊市场波动风险";
        } else if (userInput.contains("房贷") || userInput.contains("房子") || userInput.contains("买房")) {
            return "关于房贷，以下是一些建议：\n\n" +
                    "1. 房贷月供最好不超过月收入的30%\n" +
                    "2. 比较不同银行的贷款利率\n" +
                    "3. 考虑提前还款可以节省利息\n" +
                    "4. 选择适合自己的还款方式（等额本金或等额本息）";
        } else if (userInput.contains("理财") || userInput.contains("储蓄") || userInput.contains("存钱")) {
            return "关于个人理财，我建议：\n\n" +
                    "1. 遵循'收入-储蓄=支出'的原则，而不是'收入-支出=储蓄'\n" +
                    "2. 设立多个储蓄目标（短期、中期和长期）\n" +
                    "3. 使用自动转账功能定期存钱\n" +
                    "4. 考虑通胀因素，纯储蓄可能会导致购买力下降";
        }

        // 默认回复
        return "您可以问我关于预算规划、支出分析、储蓄建议、投资策略等问题，我会根据您的财务数据提供个性化建议。";
    }

    /**
     * 根据不同类别提供针对性建议
     */
    private String getAdviceForCategory(Category category) {
        if (category == Category.FOOD) {
            return "1. 尝试每周制定饮食计划并提前准备食材\n" +
                    "2. 减少外卖和餐厅就餐次数\n" +
                    "3. 使用优惠券或参加促销活动购买食品\n" +
                    "4. 选择季节性食材可以节省开支";
        } else if (category == Category.TRANSPORT) {
            return "1. 尽量使用公共交通工具代替打车\n" +
                    "2. 考虑拼车或共享单车服务\n" +
                    "3. 合理规划行程，减少不必要的出行\n" +
                    "4. 可以考虑购买交通月票以节省开支";
        } else if (category == Category.SHOPPING) {
            return "1. 购物前列清单并严格遵守\n" +
                    "2. 等待促销季节再购买非必需品\n" +
                    "3. 考虑二手市场或租赁选项\n" +
                    "4. 遵循24小时规则：大额购物前考虑24小时再决定";
        } else if (category == Category.ENTERTAINMENT) {
            return "1. 寻找免费或低成本的娱乐活动\n" +
                    "2. 使用家庭影院代替频繁去电影院\n" +
                    "3. 考虑与朋友分享订阅服务费用\n" +
                    "4. 设定娱乐预算并严格遵守";
        } else if (category == Category.UTILITIES) {
            return "1. 注意节约用水用电\n" +
                    "2. 考虑安装节能设备或灯具\n" +
                    "3. 比较不同供应商的价格\n" +
                    "4. 避免电器待机耗电";
        } else if (category == Category.RENT) {
            return "1. 考虑找室友分担房租\n" +
                    "2. 与房东协商长期租约以获得优惠\n" +
                    "3. 比较不同区域的租金差异\n" +
                    "4. 房租最好不超过月收入的30%";
        } else if (category == Category.EDUCATION) {
            return "1. 寻找免费的在线学习资源\n" +
                    "2. 利用图书馆资源代替购买书籍\n" +
                    "3. 申请教育补助或奖学金\n" +
                    "4. 评估教育支出的长期回报";
        } else if (category == Category.HEALTH) {
            return "1. 保持健康生活方式，预防胜于治疗\n" +
                    "2. 充分利用医保福利\n" +
                    "3. 比较不同药店的药品价格\n" +
                    "4. 定期体检可以避免更大的医疗支出";
        } else {
            return "1. 仔细记录并分析您在该类别的支出\n" +
                    "2. 比较不同供应商或品牌的价格\n" +
                    "3. 寻找可能的替代品或服务\n" +
                    "4. 设定合理的预算限额";
        }
    }
}
