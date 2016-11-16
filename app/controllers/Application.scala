package controllers

import play.api.mvc._
import play.api.mvc.Action
class Application extends Controller {

  def index = Action {
    Ok(views.html.diseases())
  }

  def diseasesPage = Action {
    Ok(views.html.diseases())
  }

  def newIssuePage = Action {
    Ok(views.html.new_issue())
  }

  def diseasesDetailPage = Action {
    Ok(views.html.detail("Disease"))
  }

}
