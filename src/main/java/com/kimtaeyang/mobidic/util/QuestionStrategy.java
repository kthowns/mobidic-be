package com.kimtaeyang.mobidic.util;

import com.kimtaeyang.mobidic.dto.QuestionRateDto;
import com.kimtaeyang.mobidic.entity.Question;
import com.kimtaeyang.mobidic.model.WordWithDefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

abstract public class QuestionStrategy {
    abstract public List<Question> generateQuestions(UUID memberId, List<WordWithDefs> wordsWithDefs);

    protected static <T> void partialShuffle(int n, List<T> list) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            nums.add(i);
        }
        derange(nums);

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indices.add(nums.get(i));
        }

        ArrayList<T> selectedValues = new ArrayList<>();
        for (Integer idx : indices) {
            selectedValues.add(list.get(idx));
        }
        derange(selectedValues);

        for (int i = 0; i < indices.size(); i++) {
            list.set(indices.get(i), selectedValues.get(i));
        }
    }

    protected static <T> void derange(List<T> list) {
        if (list == null || list.size() < 2) {
            return;
        }

        for (T item : list) {
            if (item == null) {
                return;
            }
        }

        List<T> orgList = new ArrayList<>(list);

        int epoch = 30;
        int cnt = 0;
        while (true) { //complete derangement
            if (epoch < cnt) {
                return;
            }
            Collections.shuffle(list);
            boolean isDerangement = true;
            cnt++;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(orgList.get(i))) {
                    isDerangement = false;
                    break;
                }
            }
            if (isDerangement) {
                return;
            }
        }
    }
}
