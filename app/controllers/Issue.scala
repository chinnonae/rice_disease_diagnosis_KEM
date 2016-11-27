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
import services.DiagnosisService
import utils.AuthSession

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class Issue @Inject()(protected val issueRepo: IssueRepo, authSession: AuthSession) extends Controller {

  def newIssuePage = Action {
    Ok(views.html.new_issue(IssueConstant.COLORS, IssueConstant.SHAPES, IssueConstant.PARTS, IssueConstant.FACTOR, IssueConstant.GROWTH_STAGE)(issueForm))
  }

  def newIssuePOST = Action.async(parse.multipartFormData) { implicit request =>
    var submitedInfo: Map[String, String] = Map()
    for {
      (key, value) <- request.body.dataParts
    } submitedInfo = submitedInfo + (key -> value.head)
    val uploadImage = request.body.file("upload-image").get


    val temporaryName = request.id.toString
    val tempdir = System.getProperty("java.io.tmpdir")
    val homedir = System.getProperty("user.home")

    new File(s"$tempdir/rice_diseases/temp/images").mkdirs()
    new File(s"$homedir/rice_diseases/images").mkdirs()
    val tempPath = s"$tempdir/rice_diseases/temp/images/$temporaryName"
    val permDir = s"$homedir/rice_diseases/images"

    val storedImageFile = saveFile(uploadImage, tempPath, permDir)

    val userOpt = authSession.toUser(request.asInstanceOf[Request[AnyContent]])

    val results = Await.result(DiagnosisService.diseaseDiagnosis(
      storedImageFile.getAbsolutePath,
      submitedInfo("color"),
      submitedInfo("shape"),
      submitedInfo("part"),
      submitedInfo("factor"),
      submitedInfo("growth-stage")
    ), 2.second)

    val answer = results.map { disease =>
      s"${disease.disease}: ${disease.chance%.2f}"
    }.mkString("\n")

    issueRepo.create(
      storedImageFile.getAbsolutePath,
      submitedInfo("color"),
      submitedInfo("shape"),
      submitedInfo("part"),
      submitedInfo("factor"),
      submitedInfo("growth-stage"),
      Some(submitedInfo("additional-info").trim),
      userOpt,
      if(answer.isEmpty) answer else "System cannot find the answer"
    ).map { id =>
      Redirect(routes.Issue.issuePage(id))
    }

  }

  def issuesPage = Action.async { implicit request =>
    issueRepo.all().map {
      issues => Ok(views.html.IssueList(issues.sortBy(- _.id)))
    }
  }

  def issuePage(id: Int) = Action.async { implicit request =>
    issueRepo.findById(id).map {
      issue => Ok(views.html.IssueDetail(issue.get))
    }
  }

  private def saveFile(file: FilePart[Files.TemporaryFile], tempPath: String, permDir: String): File = {
    val tempStoredFile = file.ref.moveTo(new File(tempPath))

    val fileExtension = file.contentType.get.split('/')(1)
    val fileName = md5Checksum(tempStoredFile) + "." + fileExtension

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
      "factor" -> text,
      "growth_stage" -> text,
      "additional-info" -> text
    )(IssueData.apply)(IssueData.unapply)
  )
}

