package forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by chinnonae on 11/22/16.
  */

object RegisterForm {

  case class Data(username: String, password: String, confirmedPassword: String, email: String, riceVariety: String, latitude: BigDecimal, longtitude: BigDecimal)

  val form = Form(
    mapping(
      "username" -> text,
      "password" -> text,
      "confirmed-password" -> text,
      "email" -> email,
      "rice-variety" -> text,
      "latitude" -> bigDecimal,
      "longtitude" -> bigDecimal
    )(Data.apply)(Data.unapply)
  )

}