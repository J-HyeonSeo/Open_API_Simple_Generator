package com.jhsfully.api.service.impl;

import com.jhsfully.api.model.dto.GradeDto;
import com.jhsfully.api.service.GradeService;
import com.jhsfully.domain.repository.GradeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    @Override
    public List<GradeDto> getGradeList() {
        return gradeRepository.findAll()
            .stream()
            .map(GradeDto::of)
            .collect(Collectors.toList());
    }

}
