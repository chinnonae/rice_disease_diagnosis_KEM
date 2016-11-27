package services


import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import inference_engine.SWIPrologDriver
import org.jpl7._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future



/**
  * Created by chinnonae on 11/24/16.
  */
object DiagnosisService {

  private val prologDriver = SWIPrologDriver
  private var consult = false

  if(!consult) {
    prologDriver.consult(System.getProperty("user.dir") + "/KE-rice-disease-diagnosis-KB/engine.pl")
    consult = true
  }


  def diseaseDiagnosis(imageSrc: String, color: String, shape: String, part: String): Future[List[Result]] = {
    println(imageSrc)
    println(color)
    println(shape)
    println(part)
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
    val ws = newWSClient
    ws.url("http://localhost:5000/upload2")
      .post(Map("file_src" -> Seq(imageSrc)))
      .map { response =>
        val respondArr = Json.parse(response.body).as[JsArray].value
        respondArr.map { jsValue =>
          val obj = jsValue.as[JsObject].value
          val disease = obj("name").as[JsString].value
          val chance = obj("percentage").as[JsNumber].value.toDouble
          Result(disease, chance)
        }.toArray
      }
      .map { array =>
        ws.close()
        array
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

  private def newWSClient: WSClient = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    AhcWSClient()
  }

  case class Result(disease: String, chance: Double) {
    override def equals(obj: scala.Any): Boolean = {
      obj.asInstanceOf[Result].disease.equalsIgnoreCase(disease)
    }
  }


}
