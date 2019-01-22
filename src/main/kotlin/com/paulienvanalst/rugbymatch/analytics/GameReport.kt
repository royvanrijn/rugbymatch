package com.paulienvanalst.rugbymatch.analytics

import com.paulienvanalst.rugbymatch.events.*
import com.paulienvanalst.rugbymatch.game.GameScore
import com.paulienvanalst.rugbymatch.game.LineOut
import com.paulienvanalst.rugbymatch.game.ScoringBoard
import com.paulienvanalst.rugbymatch.game.Scrum
import com.paulienvanalst.rugbymatch.team.TeamName
import org.apache.logging.log4j.LogManager
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
open class GameReporter (val scoringBoard: ScoringBoard) {
    private val LOG = LogManager.getLogger(GameReporter::class.java)

    lateinit var gameReport : GameReport

    @EventListener
    fun startTheReport(startGame: StartGame) {
        LOG.info("start the game ${startGame.hostingTeam} vs ${startGame.visitingTeam}")
        gameReport = GameReport(startGame.hostingTeam, startGame.visitingTeam)
    }

    @EventListener
    fun halfTime(halfTime: HalfTime) {
        gameReport.setHalfTimeScore(scoringBoard.currentScore())
    }

    @EventListener
    fun endGame(finishGame: FinishGame) : GenerateGameReport {
        gameReport.setFinalScore(scoringBoard.currentScore())
        return GenerateGameReport(this, this.gameReport)
    }

    @EventListener
    fun updatingReportAfterSetPiece(setPieceEvent: SetPieceEvent) {
        gameReport.addSetPieceEvents(setPieceEvent)
    }


}

class GenerateGameReport (source: Any, val report : GameReport) : ApplicationEvent(source)

class GameReport (val hostingTeam : TeamName, val visitingTeam : TeamName) {

    private var lineOuts: List<LineOut> = emptyList()
    private var scrums: List<Scrum> = emptyList()
    private var setPieceEvents: List<SetPieceEvent> = emptyList()

    private var halfTimeScores : GameScore = GameScore(hostingTeam to 0, visitingTeam to 0)
    private var endScores : GameScore = GameScore(hostingTeam to 0, visitingTeam to 0)


    fun addSetPieceEvents(setPieceEvent: SetPieceEvent) {
        setPieceEvents += setPieceEvent

        when(setPieceEvent) {
            is LineOutWasPlayed -> lineOuts += setPieceEvent.lineOut
            is ScrumWasPlayed -> scrums += setPieceEvent.scrum
        }

    }

    fun setHalfTimeScore(gameScore: GameScore) {
        this.halfTimeScores = gameScore
    }

    fun setFinalScore(gameScore: GameScore) {
        this.endScores = gameScore
    }

    fun format() : String {
        return """
            --------------------------------------------------------------------------------
                        The game ended with the score of ${this.endScores}.
            --------------------------------------------------------------------------------
            --------------------------------------------------------------------------------
             The half-time score was: ${halfTimeScores}

            ********* Line-outs *********
            We got ${lineOuts.size} line outs.

            ${lineOutReport(hostingTeam)}
            ${lineOutReport(visitingTeam)}

            ********* Scrums *********
            We got ${scrums.size} scrums.

            ${scrumReport(hostingTeam)}
            ${scrumReport(visitingTeam)}



            """"
    }

    private fun lineOutReport(teamName: TeamName): String {
        return """--------- $teamName ---------
                 $teamName conserved the ball in ${this.setPieceEvents.lineOutEvents().wonBy(teamName).size} line-outs
                 and lost the ball in ${this.setPieceEvents.lineOutEvents().lostBy(teamName).size} line-outs
                 """
    }

    private fun scrumReport(teamName: TeamName): String {
        return """--------- $teamName ---------
                 $teamName conserved the ball in ${this.setPieceEvents.scrumEvents().wonBy(teamName).size} scrums
                 and lost the ball in ${this.setPieceEvents.scrumEvents().lostBy(teamName).size} scrums
                 """
    }

}
