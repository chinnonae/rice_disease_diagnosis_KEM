package controllers

import javax.inject.Inject

import models.{User, UserRepo}
import play.api.mvc._
import pdi.jwt._
import play.api.libs.json._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import forms.{LoginForm, RegisterForm}
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chinnonae on 11/14/16.
  */
class Authentication @Inject()(userRepo: UserRepo, messagesApi: MessagesApi) extends Controller {

  def registerPage = Action {
    Ok(views.html.Register(RegisterForm.form))
  }

  def registerPOST = Action.async(parse.urlFormEncoded) { implicit request =>
    val input = RegisterForm.form.bindFromRequest()
    val extra_arg = Map("riceVariety" -> input.get.riceVariety)

    userRepo.create(input.get.username, input.get.password, input.get.email, extra_arg).map { id =>

      Ok(views.html.Register(RegisterForm.form))
    }
  }

  def loginPage = Action {
    Ok(views.html.Login(LoginForm.form))
  }

  def loginPOST = Action.async(parse.urlFormEncoded) { request =>
    val username = request.body("login-username").head
    val password = request.body("login-password").head

    val queryResult = userRepo.findByUsername(username);

    queryResult.map(_ match {
      case Some(user) => Redirect(routes.Application.index()).withJwtSession(sessionData(user))

      case None => Ok(views.html.Register(RegisterForm.form))
    })
  }


  private def sessionData(user: User): JsObject = {
    val json = JsObject(Seq(
      "username" -> JsString(user.username)
    ))

    json
  }
}
