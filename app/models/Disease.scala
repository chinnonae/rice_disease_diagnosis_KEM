package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chinnonae on 11/16/16.
  */

case class Disease(
                    id: Int,
                    name: String,
                    whatItDoes: String,
                    whyAndWhereItOccurs: String,
                    howToIdentify: String
                  )

class DiseaseRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends  HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Diseases = TableQuery[DiseasesTable]

  def all: Future[Seq[Disease]] = db.run(Diseases.result)

  def create(disease: Disease): Future[Int] = {
    db.run(Diseases returning Diseases.map(_.id) += disease)
  }

  def findById(id: Int): Future[Option[Disease]] = db.run(Diseases.filter(_.id === id).result.headOption)

  private class DiseasesTable(tag: Tag) extends Table[Disease](tag, "disease") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def whatItDoes = column[String]("what_it_does")
    def whyAndWhereItOccurs = column[String]("why_and_where_it_occurs")
    def howToIdentify = column[String]("how_to_identify")

    override def * = (id, name, whatItDoes, whyAndWhereItOccurs, howToIdentify) <> (Disease.tupled, Disease.unapply)
  }
}
