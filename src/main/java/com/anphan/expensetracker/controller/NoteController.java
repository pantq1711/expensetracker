package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.NoteDTO;
import com.anphan.expensetracker.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "APIs for managing personal notes")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "Get list of notes (Paginated)", description = "Retrieves notes of the current user, sorted by creation date descending.")
    @GetMapping
    public ResponseEntity<Page<NoteDTO>> getNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(noteService.getNotes(pageable));
    }

    @Operation(summary = "Get note details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Long id){
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @Operation(summary = "Create a new note")
    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@RequestBody NoteDTO noteDTO){
        return ResponseEntity.status(201).body(noteService.createNote(noteDTO));
    }

    @Operation(summary = "Update note by ID")
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @Valid @RequestBody NoteDTO dto){
        return ResponseEntity.ok(noteService.updateNote(id, dto));
    }

    @Operation(summary = "Delete note by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}