package com.financetracker.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.model.Transaction;

/**
 * DeepSeek API服务 - 调用DeepSeek API处理AI对话功能
 */
public class DeepSeekAPIService {
    private static final Logger LOGGER = Logger.getLogger(DeepSeekAPIService.class.getName());

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final String API_KEY = "xxxxxxxxxx"; // 替换为您的API密钥
    private static final String MODEL = "deepseek-chat";

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final List<Map<String, String>> conversationHistory;
    private final ExecutorService executorService;

    /**
     * 创建DeepSeek API服务
     */
    public DeepSeekAPIService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.conversationHistory = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();

        // 添加系统提示
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的财务顾问，擅长个人理财。能提供财务分析、预算规划、投资建议和财务目标规划。你的回答要简短、专业、富有洞察力，并且适合中国的金融环境和消费环境。");
        conversationHistory.add(systemMessage);
    }

    /**
     * 重置对话历史
     */
    public void resetConversation() {
        conversationHistory.clear();

        // 重新添加系统提示
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的财务顾问，擅长个人理财。能提供财务分析、预算规划、投资建议和财务目标规划。你的回答要简短、专业、富有洞察力，并且适合中国的金融环境和消费环境。");
        conversationHistory.add(systemMessage);
    }

    /**
     * 流式聊天方法，用回调函数返回生成的消息
     * 
     * @param userMessage       用户消息
     * @param transactions      交易记录
     * @param onPartialResponse 部分响应的回调
     * @param onComplete        完成时的回调，返回完整响应
     */
    public void streamChat(String userMessage, List<Transaction> transactions,
            Consumer<String> onPartialResponse,
            Consumer<String> onComplete) {
        executorService.submit(() -> {
            try {
                // 准备用户消息内容
                StringBuilder messageContent = new StringBuilder(userMessage);
                appendTransactionData(messageContent, transactions);

                // 创建用户消息
                Map<String, String> userMessageMap = new HashMap<>();
                userMessageMap.put("role", "user");
                userMessageMap.put("content", messageContent.toString());
                conversationHistory.add(userMessageMap);

                // 准备请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", MODEL);
                requestBody.put("messages", conversationHistory);
                requestBody.put("stream", true); // 启用流式输出

                String requestBodyJson = objectMapper.writeValueAsString(requestBody);

                // 发送API请求
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                        .timeout(Duration.ofSeconds(60))
                        .build();

                StringBuilder fullResponse = new StringBuilder();

                client.send(request, HttpResponse.BodyHandlers.ofLines())
                        .body()
                        .forEach(line -> {
                            if (line.startsWith("data: ") && !line.contains("[DONE]")) {
                                String jsonData = line.substring(6); // 移除 "data: " 前缀
                                try {
                                    // 解析流式响应
                                    Map<String, Object> responseData = objectMapper.readValue(
                                            jsonData,
                                            new TypeReference<Map<String, Object>>() {
                                            });

                                    if (responseData.containsKey("choices")
                                            && responseData.get("choices") instanceof List) {
                                        @SuppressWarnings("unchecked")
                                        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseData
                                                .get("choices");
                                        if (!choices.isEmpty()) {
                                            Map<String, Object> firstChoice = choices.get(0);
                                            if (firstChoice.containsKey("delta")
                                                    && firstChoice.get("delta") instanceof Map) {
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> delta = (Map<String, Object>) firstChoice
                                                        .get("delta");
                                                if (delta.containsKey("content")) {
                                                    String content = (String) delta.get("content");
                                                    if (content != null) {
                                                        fullResponse.append(content);
                                                        onPartialResponse.accept(content);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (JsonProcessingException e) {
                                    LOGGER.log(Level.WARNING, "解析流式响应时出错", e);
                                }
                            }
                        });

                // 存储完整响应到对话历史
                String completeResponse = fullResponse.toString();
                Map<String, String> assistantMessageMap = new HashMap<>();
                assistantMessageMap.put("role", "assistant");
                assistantMessageMap.put("content", completeResponse);
                conversationHistory.add(assistantMessageMap);

                // 通知完成
                onComplete.accept(completeResponse);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "流式聊天请求失败", e);
                String errorMessage = "连接到AI服务时出错：" + e.getMessage() + "\n\n请稍后再试，或检查您的网络连接。";
                onComplete.accept(errorMessage);
            }
        });
    }

    /**
     * 添加交易数据到消息内容
     */
    private void appendTransactionData(StringBuilder messageContent, List<Transaction> transactions) {
        if (transactions != null && !transactions.isEmpty()) {
            messageContent.append("\n\n以下是我的最近交易记录，请基于这些数据给我个性化的建议：\n");

            // 计算收入和支出
            double totalIncome = 0;
            double totalExpense = 0;
            Map<String, Double> categoryExpenses = new HashMap<>();

            for (Transaction t : transactions) {
                if (t.isIncome()) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();

                    String category = t.getCategory().toString();
                    categoryExpenses.putIfAbsent(category, 0.0);
                    categoryExpenses.put(category, categoryExpenses.get(category) + t.getAmount());
                }

                // 添加最近10条交易记录
                if (transactions.size() <= 10 || transactions.indexOf(t) >= transactions.size() - 10) {
                    messageContent.append("- 日期: ")
                            .append(t.getDate())
                            .append(", 金额: ")
                            .append(t.getAmount())
                            .append(", 类型: ")
                            .append(t.isIncome() ? "收入" : "支出")
                            .append(", 类别: ")
                            .append(t.getCategory())
                            .append(", 描述: ")
                            .append(t.getDescription())
                            .append("\n");
                }
            }

            // 添加收入支出统计
            messageContent.append("\n总收入: ").append(totalIncome)
                    .append("\n总支出: ").append(totalExpense)
                    .append("\n各类别支出:\n");

            for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
                messageContent.append("- ").append(entry.getKey())
                        .append(": ").append(entry.getValue())
                        .append("\n");
            }
        }
    }

    /**
     * 发送消息到DeepSeek API并获取回复（非流式，一次性获取完整回复）
     * 
     * @param userMessage  用户消息
     * @param transactions 用户交易记录（可选）
     * @return AI回复
     */
    public String chat(String userMessage, List<Transaction> transactions) {
        try {
            // 准备用户消息内容
            StringBuilder messageContent = new StringBuilder(userMessage);
            appendTransactionData(messageContent, transactions);

            // 创建用户消息
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", messageContent.toString());
            conversationHistory.add(userMessageMap);

            // 准备请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("messages", conversationHistory);
            requestBody.put("stream", false);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            // 发送API请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 检查响应状态
            if (response.statusCode() == 200) {
                // 解析响应
                Map<String, Object> responseData = objectMapper.readValue(
                        response.body(),
                        new TypeReference<Map<String, Object>>() {
                        });

                if (responseData.containsKey("choices") && responseData.get("choices") instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseData.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        if (firstChoice.containsKey("message") && firstChoice.get("message") instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                            if (message.containsKey("content") && message.get("content") instanceof String) {
                                String assistantResponse = (String) message.get("content");

                                // 添加到对话历史
                                Map<String, String> assistantMessageMap = new HashMap<>();
                                assistantMessageMap.put("role", "assistant");
                                assistantMessageMap.put("content", assistantResponse);
                                conversationHistory.add(assistantMessageMap);

                                return assistantResponse;
                            }
                        }
                    }
                }

                // 如果无法解析响应，返回错误消息
                return "无法解析API响应。";
            } else {
                // 处理API错误
                return "API错误：" + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "API请求失败", e);
            // 如果连接失败，使用后备方案（本地AI服务）
            return "连接到AI服务时出错：" + e.getMessage() + "\n\n替代回复：请稍后再试，或检查您的网络连接。";
        }
    }
}

