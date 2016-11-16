package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by chinnonae on 11/16/16.
  */

case class Disease(
                    id: Option[Int],
                    name: String,
                    detail: String
                  )

class DiseaseRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends  HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Diseases = TableQuery[DiseasesTable]

  def all: Future[Seq[Disease]] = db.run(Diseases.result)

  private class DiseasesTable(tag: Tag) extends Table[Disease](tag, "disease") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def detail = column[String]("detail")

    override def * = (id, name, detail) <> (Disease.tupled, Disease.unapply)
  }
}
