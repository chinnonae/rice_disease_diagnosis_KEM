package controllers

import play.api.mvc._

/**
  * Created by chinnonae on 11/14/16.
  */
class Authentication extends Controller {

  def loginModal = Action {
    Ok(views.html.login()).as(HTML)
  }

  def registerModal = Action {
    Ok(views.html.register()).as(HTML)
  }
}
