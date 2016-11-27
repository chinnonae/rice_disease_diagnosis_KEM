package utils

import javax.inject.Inject

import models.{User, UserRepo}
import pdi.jwt.{JwtAlgorithm, JwtJson}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Created by chinnonae on 11/26/16.
  */
class AuthSession @Inject() (userRepo: UserRepo) {

  val AUTHEN_SESSION_NAME = "authen.jwt.session"
  val secretKey = "rice_diseases"
  val algorithm = JwtAlgorithm.HS256

  def toUser(request: Request[AnyContent]): Option[User] = {
    request.session.get(AUTHEN_SESSION_NAME) match {
      case Some(token) => decode(token)
      case None => None
    }
  }

  def toSession(user: User): (String, String) = {
    return (AUTHEN_SESSION_NAME, encode(user))
  }

  def decode(token: String): Option[User] = {
    val result = JwtJson.decodeJson(token, secretKey, Seq(algorithm))
    result match {
      case Failure(fail) => None
      case Success(jsObj) => Await.result(userRepo.findByUsername(jsObj.value("username").as[JsString].value), 1.second)
    }
  }

  def encode(user: User): String = {
    val payload = Json.obj(
      ("username", user.username))

    JwtJson.encode(payload, secretKey, algorithm)
  }
}
