package com.bot.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "table_gamers")
public class Gamer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gamer_score")
    private Long score;

    @Column(name = "gamer_username")
    private String gamer_username;

    @Column(name = "gamer_chat_id")
    private Long gamer_chatId;

    @Column(name = "gamer_id")
    private Long gamer_id;

    @Column(name = "gamer_is_arrested")
    private Boolean gamer_isArrested;

    @Column(name = "gamer_arrested_time")
    private Long gamer_arrestedTime;

    @Column(name = "gamer_last_time_smoked")
    private Long gamer_lastTimeSmoked;
}
