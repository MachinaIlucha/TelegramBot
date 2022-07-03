package com.bot.Repositories;

import com.bot.Models.Gamer;
import com.pengrad.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class GamerRepository{

    private final String SQL_gamerExist = "from Gamer where gamer_id = :gamerId";
    private final String SQL_countGamers = "Select count(*) from Gamer where gamer_chatId = :chatId";
    private final String SQL_addNumberToEveryone = "update Gamer gamer set gamer.score = gamer.score + :number" +
            " where gamer.gamer_id != :gamerId and gamer.gamer_chatId = :chatId";
    private final String SQL_getTopGamers = "from Gamer gamer where gamer.gamer_chatId = :chatId order by gamer.score desc";

    @PersistenceContext
    private EntityManager entityManager;

    public void save(User user, Long chatId){
        Gamer gamer = new Gamer();
        gamer.setScore(0L);
        if (user.username() != null)
            gamer.setGamer_username(user.username());
        gamer.setGamer_chatId(chatId);
        gamer.setGamer_id(user.id());
        gamer.setGamer_isArrested(false);
        gamer.setGamer_lastTimeSmoked(null);
        gamer.setGamer_arrestedTime(null);

        try {
            entityManager.persist(gamer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Gamer gamer){
        try {
            entityManager.merge(gamer);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Gamer getGamerById(Long gamerId){
        Query query = entityManager.createQuery(SQL_gamerExist);
        query.setParameter("gamerId", gamerId);
        Gamer gamer;
        try {
            gamer = (Gamer) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return gamer;
    }

    public boolean checkIfGamerAlreadyExist(Long gamerId){
        Query query = entityManager.createQuery(SQL_gamerExist);
        query.setParameter("gamerId", gamerId);
        try {
            query.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public long getCountOfGamers(Long chatId){
        Query query = entityManager.createQuery(SQL_countGamers);
        query.setParameter("chatId", chatId);
        try {
            return (Long) query.getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public void addNumberToEveryOne(long number, Long gamerId, long chatId){
        Query query = entityManager.createQuery(SQL_addNumberToEveryone);
        query.setParameter("number", number);
        query.setParameter("gamerId", gamerId);
        query.setParameter("chatId", chatId);
        try {
            query.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Gamer> getTopGamers(Long chatId){
        Query query = entityManager.createQuery(SQL_getTopGamers);
        query.setParameter("chatId", chatId);
        try {
            return query.getResultList();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
