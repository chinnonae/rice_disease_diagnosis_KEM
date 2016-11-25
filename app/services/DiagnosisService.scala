package services


import java.io.File
import javax.inject.Inject

import inference_engine.SWIPrologDriver
import org.jpl7._
import play.api.libs.ws._
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



/**
  * Created by chinnonae on 11/24/16.
  */
class DiagnosisService @Inject() (ws: WSClient){

  private val prologDriver = SWIPrologDriver
  prologDriver.consult(System.getProperty("user.dir") + "/KE-rice-disease-diagnosis-KB/engine.pl")

  def diseaseDiagnosis(imageSrc: String, color: String, shape: String, part: String): Future[List[Result]] = {
    val futures = for {
      esResults <- expertSystemDiagnose(color, shape, part)
      ipResults <- imageProcessingDiagnose(imageSrc)
    } yield (esResults, ipResults)

    futures.map{ case(esResult: Array[Result], ipResult: Array[Result]) => accuratizeResult(esResult, ipResult)}
  }

  def expertSystemDiagnose(color: String, shape: String, part: String): Future[Array[Result]] = {
    prologDriver.query(new Query(new Compound(
      "diagnosis",
      Array[Term](new Variable("Disease"), new Atom(color.toLowerCase), new Atom(part.toLowerCase), new Atom(shape.toLowerCase)))))
        .map{ query =>
          val diseases = query.allSolutions()(0).values()
          var results = Array[Result]()
          val iterator = diseases.iterator()
          while(iterator.hasNext) {
            val disease = iterator.next().toString
            results = results :+ Result(disease, 50)
          }
          results
        }
  }


  def imageProcessingDiagnose(imageSrc: String): Future[Array[Result]] = {
    val file = new File(imageSrc)
    ws.url("http://localhost:5000/upload2")
      .post(Map("file_src" -> Seq(imageSrc)))
      .map { response =>
        val respondArr = Json.parse(response.body).as[JsArray].value
        respondArr.map { jsValue =>
          val obj = jsValue.as[JsObject].value
          val disease = obj.get("name").get.toString
          val chance = obj.get("percentage").get.toString.toDouble
          Result(disease, chance)
        }.toArray
      }
  }

  private def accuratizeResult(expertSystemResults: Array[Result], imageProcessorResults: Array[Result]): List[Result] = {
    var answers: List[Result] = expertSystemResults.toList


    for(ipResult <- imageProcessorResults) {
      println(ipResult)
      println(answers)
      println(answers.contains(ipResult))
      if(!answers.contains(ipResult) && ipResult.chance > 85) {
        answers = Result(ipResult.disease, ipResult.chance/2)::answers
        println("-" + answers)
      } else if (answers.contains(ipResult)) {
        answers = answers.filterNot(_ equals ipResult)
        if (ipResult.chance >= 45) {
          val esChance = expertSystemResults.collectFirst({case Result(ipResult, chance) => chance}).get
          answers = Result(ipResult.disease, (esChance+ipResult.chance)/2)::answers
        }
        println("--" + answers)
      }
      println("++++++++++")
    }

    answers.sortBy(- _.chance)
  }

  case class Result(disease: String, chance: Double) {
    override def equals(obj: scala.Any): Boolean = {
      obj.asInstanceOf[Result].disease.equalsIgnoreCase(disease)
    }
  }


}
