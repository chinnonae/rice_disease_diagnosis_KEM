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
                  id: Option[Int],
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
    db.run(Issues returning Issues.map(_.id.get) += issue)
  }

  private class IssuesTable(tag: Tag) extends Table[Issue](tag, "issue") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def imageSrc = column[String]("image_source")
    def color = column[Int]("color")
    def shape = column[Int]("shape")
    def part = column[Int]("part")
    def addInfo = column[Option[String]]("additional_info")
    def answer = column[Option[String]]("answer")

    override def * = (id, imageSrc, color, shape, part, addInfo, answer) <> (Issue.tupled, Issue.unapply)
  }
}
