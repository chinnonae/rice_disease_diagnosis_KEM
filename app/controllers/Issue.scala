package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.ExecutionContext.Implicits.global
import java.io.File
import javax.inject.Inject

import forms.data.IssueData
import org.apache.commons.codec.digest.DigestUtils
import models.IssueRepo
import play.api.libs.Files
import play.api.mvc.MultipartFormData.FilePart

import models.IssueConstant
import forms.data.IssueData

class Issue @Inject()(protected val issueRepo: IssueRepo) extends Controller {

  def newIssuePage = Action {
    Ok(views.html.new_issue(IssueConstant.COLORS, IssueConstant.SHAPES, IssueConstant.PARTS)(issueForm))
  }

  def newIssuePOST = Action.async(parse.multipartFormData) { implicit request =>
    var submitedInfo: Map[String, String] = Map()
    for {
      (key, value) <- request.body.dataParts
    } submitedInfo = submitedInfo + (key -> value.head)
    val uploadImage = request.body.file("upload-image").get


    val temporaryName = request.id.toString
    val tempdir = System.getProperty("java.io.tmpdir")

    val tempPath = s"$tempdir/rice_diseases/temp/images/$temporaryName"
    val permDir = s"$tempdir/rice_diseases/images"

    val storedImageFile = saveFile(uploadImage, tempPath, permDir)

    issueRepo.create(
      storedImageFile.getAbsolutePath,
      submitedInfo("color"),
      submitedInfo("shape"),
      submitedInfo("part"),
      None
    ).map { id =>
      Redirect(routes.Issue.newIssuePage)
    }
  }

  private def saveFile(file: FilePart[Files.TemporaryFile], tempPath: String, permDir: String): File = {
    val tempStoredFile = file.ref.moveTo(new File(tempPath))

    val fileExtension = file.contentType.get.split('/')(1)
    val fileName = md5Checksum(tempStoredFile) + fileExtension

    val permStoredFile = new File(s"$permDir/$fileName")
    if (permStoredFile.exists) return permStoredFile
    tempStoredFile.renameTo(permStoredFile)

    permStoredFile
  }

  private def md5Checksum(file: File): String = {
    var checksum: String = null;
    import java.io._

    try {
      checksum = DigestUtils.md5Hex(new FileInputStream(file))
    } catch {
      case ioex: IOException => System.err.println(ioex)
    }

    checksum
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

