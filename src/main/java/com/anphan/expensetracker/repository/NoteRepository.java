package com.anphan.expensetracker.repository;

import com.anphan.expensetracker.entity.Note;
import com.anphan.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    public Page<Note> findByUser(User user, Pageable pageable);

    public List<Note> findAllByUser (User user);
}
