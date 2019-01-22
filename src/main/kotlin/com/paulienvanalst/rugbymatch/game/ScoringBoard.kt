package com.paulienvanalst.rugbymatch.game

import com.paulienvanalst.rugbymatch.events.ScoringEvent
import com.paulienvanalst.rugbymatch.events.StartGame
import com.paulienvanalst.rugbymatch.team.TeamName
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class ScoringBoard {

    lateinit var hostingTeam: TeamName
    lateinit var visitingTeam: TeamName

    private var scoringHistory : List<Score> = emptyList()

    @EventListener
    fun startGameListener(startGame: StartGame) {
        hostingTeam = startGame.hostingTeam
        visitingTeam = startGame.visitingTeam
    }

    @EventListener
    fun scoreEventListener(scoringEvent: ScoringEvent) {
        scoringHistory += Score(scoringEvent.team, scoringEvent.type)
    }

    fun currentScore(): GameScore = GameScore(scoreForTeam(hostingTeam), scoreForTeam(visitingTeam))

    private fun scoreForTeam(team: TeamName): Pair<TeamName, Int> {
        return Pair(team,
                scoringHistory.filter { it.forTeam == team }
                        .map { it.type.points }
                        .sum())
    }


    fun clear () {
        scoringHistory = emptyList()

    }
}


