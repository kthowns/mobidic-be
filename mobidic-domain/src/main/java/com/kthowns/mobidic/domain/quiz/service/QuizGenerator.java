package com.kthowns.mobidic.domain.quiz.service;

import com.kthowns.mobidic.domain.quiz.model.Quiz;
import com.kthowns.mobidic.domain.word.model.WordDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

abstract class QuizGenerator {
    public abstract List<Quiz> generate(UUID memberId, List<WordDetail> wordDetails);

    protected <T> void partialShuffle(int n, List<T> list) {
        if (list == null || list.size() < 2 || n < 2) {
            return;
        }

        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);

        int shuffleSize = Math.min(n, list.size());
        List<Integer> indices = nums.subList(0, shuffleSize);

        List<T> selectedValues = new ArrayList<>();
        for (Integer idx : indices) {
            selectedValues.add(list.get(idx));
        }

        if (derange(selectedValues)) {
            for (int i = 0; i < indices.size(); i++) {
                list.set(indices.get(i), selectedValues.get(i));
            }
        }
    }

    protected <T> boolean derange(List<T> list) {
        if (list == null || list.size() < 2) {
            return false;
        }

        for (T item : list) {
            if (item == null) {
                return false;
            }
        }

        // Sattolo's algorithm: Generates a random cyclic permutation (guaranteed derangement) in O(n)
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = list.size() - 1; i > 0; i--) {
            int j = rnd.nextInt(i);
            Collections.swap(list, i, j);
        }

        return true;
    }
}
