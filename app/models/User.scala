package models

import java.util.UUID
import javax.inject.Inject

import org.apache.commons.codec.digest.DigestUtils
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by chinnonae on 11/21/16.
  */
case class User(
               id: Option[Int],
               username: String,
               hashPassword: String,
               email: String,
               riceVariety: String,
               latitude: Double,
               longtitude: Double
               )

class UserRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  val Users = TableQuery[UserTable]

  def create(username: String, password: String, email: String, extra_arg: Map[String, String]): Future[Int] = {
    val hashedPassword = DigestUtils.sha256Hex(password)
    val newUser = User(None, username, hashedPassword, email, extra_arg("riceVariety"), extra_arg("lat").toDouble, extra_arg("lon").toDouble)

    db.run(Users returning Users.map(_.id) += newUser)
  }

  def all(): Future[Seq[User]] = {
    db.run(Users.result)
  }

  def findByUsername(username: String): Future[Option[User]] = {
    db.run(Users.filter(_.username === username).result.headOption)
  }

  class UserTable(tag: Tag) extends Table[User](tag, "member") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def username = column[String]("username")
    def hashedPassword = column[String]("hashed_password")
    def email = column[String]("email")
    def riceVariety = column[String]("rice_variety")
    def latitude = column[Double]("latitude")
    def longtitude = column[Double]("longtitude")

    override def * = (id.?, username, hashedPassword, email, riceVariety, latitude, longtitude) <> (User.tupled, User.unapply)
  }

}
