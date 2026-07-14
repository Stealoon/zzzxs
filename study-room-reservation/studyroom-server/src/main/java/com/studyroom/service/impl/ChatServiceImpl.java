package com.studyroom.service.impl;

import com.studyroom.constant.RedisKeyConstant;
import com.studyroom.context.BaseContext;
import com.studyroom.properties.ChatProperties;
import com.studyroom.service.ChatService;
import com.studyroom.utils.HttpClientUtil;
import com.studyroom.vo.ChatReplyVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatProperties chatProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    // 系统预设Prompt
    private static final String SYSTEM_PROMPT = "你是共享自习室的智能客服，职责是解答用户关于自习室预约、套餐价格、开放时间、签到规则、退款规则、场馆设施的问题。\n\n回答要求：\n1. 回答简洁友好，口语化，不超过200字。\n2. 严格基于知识库内容回答，知识库没有的内容，直接引导用户联系人工客服，禁止编造信息。\n3. 不回答任何与自习室业务无关的问题，无关问题直接引导咨询业务相关问题。";

    // 知识库（内存存储）
    private List<KnowledgeItem> knowledgeBase = new ArrayList<>();

    @PostConstruct
    public void initKnowledgeBase() {
        // 基础信息
        knowledgeBase.add(new KnowledgeItem("基础信息", Arrays.asList("营业时间", "几点", "开门", "关门"), "场馆营业时间为每天7:00-22:00，全年无休。"));
        knowledgeBase.add(new KnowledgeItem("基础信息", Arrays.asList("地址", "位置", "在哪里", "怎么去"), "场馆位于XX路XX号，地铁XX站B出口步行5分钟即到。"));
        knowledgeBase.add(new KnowledgeItem("基础信息", Arrays.asList("电话", "联系方式", "客服"), "客服电话：400-XXX-XXXX，工作时间9:00-18:00。"));

        // 套餐规则
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("单日", "日卡", "畅学卡", "一天"), "单日畅学卡30元，当日有效，不限区域与座位，先到先得。"));
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("周卡", "7天", "一周"), "周卡150元，自购买之日起7天内有效，不限区域与座位，每日需重新预约。"));
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("月卡", "30天", "一个月"), "月卡500元，自购买之日起30天内有效，不限区域与座位，每日需重新预约。"));
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("2小时", "体验卡", "短时"), "2小时体验卡8元，适合短时学习体验。"));
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("4小时", "专注卡"), "4小时专注卡15元，适合半日专注学习。"));
        knowledgeBase.add(new KnowledgeItem("套餐规则", Arrays.asList("8小时", "全天卡"), "8小时全天卡25元，适合全天学习。"));

        // 预约规则
        knowledgeBase.add(new KnowledgeItem("预约规则", Arrays.asList("怎么预约", "如何预约", "预订", "下单"), "选择心仪的座位和时间段，提交预约并完成支付即可。支付成功后会生成签到码，到店后扫码签到即可使用。"));
        knowledgeBase.add(new KnowledgeItem("预约规则", Arrays.asList("提前", "多久", "提前预约"), "可提前7天预约座位。"));
        knowledgeBase.add(new KnowledgeItem("预约规则", Arrays.asList("时段冲突", "被占用", "重复预约"), "同一座位在同一时间段内不可重复预约。如果提示时段冲突，请选择其他时段或座位。"));
        knowledgeBase.add(new KnowledgeItem("预约规则", Arrays.asList("签到", "核销", "验证"), "支付成功后生成6位签到码。请在预约开始前15分钟至开始后30分钟内扫码签到。提前过早或迟到超过30分钟均无法签到。"));

        // 退款规则
        knowledgeBase.add(new KnowledgeItem("退款规则", Arrays.asList("退款", "退钱", "取消", "退"), "待支付状态的预约可直接取消，无需退款。已支付的预约在预约开始前可取消，系统将自动退款。预约开始后不可取消和退款。"));

        // 常见设施问题
        knowledgeBase.add(new KnowledgeItem("常见设施问题", Arrays.asList("插座", "电源", "充电"), "静音区、沉浸区大部分座位配有插座，靠窗区部分座位有插座，预约时可查看座位描述。"));
        knowledgeBase.add(new KnowledgeItem("常见设施问题", Arrays.asList("wifi", "WiFi", "无线网", "网络"), "全场覆盖免费WiFi，密码为场馆名称拼音首字母。"));
        knowledgeBase.add(new KnowledgeItem("常见设施问题", Arrays.asList("空调", "温度", "冷", "热"), "场馆配备中央空调，保持适宜温度。如有温度不适，请联系前台调节。"));
        knowledgeBase.add(new KnowledgeItem("常见设施问题", Arrays.asList("饮水", "喝水", "水", "饮料"), "场馆前台提供免费饮用水，也可自带水杯。一楼设有自动贩卖机。"));
        knowledgeBase.add(new KnowledgeItem("常见设施问题", Arrays.asList("卫生间", "厕所", "洗手间"), "每层均设有卫生间，位于走廊尽头。"));

        log.info("智能客服知识库加载完成，共{}条记录", knowledgeBase.size());
    }

    @Override
    public ChatReplyVO chat(String message) {
        if (!chatProperties.isEnabled()) {
            return ChatReplyVO.builder().reply("智能客服功能暂未开启，请联系人工客服。").build();
        }

        // 1. RAG知识库检索
        String matchedKnowledge = retrieveKnowledge(message);

        // 2. 获取历史上下文
        Long userId = BaseContext.getCurrentId();
        String contextKey = RedisKeyConstant.CHAT_CONTEXT_PREFIX + userId;
        List<JSONObject> history = getHistoryContext(contextKey);

        // 3. 构建请求
        String reply;
        try {
            reply = callLlmApi(message, matchedKnowledge, history);
        } catch (Exception e) {
            log.error("调用大模型API失败：{}", e.getMessage());
            reply = "抱歉，智能客服暂时无法回复，请稍后重试或联系人工客服。";
        }

        // 4. 更新上下文
        updateHistoryContext(contextKey, message, reply);

        return ChatReplyVO.builder().reply(reply).build();
    }

    /**
     * RAG知识库检索
     */
    private String retrieveKnowledge(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "";
        }

        List<KnowledgeItem> matched = new ArrayList<>();
        for (KnowledgeItem item : knowledgeBase) {
            for (String keyword : item.keywords) {
                if (message.contains(keyword)) {
                    matched.add(item);
                    break;
                }
            }
        }

        if (matched.isEmpty()) {
            return "";
        }

        // 取匹配度最高的3条
        StringBuilder sb = new StringBuilder("以下是知识库中的相关信息，请基于这些内容回答用户问题：\n\n");
        int limit = Math.min(matched.size(), 3);
        for (int i = 0; i < limit; i++) {
            sb.append(i + 1).append(". ").append(matched.get(i).content).append("\n");
        }
        return sb.toString();
    }

    /**
     * 获取历史上下文
     */
    @SuppressWarnings("unchecked")
    private List<JSONObject> getHistoryContext(String contextKey) {
        Object obj = redisTemplate.opsForValue().get(contextKey);
        if (obj == null) {
            return new ArrayList<>();
        }
        try {
            if (obj instanceof String) {
                return JSON.parseArray((String) obj).toJavaList(JSONObject.class);
            }
            return (List<JSONObject>) obj;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 更新历史上下文
     */
    private void updateHistoryContext(String contextKey, String userMessage, String reply) {
        List<JSONObject> history = getHistoryContext(contextKey);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        history.add(userMsg);

        JSONObject assistantMsg = new JSONObject();
        assistantMsg.put("role", "assistant");
        assistantMsg.put("content", reply);
        history.add(assistantMsg);

        // 只保留最近5轮（10条消息）
        int maxMessages = chatProperties.getContextMaxRounds() * 2;
        while (history.size() > maxMessages) {
            history.remove(0);
        }

        redisTemplate.opsForValue().set(contextKey, JSON.toJSONString(history),
                Duration.ofMinutes(chatProperties.getContextTtlMinutes()));
    }

    /**
     * 调用大模型API
     */
    private String callLlmApi(String message, String knowledge, List<JSONObject> history) {
        // 构建消息列表
        JSONArray messages = new JSONArray();

        // 系统Prompt
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        String systemContent = SYSTEM_PROMPT;
        if (knowledge != null && !knowledge.isEmpty()) {
            systemContent = systemContent + "\n\n" + knowledge;
        }
        systemMsg.put("content", systemContent);
        messages.add(systemMsg);

        // 历史上下文
        for (JSONObject h : history) {
            messages.add(h);
        }

        // 当前用户消息
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        messages.add(userMsg);

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", chatProperties.getModel());
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        try {
            String response = HttpClientUtil.doPost4Json(
                    chatProperties.getBaseUrl() + "/chat/completions",
                    requestBody.toJSONString(),
                    chatProperties.getApiKey());
            JSONObject respJson = JSON.parseObject(response);
            JSONArray choices = respJson.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content");
            }
            return "抱歉，我暂时无法理解您的问题，请联系人工客服。";
        } catch (Exception e) {
            log.error("大模型API调用异常：{}", e.getMessage());
            throw new RuntimeException("大模型API调用失败", e);
        }
    }

    // 知识库条目内部类
    private static class KnowledgeItem {
        String category;
        List<String> keywords;
        String content;

        KnowledgeItem(String category, List<String> keywords, String content) {
            this.category = category;
            this.keywords = keywords;
            this.content = content;
        }
    }
}
