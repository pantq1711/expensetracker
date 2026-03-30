package com.anphan.expensetracker.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteDTO {
    @Size(max = 2000)
    private String content;

    private boolean pinned;


    @NotBlank(message = "Tieu de khong duoc de trong")
    private String title;

    private Long id;



}
