package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import models.{Disease, DiseaseRepo}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * Created by chinnonae on 11/17/16.
  */
class Development @Inject()(protected val diseaseRepo: DiseaseRepo) extends Controller {


  def createDiseasePOST = Action.async { implicit request => {
    val disease = diseaseForm.bindFromRequest.get
    val result = diseaseRepo.create(disease)
    result.map(a => Redirect(routes.Development.createDiseaseView()))
  }}

  def createDiseaseView = Action {
    Ok(views.html.createDisease(diseaseForm))
  }

  private val diseaseForm = Form(
    mapping(
      "id" -> optional(number),
      "name" -> text,
      "what_it_does" -> text,
      "why_and_where_it_occurs" -> text,
      "how_to_identify" -> text
    )(Disease.apply)(Disease.unapply)
  )
}
