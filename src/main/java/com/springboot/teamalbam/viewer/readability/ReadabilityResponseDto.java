package com.springboot.teamalbam.viewer.readability;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadabilityResponseDto {
    private String fileId;
    private String result;
}