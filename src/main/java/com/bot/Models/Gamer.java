package com.bot.Models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "table_gamers")
public class Gamer {
    private Long id;
    private Long score;
    private String gamer_username;
    private Long gamer_chatId;
    private Long gamer_id;
    private Boolean gamer_isArrested;
    private Long gamer_arrestedTime;
    private Long gamer_lastTimeSmoked;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "gamer_score")
    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    @Column(name = "gamer_username")
    public String getGamer_username() {
        return gamer_username;
    }

    public void setGamer_username(String gamer_username) {
        this.gamer_username = gamer_username;
    }

    @Column(name = "gamer_is_arrested")
    public Boolean getGamer_isArrested() {
        return gamer_isArrested;
    }

    public void setGamer_isArrested(Boolean gamer_isArrested) {
        this.gamer_isArrested = gamer_isArrested;
    }


    @Column(name = "gamer_arrested_time")
    public Long getGamer_arrestedTime() {
        return gamer_arrestedTime;
    }

    public void setGamer_arrestedTime(Long gamer_arrestedTime) {
        this.gamer_arrestedTime = gamer_arrestedTime;
    }

    @Column(name = "gamer_last_time_smoked")
    public Long getGamer_lastTimeSmoked() {
        return gamer_lastTimeSmoked;
    }

    public void setGamer_lastTimeSmoked(Long gamer_lastTimeSmoked) {
        this.gamer_lastTimeSmoked = gamer_lastTimeSmoked;
    }

    @Column(name = "gamer_chat_id")
    public Long getGamer_chatId() {
        return gamer_chatId;
    }

    public void setGamer_chatId(Long gamer_chatId) {
        this.gamer_chatId = gamer_chatId;
    }

    @Column(name = "gamer_id")
    public Long getGamer_id() {
        return gamer_id;
    }

    public void setGamer_id(Long gamer_id) {
        this.gamer_id = gamer_id;
    }

    @Override
    public String toString() {
        return "Gamer{" +
                "id=" + id +
                ", gamer_username='" + gamer_username + '\'' +
                ", gamer_isArrested=" + gamer_isArrested +
                ", gamer_arrestedTime=" + gamer_arrestedTime +
                ", gamer_lastTimeSmoked=" + gamer_lastTimeSmoked +
                '}';
    }
}
