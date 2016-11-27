package inference_engine

import org.jpl7._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
  * Created by chinnonae on 11/24/16.
  */
object SWIPrologDriver extends PrologDriver {

  def consult(file: String): Boolean = {
    val q = new Query(
      "consult", Array[Term](new Atom(file))
    )
    q.hasSolution
  }

  def query(query: Query): Future[Query] = Future {
    query.hasSolution
    query
  }
}
