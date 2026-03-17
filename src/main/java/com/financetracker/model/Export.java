package com.financetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exports")
public class Export {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ExportFormat format;

    @Column(updatable = false)
    private LocalDateTime exportedAt = LocalDateTime.now();

    public enum ExportFormat { PDF, CSV }

    public Export() {}

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public ExportFormat getFormat() { return format; }
    public LocalDateTime getExportedAt() { return exportedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setFormat(ExportFormat format) { this.format = format; }
    public void setExportedAt(LocalDateTime exportedAt) { this.exportedAt = exportedAt; }

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Export e = new Export();
        public Builder user(User v) { e.user = v; return this; }
        public Builder format(ExportFormat v) { e.format = v; return this; }
        public Export build() { return e; }
    }
}
