package com.anphan.expensetracker.dto;
import lombok.Data;

@Data
public class NoteDTO {
    private String content;

    private boolean pinned;

    private String title;

    private Long id;



}
