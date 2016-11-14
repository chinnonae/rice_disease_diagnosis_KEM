package controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Redirect(routes.Application.diseasesPage())
  }

  def diseasesPage = Action {
    Ok(views.html.diseases())
  }

  def newIssuePage = Action {
    Ok(views.html.new_issue())
  }

}
