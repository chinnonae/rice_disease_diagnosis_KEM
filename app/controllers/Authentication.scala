package controllers

import play.api.mvc._

/**
  * Created by chinnonae on 11/14/16.
  */
class Authentication extends Controller {

  def loginPage = Action {
    Ok(views.html.login())
  }

  def registerPage = Action {
    Ok(views.html.register())
  }
}
