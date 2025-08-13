package com.springboot.teamalbam.training.DTO;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@ToString

public class OptionDto {
    private int testNumber;
    private String optionContent;

    public OptionDto(int testNumber, String optionContent) {
        this.testNumber = testNumber;
        this.optionContent = optionContent;
    }
}
