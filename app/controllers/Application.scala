package controllers

import play.api.mvc._
import play.api.mvc.Action

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import models.DiseaseRepo



class Application @Inject() (diseaseRepo: DiseaseRepo) extends Controller {

  def index = Action.async { implicit request =>
    diseaseRepo.all.map {
      diseases => Ok(views.html.diseases(diseases))
    }
  }







}
