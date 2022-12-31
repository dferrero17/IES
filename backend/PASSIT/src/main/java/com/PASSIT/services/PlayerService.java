package com.PASSIT.services;

import com.PASSIT.model.Player;
import com.PASSIT.model.StatsByGame;
import com.PASSIT.repository.PlayerRepository;
import com.PASSIT.repository.StatsByGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final StatsByGameRepository statsByGameRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, StatsByGameRepository statsByGameRepository) {
        this.playerRepository = playerRepository;
        this.statsByGameRepository = statsByGameRepository;
    }

    public Player savePlayer(Player player) {
        player.getTeam_id().getPlayers_list().add(player);
        return playerRepository.save(player);
    }

    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerById(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public void remStamina(Long id, Double stamina) {
        StatsByGame statsByGame = statsByGameRepository.findById(id).get();
        Player player = statsByGame.getPlayer_id();
        if (statsByGame.getGame_id().getDate().compareTo(this.getLastGame(player.getId())) > 0) {
            player.setStamina(stamina);
            playerRepository.save(player);
        }
    }

    public Date getLastGame(Long id) {
        Date date = new Date(0);
        for (StatsByGame statsByGame : statsByGameRepository.findAll()) {
            if (statsByGame.getPlayer_id().getId() == id) {
                if (date.compareTo(statsByGame.getGame_id().getDate()) < 0)
                    date = statsByGame.getGame_id().getDate();
            }
        }
        return date;
    }

    public Map<String, Double> getStatsUserGame(Long id, Long game_id) {
        Map<String, Double> stats = new HashMap<>();
        for (StatsByGame statsByGame : statsByGameRepository.findAll()) {
            if (statsByGame.getPlayer_id().getId() == id && statsByGame.getGame_id().getId() == game_id) {
                stats.put("speed", statsByGame.getAvgSpeed());
                stats.put("breathing_rate", statsByGame.getAvgBreathingRate());
                stats.put("bpm", statsByGame.getAvgBpm());
            }
        }
        return stats;
    }

    public Map<String, Double> getStatsUserGame(Long id) {
        Map<String, Double> stats = new HashMap<>();
        StatsByGame statsByGame = statsByGameRepository.findById(id).get();
        stats.put("speed", statsByGame.getAvgSpeed());
        stats.put("breathing_rate", statsByGame.getAvgBreathingRate());
        stats.put("bpm", statsByGame.getAvgBpm());
        return stats;
    }
    


}
