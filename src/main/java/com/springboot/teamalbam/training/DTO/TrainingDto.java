package com.springboot.teamalbam.training.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class TrainingDto {
    private Long id;
    private int level;
    private String content;
    private int answer;
    private String explanation;
    private String imageUrl;
    private List<OptionDto> options;

    public TrainingDto(Long id, int level, String content, int answer, String explanation, String imageUrl, List<OptionDto> options) {
        this.id = id;
        this.level = level;
        this.content = content;
        this.answer = answer;
        this.explanation = explanation;
        this.imageUrl = imageUrl;
        this.options = options;
    }
}
