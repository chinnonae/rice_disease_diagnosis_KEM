package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.ExecutionContext.Implicits.global
import java.io.File
import javax.inject.Inject

import org.apache.commons.codec.digest.DigestUtils

import scala.concurrent.Future
import models.IssueRepo
/**
  * Created by chinnonae on 11/18/16.
  */

class Issue @Inject()(protected val issueRepo: IssueRepo) extends Controller {


  val mockupColors = Array("Brown", "Red")
  val mockupShape = Array("Spot", "Circle")
  val mockupPart = Array("Leaf", "Stem")

  def newIssuePage = Action {
    Ok(views.html.new_issue(mockupColors, mockupShape, mockupPart)(issueForm))
  }

  def newIssuePOST = Action.async(parse.multipartFormData) { implicit request =>
    var submitedInfo: Map[String, String] = Map()
    for {
      (key, value) <- request.body.dataParts
    } submitedInfo = submitedInfo + (key -> value.head)
    val uploadImage = request.body.file("upload-image").get

    val temporaryName = request.id.toString
    val tempdir = System.getProperty("java.io.tmpdir")
    val file = new File(s"$tempdir/rice_diseases/temp/images/$temporaryName")
    val storedImageFile = uploadImage.ref.moveTo(file)

    var checksum: String = null;
    import java.io._
    try {
      checksum = DigestUtils.md5Hex(new FileInputStream(storedImageFile))
    } catch {
      case ioe: IOException => {}
    }

    val fileExtension = uploadImage.contentType.get.split("/")(1)
    val movedFile = new File(s"$tempdir/rice_diseases/images/$checksum.$fileExtension")
    if(!movedFile.exists()) {
      storedImageFile.renameTo(movedFile)
    }

    val issue = models.Issue(
      -1,
      storedImageFile.getAbsolutePath,
      mockupColors.indexOf(submitedInfo("color")),
      mockupShape.indexOf(submitedInfo("shape")),
      mockupPart.indexOf(submitedInfo("part")),
      Some(submitedInfo("additional-info")),
      None
    )

    issueRepo.create(issue).map { _ =>
      Redirect(routes.Issue.newIssuePage)
    }
  }

  private val issueForm = Form(
    mapping(
      "color" -> text,
      "shape" -> text,
      "part" -> text,
      "additional-info" -> text
    )(IssueData.apply)(IssueData.unapply)
  )
}

case class IssueData(color: String, shape: String, part: String, addInfo: String)