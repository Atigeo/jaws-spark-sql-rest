package com.xpatterns.jaws.data.DTO

import spray.json.DefaultJsonProtocol._
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.catalyst.expressions.Row

/**
 * Created by emaorhian
 */
case class ResultDTO(var schema: Array[Column], var results: Array[Array[String]]) {

  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    Option(results) match {
      case None => result = prime * result + 0
      case _ => result = prime * result + results.hashCode()
    }
    Option(schema) match {
      case None => result = prime * result + 0
      case _ => result = prime * result + schema.hashCode()
    }

    result
  }

  override def equals(other: Any): Boolean = {

    other match {

      case that: ResultDTO =>
        (that canEqual this) &&
          results.deep == that.results.deep &&
          schema.deep == that.schema.deep

      case _ => false
    }
  }

  override def toString(): String = {
    "ResultDTO [schema=" + schema + ", results=" + results + "]"
  }

  def this(schema: Set[Attribute], result: Array[Row]) {
	  this(ResultDTO.getSchema(schema), ResultDTO.getResults(result))
  }
}

object ResultDTO {
  implicit val logsJson = jsonFormat2(apply)

  def fromTuples(schema: Array[Column], filteredResults: Array[Tuple2[Object, Array[Object]]]): ResultDTO = {

    var results = Array[Array[String]]()
    filteredResults.foreach(tuple => {
      var row = Array[String]()
      tuple._2.foreach(field => row = row ++ Array(Option(field).getOrElse("Null").toString))
      results = results ++ Array(row)
    })
    ResultDTO(schema, results)
  }

  def trimResults(result: ResultDTO): ResultDTO = {
    ResultDTO(result.schema, result.results.map(row => row.map(field => field.trim())))
  }
  
   def getSchema(schema: Set[Attribute]): Array[Column] = {
    var finalSchema = Array[Column]()
    schema.foreach(attribute => { finalSchema = finalSchema ++ Array(new Column(attribute.name, attribute.dataType.toString())) })
    finalSchema
  }
   
   def getResults(results: Array[Row]): Array[Array[String]] = {
    var finalResults = Array[Array[String]]()
 
    results.foreach(row => {
      var rrow = row.map(value =>{
        Option(value) match {
            case None => "Null"
            case _ => value.toString()
      }})
      
      finalResults = finalResults ++ Array(rrow.toArray)
    })
    
    finalResults
  }
}  
  