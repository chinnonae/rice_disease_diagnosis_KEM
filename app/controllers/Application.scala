package controllers

import javax.inject.Inject

import models.DiseaseRepo
import play.api.mvc._
import play.api.mvc.Action
import scala.concurrent.ExecutionContext.Implicits.global


class Application @Inject() (diseaseRepo: DiseaseRepo) extends Controller {

  def index = Action.async { implicit request =>
    diseaseRepo.all.map {
      diseases => Ok(views.html.diseases(diseases))
    }
  }

  def diseasesPage = Action.async { implicit request =>
    diseaseRepo.all.map {
      diseases => Ok(views.html.diseases(diseases))
    }
  }

  def newIssuePage = Action {
    Ok(views.html.new_issue())
  }

  def diseasesDetailPage(id: Int) = Action {
    Ok(views.html.detail("Disease"))
  }

}
