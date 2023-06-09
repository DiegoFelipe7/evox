package com.evox.evox.dto;

import com.evox.evox.model.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupportDto {
    private Integer id;
    private String category;
    private String question;
    private String answer;
    private String username;
    private String email;
    private String urlPhoto;
    private State state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportDto(Integer id, String category, String question, String answer, String urlPhoto, State state, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.question = question;
        this.answer = answer;
        this.urlPhoto = urlPhoto;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
