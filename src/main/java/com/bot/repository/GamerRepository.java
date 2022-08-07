package com.bot.repository;

import com.bot.models.Gamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, Long> {

    @Query("Select gamer from Gamer gamer where gamer.gamer_id = :gamerId")
    Gamer getGamer(Long gamerId);
    @Query("Select count(g) from Gamer g where g.gamer_chatId = :chatId")
    long getCountOfGamers(Long chatId);

    @Transactional
    @Modifying
    @Query("Update Gamer g set g.score = g.score + :number where g.gamer_id <> :gamerId and g.gamer_chatId = :chatId")
    void addNumberToEveryOne(long number, Long gamerId, long chatId);

    @Query("From Gamer gamer where gamer.gamer_chatId = :chatId order by gamer.score desc")
    List<Gamer> getTopGamers(Long chatId);
}
