package com.zbelzer

import java.sql.{Time, Timestamp}

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes(Array(
  new Type(value = classOf[Event], name = "event"),
  new Type(value = classOf[Metric], name = "metric")
))
trait Message

case class Event(
  id: Option[Long] = None,
  created: Timestamp,
  name: String,
  value: String) extends Message

case class Metric(
  id: Option[Long] = None,
  created: Timestamp,
  name: String,
  value: Double) extends Message

class Metrics(tag: Tag) extends Table[Metric](tag, "metrics") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def created = column[Timestamp]("created")
  def name = column[String]("name")
  def value = column[Double]("value")

  def * = (id.?, created, name, value) <> (Metric.tupled, Metric.unapply)
}

class Events(tag: Tag) extends Table[Event](tag, "events") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def created = column[Timestamp]("created")
  def name = column[String]("name")
  def value = column[String]("value")

  def * = (id.?, created, name, value) <> (Event.tupled, Event.unapply)
}
