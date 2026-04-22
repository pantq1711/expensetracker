package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.NoteDTO;
import com.anphan.expensetracker.entity.Note;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

    private Note getNoteAndCheckOwnership(Long id){
        Note note = noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ghi chú: " + id));

        User currentUser = getCurrentUser();

        if(!note.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You don't have permission to access!");
        }
        return note;
    }

    public Page<NoteDTO> getNotes(Pageable pageable){
        return noteRepository.findByUser(getCurrentUser(), pageable).map(this :: convertToDTO);
    }

    public NoteDTO getNoteById(Long id){
        return convertToDTO(getNoteAndCheckOwnership(id));
    }
    public NoteDTO createNote(NoteDTO dto){
        Note note = new Note();
        note.setContent(dto.getContent());
        note.setPinned(dto.isPinned());
        note.setTitle(dto.getTitle());
        note.setUser(getCurrentUser());
        noteRepository.save(note);
        return convertToDTO(note);
    }

    public NoteDTO updateNote(Long id, NoteDTO dto){
        Note note = getNoteAndCheckOwnership(id);
        note.setContent(dto.getContent());
        note.setPinned(dto.isPinned());
        note.setTitle(dto.getTitle());
        noteRepository.save(note);
        return convertToDTO(note);
    }

    public void deleteNote(Long id){
        noteRepository.delete(getNoteAndCheckOwnership(id));
    }

    private User getCurrentUser(){
        return securityUtils.getCurrentUser();
    }

    private NoteDTO convertToDTO(Note note){
        NoteDTO dto = new NoteDTO();
        dto.setContent(note.getContent());
        dto.setPinned(note.isPinned());
        dto.setTitle(note.getTitle());
        dto.setId(note.getId());
        return dto;
    }
}
