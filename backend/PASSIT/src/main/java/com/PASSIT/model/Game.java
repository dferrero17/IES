package com.PASSIT.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Entity
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "flagLive")
    private boolean flagLive = false;

    @ManyToMany(mappedBy ="games_list")
    @JsonIgnoreProperties({"players_list", "games_list"})
    private List<Team> teams_list = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "game_id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIgnoreProperties({"game_id","player_id", "bpm", "speed", "breathing_rate", "ecg"})
    private List<StatsByGame> stats_list;

    public void addStatsByGame(StatsByGame stats) {
        this.stats_list.add(stats);
    }

    public boolean flagLive() {
        return flagLive;
    }

    public float avgBpm() {
        float sum = 0;
        for (StatsByGame stat : stats_list) {
            sum += stat.avgBpm();
        }
        return sum / stats_list.size();
    }

    public float avgBreathingRate() {
        float sum = 0;
        for (StatsByGame stat : stats_list) {
            sum += stat.avgBreathingRate();
        }
        return sum / stats_list.size();
    }

    public float avgSpeed() {
        float sum = 0;
        for (StatsByGame stat : stats_list) {
            sum += stat.avgSpeed();
        }
        return sum / stats_list.size();
    }

    public List<Player> players_list() {
        List<Player> players = new ArrayList<>();
        for (Team team: teams_list) {
            for (Player player: team.getPlayers_list())
                players.add(player);
        }
        return players;
    }

}

