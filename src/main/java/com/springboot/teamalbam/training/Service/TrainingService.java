package com.springboot.teamalbam.training.Service;

import com.springboot.teamalbam.training.DTO.OptionDto;
import com.springboot.teamalbam.training.DTO.TrainingDto;
import com.springboot.teamalbam.training.Entity.Option;
import com.springboot.teamalbam.training.Entity.Training;
import com.springboot.teamalbam.training.Repository.OptionRepository;
import com.springboot.teamalbam.training.Repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingService {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private OptionRepository optionRepository;

    public List<TrainingDto> loadAllTestsWithOptions() {
        List<Training> trainings = trainingRepository.findAll();

        return trainings.stream().map(test -> {
            List<Option> options = optionRepository.findByTrainingId(test.getId());
            List<OptionDto> optionDtos = options.stream()
                    .map(o -> new OptionDto(o.getTestNumber(), o.getOptionContent()))
                    .collect(Collectors.toList());

            return new TrainingDto(
                    test.getId(),
                    test.getLevel(),
                    test.getContent(),
                    test.getAnswer(),
                    test.getExplanation(),
                    test.getImageUrl(),
                    optionDtos
            );
        }).collect(Collectors.toList());
    }
}
