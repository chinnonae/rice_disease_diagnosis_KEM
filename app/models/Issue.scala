package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future
import models.UserRepo
import utils.AuthSession

/**
  * Created by chinnonae on 11/16/16.
  */

case class Issue(
                  id: Int,
                  imageSrc: String,
                  color: Int,
                  shape: Int,
                  part: Int,
                  addInfo: Option[String],
                  answer: Option[String],
                  userId: Option[Int]
                )

class IssueRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, userRepo: UserRepo) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val Issues = TableQuery[IssuesTable]

  def all(): Future[Seq[Issue]] = db.run(Issues.result)

  def findById(id: Int): Future[Option[Issue]] = db.run(Issues.filter(_.id === id).result.headOption)

  def create(issue: Issue): Future[Int] = {
    db.run(Issues returning Issues.map(_.id) += issue)
  }

  def create(imageSrc: String, color: String, shape: String, part: String, addInfo: Option[String] = None, user: Option[User], answer: String): Future[Int] = {
    create(Issue(
      -1,
      imageSrc,
      IssueConstant.COLORS.indexOf(color),
      IssueConstant.SHAPES.indexOf(shape),
      IssueConstant.PARTS.indexOf(part),
      addInfo,
      Some(answer),
      if (user.isDefined) user.get.id else None
    ))
  }

  def answer(id: Int, answer: String) = {

  }

  class IssuesTable(tag: Tag) extends Table[Issue](tag, "issue") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def imageSrc = column[String]("image_source")
    def color = column[Int]("color")
    def shape = column[Int]("shape")
    def part = column[Int]("part")
    def addInfo = column[Option[String]]("additional_info")
    def answer = column[Option[String]]("answer")
    def userId = column[Option[Int]]("user_id")

    override def * = (id, imageSrc, color, shape, part, addInfo, answer, userId) <> (Issue.tupled, Issue.unapply)
  }
}

object IssueConstant {
  val COLORS = Array("Brown", "Red")
  val SHAPES = Array("Spot", "Circle")
  val PARTS = Array("Neck", "Crowns", "Collar", "Node", "Leaf", "Glumes", "Spikelets", "Seeds", "Leaf sheath", "Coleoptile", "Roots", "Parts of panicle", "Panicle branches")
}
