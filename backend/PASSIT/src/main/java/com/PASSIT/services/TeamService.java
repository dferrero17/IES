package com.PASSIT.services;

import com.PASSIT.model.*;
import com.PASSIT.repository.GameRepository;
import com.PASSIT.repository.StatsByGameRepository;
import com.PASSIT.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, GameRepository gameRepository) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
    }

    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(Long id) {
        return teamRepository.findById(id).get();
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Player> getPlayers(Long id) {
        return teamRepository.findById(id).get().getPlayers_list();
    }

    public Team findById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public Map<String,Double> statsTeamGame(Long id, Long game_id) {
        Game game = gameRepository.findById(game_id).get();
        Map<String, Double> statsMap = new HashMap<>();
        statsMap.put("bpm", 0.0);
        statsMap.put("speed", 0.0);
        statsMap.put("breathing_rate", 0.0);
        List<Player> players_list = teamRepository.findById(id).get().getPlayers_list();
        for (Player player : players_list) {
            StatsByGame statsByGame = player.getStatsByGame(game);
            statsMap.put("bpm", statsMap.get("bpm") + statsByGame.avgBpm());
            statsMap.put("speed", statsMap.get("speed") + statsByGame.avgSpeed());
            statsMap.put("breathing_rate", statsMap.get("breathing_rate") + statsByGame.avgBreathingRate());
        }
        System.out.println(statsMap);
        statsMap.put("bpm", statsMap.get("bpm") / players_list.size());
        statsMap.put("speed", statsMap.get("speed") / players_list.size());
        statsMap.put("breathing_rate", statsMap.get("breathing_rate") / players_list.size());
        return statsMap;
    }

    public Player highestPlayerByStat(Long game_id, String stat) {
        List<Player> players = teamRepository.findById(game_id).get().getPlayers_list();
        Player highestPlayer = players.get(0);
        float highestStat = 0;
        for(Player player : players) {
            StatsByGame statsByGame = player.getStatsByGame(gameRepository.findById(game_id).get());
            switch (stat){
                case "bpm":
                    if(statsByGame.avgBpm() > highestStat) {
                        highestPlayer = player;
                        highestStat = statsByGame.avgBpm();
                    }
                    break;
                case "speed":
                    if(statsByGame.avgSpeed() > highestStat) {
                        highestPlayer = player;
                        highestStat = statsByGame.avgSpeed();
                    }
                    break;
                case "breathing_rate":
                    if(statsByGame.avgBreathingRate() > highestStat) {
                        highestPlayer = player;
                        highestStat = statsByGame.avgBreathingRate();
                    }
                    break;
                default:
                    System.out.println("Invalid stat");
                    break;
            }
        }
        return highestPlayer;
    }

}
