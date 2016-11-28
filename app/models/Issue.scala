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
                  factor: Int,
                  growthStage: Int,
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

  def create(imageSrc: String, color: String, shape: String, part: String, factor: String, growthStage: String,
             addInfo: Option[String] = None, user: Option[User], answer: String): Future[Int] = {
    create(Issue(
      -1,
      imageSrc,
      IssueConstant.COLORS.indexOf(color),
      IssueConstant.SHAPES.indexOf(shape),
      IssueConstant.PARTS.indexOf(part),
      IssueConstant.FACTOR.indexOf(factor),
      IssueConstant.GROWTH_STAGE.indexOf(growthStage),
      addInfo,
      Some(answer),
      if (user.isDefined) user.get.id else None
    ))
  }

  class IssuesTable(tag: Tag) extends Table[Issue](tag, "issue") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def imageSrc = column[String]("image_source")
    def color = column[Int]("color")
    def shape = column[Int]("shape")
    def part = column[Int]("part")
    def factor = column[Int]("factor")
    def growthStage = column[Int]("growth_stage")
    def addInfo = column[Option[String]]("additional_info")
    def answer = column[Option[String]]("answer")
    def userId = column[Option[Int]]("user_id")

    override def * = (id, imageSrc, color, shape, part, factor, growthStage, addInfo, answer, userId) <> (Issue.tupled, Issue.unapply)
  }
}

object IssueConstant {
  val COLORS = Array("Brown", "Red", "Dark", "Green", "Gray", "Purple", "White", "Yellow", "Orange", "Black",
    "Light_tan", "Light", "None")
  val SHAPES = Array("Spot", "Diamond", "Elliptic", "Spindle", "Circle", "Oval", "Ellipsoid", "Stripe", "Taller_plant",
    "Thin_leave", "Spore_ball", "Linear", "None")
  val PARTS = Array("Leaf", "Collar", "Node", "Neck", "Panicle", "Leaf_sheath", "Glume", "Spikelet", "Seed",
    "Red_stripe", "Root", "Crown", "Coleoptile", "Leaf_blade", "Pedicel", "Upper_most_leaf_sheath", "Seedling",
    "Leaf_tips", "None")
  val FACTOR = Array("Low_soil_moisture", "Low_temperature", "High_humidity", "Unflooded_soil", "Toxic_soil",
    "High_nitrogen", "Rainy", "High_wetness", "pathogen_in_soil", "High_temperature", "Wet_weather", "None")
  val GROWTH_STAGE =  Array("Late", "Crop", "Any", "Flowering", "Tillering", "Milk", "Reproductive", "Booting", "None")
}
