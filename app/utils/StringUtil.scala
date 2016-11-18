package utils

/**
  * Created by chinnonae on 11/18/16.
  */

class ExtendedString(val string: String) {
  def truncate(length: Int): String = {
    if(string.length <= length - 3) return string

    string.substring(0, length-3).trim() + "..."
  }

}

object StringUtil {
  implicit def extendString(string: String) = new ExtendedString(string)
}
