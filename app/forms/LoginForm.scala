package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by chinnonae on 11/22/16.
  */
object LoginForm {

  case class Data(username: String, password: String)

  val form = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(Data.apply)(Data.unapply)
  )
}
