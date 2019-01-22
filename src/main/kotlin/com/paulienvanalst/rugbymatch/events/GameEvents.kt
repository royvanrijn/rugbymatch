package com.paulienvanalst.rugbymatch.events

import com.paulienvanalst.rugbymatch.game.SetPiece
import com.paulienvanalst.rugbymatch.team.TeamName
import org.springframework.context.ApplicationEvent

sealed class SetPieceEvent(source: Any, open val setPiece: SetPiece, open val winningTeam: TeamName) : ApplicationEvent(source)

class ScrumWasPlayed(source: Any, val scrum: SetPiece, override val winningTeam: TeamName) : SetPieceEvent(source, scrum, winningTeam)

class LineOutWasPlayed(source: Any, val lineOut: SetPiece, override val winningTeam: TeamName) : SetPieceEvent(source, lineOut, winningTeam)

fun List<SetPieceEvent>.wonBy(teamName: TeamName) = this.filter { event -> event.winningTeam == teamName }

fun List<SetPieceEvent>.lostBy(teamName: TeamName) = this.filter { event -> event.winningTeam != teamName }

fun List<SetPieceEvent>.scrumEvents() = this.filter { event -> event is ScrumWasPlayed }

fun List<SetPieceEvent>.lineOutEvents() = this.filter { event -> event is LineOutWasPlayed }
