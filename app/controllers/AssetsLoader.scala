package controllers

import java.io.File

import play.api.mvc._

/**
  * Created by chinnonae on 11/21/16.
  */
class AssetsLoader extends Controller {

  def Load(path: String) = Action {
    Ok.sendFile(new File(path))
  }

  def LoadRiceImage(file: String) = Load(System.getProperty("user.home") + "/rice_diseases/images/" + file)
}
