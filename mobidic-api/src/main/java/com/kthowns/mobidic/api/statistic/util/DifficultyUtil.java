package com.kthowns.mobidic.api.statistic.util;

import org.springframework.stereotype.Component;

@Component
public class DifficultyUtil {
    public double calcAccuracyRatio(Long correct, Long incorrect) {
        long total = correct + incorrect;
        if (total == 0) return 0.0; // 또는 0.5 등 초기 기본값 설정
        return (double) correct / total;
    }

    public double calcDifficultyRatio(Long correct, Long incorrect) {
        /*
            난이도 함수 : -0.045correct + 0.055incorrect + 0.5
        */
        double diff = (-0.045 * correct) + (0.055 * incorrect) + 0.5;
        if (diff > 1) {
            diff = 1;
        } else if (diff < 0) {
            diff = 0;
        }

        return diff;
    }
}
