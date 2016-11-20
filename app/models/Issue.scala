package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

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
                  answer: Option[String]
                )

class IssueRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Issues = TableQuery[IssuesTable]

  def all(): Future[Seq[Issue]] = db.run(Issues.result)

  def create(issue: Issue): Future[Int] = {
    db.run(Issues returning Issues.map(_.id) += issue)
  }

  def create(imageSrc: String, color: String, shape: String, part: String, addInfo: Option[String] = None): Future[Int] = {
    create(Issue(
      -1,
      imageSrc,
      IssueConstant.COLORS.indexOf(color),
      IssueConstant.SHAPES.indexOf(shape),
      IssueConstant.PARTS.indexOf(part),
      addInfo,
      None
    ))
  }

  private class IssuesTable(tag: Tag) extends Table[Issue](tag, "issue") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def imageSrc = column[String]("image_source")
    def color = column[Int]("color")
    def shape = column[Int]("shape")
    def part = column[Int]("part")
    def addInfo = column[Option[String]]("additional_info")
    def answer = column[Option[String]]("answer")

    override def * = (id, imageSrc, color, shape, part, addInfo, answer) <> (Issue.tupled, Issue.unapply)
  }
}

object IssueConstant {
  val COLORS = Array("Brown", "Red")
  val SHAPES = Array("Spot", "Circle")
  val PARTS = Array("Leaf", "Stem")
}
