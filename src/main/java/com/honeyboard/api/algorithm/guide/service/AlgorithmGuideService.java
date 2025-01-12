package com.honeyboard.api.algorithm.guide.service;

import com.honeyboard.api.algorithm.guide.model.AlgorithmGuide;

import java.util.List;

import org.springframework.stereotype.Service;

public interface AlgorithmGuideService {

    //알고리즘 개념 전체 조회 (GET /api/v1/algorithm/guide?generation={generationId}) 구현
    List<AlgorithmGuide> getAlgorithmGuides(int generationId);

    //알고리즘 개념 상세 조회 (GET /api/v1/algorithm/guide/{id}/bookmark/{bookmarkflag}) 구현
    AlgorithmGuide getAlgorithmGuideDetail (int id, boolean bookmarkflag);

    //알고리즘 개념 검색 (GET /api/v1/algorithm/guide?generation={generationId}&title={title}) 구현
    List<AlgorithmGuide> searchAlgorithmGuide(int generationId, String title);

    //알고리즘 개념 작성 (POST /api/v1/algorithm/guide?generation={generationId}) 구현
    boolean addAlgorithmGuide (int generationId, AlgorithmGuide algorithmGuide);

    //알고리즘 개념 수정 (PUT /api/v1/algorithm/guide/{id}) 구현
    boolean updateAlgorithmGuide(int id, AlgorithmGuide algorithmGuide);

    //알고리즘 개념 삭제 (DELETE /api/v1/algorithm/guide/{id}) 구현
    boolean deleteAlgorithmGuide(int id);
}
