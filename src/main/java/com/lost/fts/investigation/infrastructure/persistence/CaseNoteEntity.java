package com.lost.fts.investigation.infrastructure.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "casenotes", indexes = {
        @Index(name = "idxcasenotescasecreated", columnList = "caseid, createdat")
})
public class CaseNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caseid", nullable = false, foreignKey = @ForeignKey(name = "fkcasenotescase"))
    private CaseEntity investigationCase;

    @Column(name = "noteref", nullable = false, unique = true, length = 64)
    private String noteRef;

    @Column(name = "authorid", nullable = false, length = 100)
    private String authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CaseNoteVisibility visibility = CaseNoteVisibility.INTERNAL;

    @Column(name = "notetext", nullable = false, columnDefinition = "TEXT")
    private String noteText;

    @Column(name = "createdat", nullable = false, updatable = false)
    private Instant createdAt;

    protected CaseNoteEntity() {
        // Required by JPA
    }

    public CaseNoteEntity(
            CaseEntity investigationCase,
            String noteRef,
            String authorId,
            CaseNoteVisibility visibility,
            String noteText,
            Instant createdAt) {

        if (investigationCase == null) {
            throw new IllegalArgumentException("Case is required");
        }

        if (noteRef == null || noteRef.isBlank()
                || authorId == null || authorId.isBlank()
                || noteText == null || noteText.isBlank()
                || visibility == null || createdAt == null) {
            throw new IllegalArgumentException(
                    "Note reference, author, visibility, text and timestamp are required");
        }

        this.investigationCase = investigationCase;
        this.noteRef = noteRef.trim();
        this.authorId = authorId.trim();
        this.visibility = visibility;
        this.noteText = noteText.trim();
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaseEntity getInvestigationCase() {
        return investigationCase;
    }

    public void setInvestigationCase(CaseEntity investigationCase) {
        this.investigationCase = investigationCase;
    }

    public String getNoteRef() {
        return noteRef;
    }

    public void setNoteRef(String noteRef) {
        this.noteRef = noteRef;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public CaseNoteVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CaseNoteVisibility visibility) {
        this.visibility = visibility;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
