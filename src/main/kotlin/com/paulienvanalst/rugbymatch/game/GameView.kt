package com.paulienvanalst.rugbymatch.game

import com.paulienvanalst.rugbymatch.analytics.MailingService
import com.paulienvanalst.rugbymatch.events.FinishGame
import com.paulienvanalst.rugbymatch.events.ScoringEvent
import com.paulienvanalst.rugbymatch.events.StartGame
import com.paulienvanalst.rugbymatch.team.TeamName
import org.apache.logging.log4j.LogManager
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class GameController(val publisher : ApplicationEventPublisher, val mailingService: MailingService) {



    private val logger = LogManager.getLogger(GameController::class.java)

    @GetMapping("/")
    fun home(): String {
        return "Welcome to this awesome workshop!"
    }

    @GetMapping("/start-game")
    fun start(): Response {
        logger.info("Publishing event!")
        return try {
            publisher.publishEvent(StartGame(this, TeamName.RC_TOULON, TeamName.WASPS))
            Response(200, "The Game was started", Teams(TeamName.RC_TOULON, TeamName.WASPS))
        } catch(e: RuntimeException) {
            Response(500, "Oops an error occurred ${e.message}", null)
        }
    }

    @GetMapping("/try")
    fun tryToulon(): Response {
        logger.info("Publishing try event!")

        return try {
            publisher.publishEvent(ScoringEvent(this, Type.TRY, TeamName.RC_TOULON))
            Response(200, "A try was scored", null)
        } catch(e: RuntimeException) {
            Response(500, "Oops an error occurred ${e.message}", null)
        }
    }

    @GetMapping("/end-game")
    fun end(): Response {
        logger.info("Publishing event!")
        return try {
            publisher.publishEvent(FinishGame(this))
            Response(200, "The Game was stopped", mailingService.mailToSend)
        } catch(e: RuntimeException) {
            Response(500, "Oops an error occurred ${e.message}", null)
        }
    }

}

data class Response(val code: Int, val message: String, val body: Any?)
