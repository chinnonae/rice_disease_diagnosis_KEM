package inference_engine

import org.jpl7.Query

import scala.concurrent.Future

/**
  * Created by chinnonae on 11/24/16.
  */
trait PrologDriver {
  def consult(string: String): Boolean
  def query(query: Query): Future[Query]
}
