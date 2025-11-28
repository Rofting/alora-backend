package com.alora.carelog.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCareNoteDto {
    @NotEmpty(message = "The note text cannot be empty.")
    @Size(max = 1000, message = "The note text cannot exceed 1000 characters.")
    private String text;
}