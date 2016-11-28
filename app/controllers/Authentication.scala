package controllers

import javax.inject.Inject

import models.{User, UserRepo}
import play.api.mvc._
import play.api.libs.json._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import forms.{LoginForm, RegisterForm}
import play.api.i18n.MessagesApi
import utils.AuthSession

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chinnonae on 11/14/16.
  */
class Authentication @Inject()(userRepo: UserRepo, messagesApi: MessagesApi, authSession: AuthSession) extends Controller {

  def registerPage = Action {
    Ok(views.html.Register(RegisterForm.form))
  }

  def registerPOST = Action.async(parse.urlFormEncoded) { implicit request =>
    val input = RegisterForm.form.bindFromRequest()
    val extra_arg = Map(
      "riceVariety" -> input.get.riceVariety,
      "lat" -> input.get.latitude.toString,
      "lon" -> input.get.longtitude.toString
    )

    userRepo.create(input.get.username, input.get.password, input.get.email, extra_arg).map { id =>

      Redirect(routes.Authentication.loginPage())
    }
  }

  def loginPage = Action {
    Ok(views.html.Login(LoginForm.form))
  }

  def profileGET(token: String) = Action {
    val result = authSession.decode(token)
    result match {
      case Some(user) => {
        Ok(Json.obj(
          ("username", user.username),
          ("email", user.email)
        ))
      }
      case None => BadRequest("")
    }
  }

  def loginPOST = Action.async(parse.urlFormEncoded) { implicit request =>
    println(request.body)
    val username = request.body("username").head
    val password = request.body("password").head
    println(username)
    println(password)

    val queryResult = userRepo.findByUsername(username)

    queryResult.map(_ match {
      case Some(user) => Redirect(routes.Application.index()).addingToSession(authSession.toSession(user)).withCookies(Cookie("logged-in", authSession.encode(user), httpOnly = false))

      case None => Ok(views.html.Register(RegisterForm.form))
    })
  }

  def logoutPOST = Action { request =>
    Redirect(routes.Application.index()).withNewSession.discardingCookies(DiscardingCookie("logged-in"))
  }


}
