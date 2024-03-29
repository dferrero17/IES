package com.PASSIT.services;

import com.PASSIT.model.Player;
import com.PASSIT.model.StatsByGame;
import com.PASSIT.model.Team;
import com.PASSIT.repository.GameRepository;
import com.PASSIT.repository.PlayerRepository;
import com.PASSIT.repository.StatsByGameRepository;
import com.PASSIT.repository.TeamRepository;

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
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, StatsByGameRepository statsByGameRepository, TeamRepository teamRepository,
                         GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.statsByGameRepository = statsByGameRepository;
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
    }

    public Player savePlayer(Player player) {
        Team team = teamRepository.findById(player.getTeam_id().getId()).orElse(null);
        team.addPlayer(player);
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
        System.out.println("Stamina not updated Sucefully");
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

    public Map<String, Float> getStatsUserGame(Long id, Long game_id) {
        Map<String, Float> stats = new HashMap<>();
        for (StatsByGame statsByGame : statsByGameRepository.findAll()) {
            if (statsByGame.getPlayer_id().getId() == id && statsByGame.getGame_id().getId() == game_id) {
                stats.put("speed", statsByGame.avgSpeed());
                stats.put("breathing_rate", statsByGame.avgBreathingRate());
                stats.put("bpm", statsByGame.avgBpm());
            }
        }
        return stats;
    }

    public Map<String, Float> getStatsUserGame(Long id) {
        Map<String, Float> stats = new HashMap<>();
        StatsByGame statsByGame = statsByGameRepository.findById(id).get();
        stats.put("speed", statsByGame.avgSpeed());
        stats.put("breathing_rate", statsByGame.avgBreathingRate());
        stats.put("bpm", statsByGame.avgBpm());
        return stats;
    }

    public boolean login(String username, String password) {
        for (Player player : playerRepository.findAll()) {
            if (player.getUsername().equals(username) && player.getPassword().equals(password))
                return true;
        }
        return false;
    }

}
