package com.demo.lesson20.controller;

import org.springframework.ai.mistralai.moderation.MistralAiModerationModel;
import org.springframework.ai.mistralai.moderation.MistralAiModerationOptions;
import org.springframework.ai.moderation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModerationController {

    private final MistralAiModerationModel mistralAiModerationModel;

    @Autowired
    public ModerationController(MistralAiModerationModel mistralAiModerationModel) {
        this.mistralAiModerationModel = mistralAiModerationModel;
    }

    @GetMapping("/ai/moderation")
    public void moderation(@RequestParam(value = "message", defaultValue = "这个事情只有男的可以做，女的做不到！") String message) {
        // 说明一下，这个message只是为了测试效果，并不代表作者任何观点
        MistralAiModerationOptions moderationOptions = MistralAiModerationOptions.builder()
                .model("mistral-moderation-latest")
                .build();

        ModerationPrompt moderationPrompt = new ModerationPrompt(message, moderationOptions);
        ModerationResponse moderationResponse = mistralAiModerationModel.call(moderationPrompt);

        // 获取审核结果
        Moderation moderation = moderationResponse.getResult().getOutput();

        // Access the moderation results (there's usually only one, but it's a list)
        for (ModerationResult result : moderation.getResults()) {
            System.out.println("最终结果:");
            System.out.println("Flagged: " + result.isFlagged());

            // Access categories
            Categories categories = result.getCategories();
            System.out.println("\n类型，true表示包含:");
            // 触发法律
            System.out.println("Law: " + categories.isLaw());
            // 触发金融限制
            System.out.println("Financial: " + categories.isFinancial());
            // 包括个人信息
            System.out.println("PII: " + categories.isPii());
            // 包括色情
            System.out.println("Sexual: " + categories.isSexual());
            // 包括偏见、敌意
            System.out.println("Hate: " + categories.isHate());
            // 包括骚扰信息
            System.out.println("Harassment: " + categories.isHarassment());
            // 包括宣传、鼓励自杀自残等
            System.out.println("Self-Harm: " + categories.isSelfHarm());
            // 包括未成年人色情
            System.out.println("Sexual/Minors: " + categories.isSexualMinors());
            // 包括偏见、敌意的恐吓等
            System.out.println("Hate/Threatening: " + categories.isHateThreatening());
            // 包括暴力血腥层面
            System.out.println("Violence/Graphic: " + categories.isViolenceGraphic());
            // 包括自杀自残倾向
            System.out.println("Self-Harm/Intent: " + categories.isSelfHarmIntent());
            // 包括引导自杀自残
            System.out.println("Self-Harm/Instructions: " + categories.isSelfHarmInstructions());
            // 包括自杀自残恐吓
            System.out.println("Harassment/Threatening: " + categories.isHarassmentThreatening());
            // 包括暴力
            System.out.println("Violence: " + categories.isViolence());

            // Access category scores
            CategoryScores scores = result.getCategoryScores();
            System.out.println("\n以下个各种类型分数（分数越高代表可能性越高:");
            System.out.println("Law: " + scores.getLaw());
            System.out.println("Financial: " + scores.getFinancial());
            System.out.println("PII: " + scores.getPii());
            System.out.println("Sexual: " + scores.getSexual());
            System.out.println("Hate: " + scores.getHate());
            System.out.println("Harassment: " + scores.getHarassment());
            System.out.println("Self-Harm: " + scores.getSelfHarm());
            System.out.println("Sexual/Minors: " + scores.getSexualMinors());
            System.out.println("Hate/Threatening: " + scores.getHateThreatening());
            System.out.println("Violence/Graphic: " + scores.getViolenceGraphic());
            System.out.println("Self-Harm/Intent: " + scores.getSelfHarmIntent());
            System.out.println("Self-Harm/Instructions: " + scores.getSelfHarmInstructions());
            System.out.println("Harassment/Threatening: " + scores.getHarassmentThreatening());
            System.out.println("Violence: " + scores.getViolence());
        }
    }
}
