package controllers

import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import models.{DiseaseRepo}

/**
  * Created by chinnonae on 11/18/16.
  */
class Disease @Inject() (diseaseRepo: DiseaseRepo) extends Controller{

  def diseasesPage = Action.async { implicit request =>
    diseaseRepo.all.map {
      diseases => Ok(views.html.diseases(diseases))
    }
  }

  def diseasesDetailPage(id: Int) = Action.async { implicit request =>
    for {
      Some(disease) <- diseaseRepo.findById(id)
    } yield Ok(views.html.detail(disease))

  }
}
