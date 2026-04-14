package com.zhongbei.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "words")
public class Word {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String word;
    
    @Column(nullable = false)
    private String pronunciation;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning;
    
    @Column(columnDefinition = "TEXT")
    private String example;
    
    @Column(columnDefinition = "TEXT")
    private String exampleTranslation;
    
    @Column(name = "word_type")
    private String wordType;
    
    @Column(name = "is_learned")
    private Boolean isLearned = false;
    
    @Column(name = "learn_count")
    private Integer learnCount = 0;
    
    @Column(name = "wrong_count")
    private Integer wrongCount = 0;
    
    @Column(name = "last_learned")
    private LocalDateTime lastLearned;
    
    @PrePersist
    protected void onCreate() {
        lastLearned = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWord() {
        return word;
    }
    
    public void setWord(String word) {
        this.word = word;
    }
    
    public String getPronunciation() {
        return pronunciation;
    }
    
    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public String getExample() {
        return example;
    }
    
    public void setExample(String example) {
        this.example = example;
    }
    
    public String getExampleTranslation() {
        return exampleTranslation;
    }
    
    public void setExampleTranslation(String exampleTranslation) {
        this.exampleTranslation = exampleTranslation;
    }
    
    public String getWordType() {
        return wordType;
    }
    
    public void setWordType(String wordType) {
        this.wordType = wordType;
    }
    
    public Boolean getIsLearned() {
        return isLearned;
    }
    
    public void setIsLearned(Boolean isLearned) {
        this.isLearned = isLearned;
    }
    
    public Integer getLearnCount() {
        return learnCount;
    }
    
    public void setLearnCount(Integer learnCount) {
        this.learnCount = learnCount;
    }
    
    public Integer getWrongCount() {
        return wrongCount;
    }
    
    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
    
    public LocalDateTime getLastLearned() {
        return lastLearned;
    }
    
    public void setLastLearned(LocalDateTime lastLearned) {
        this.lastLearned = lastLearned;
    }
}
