/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.zbelzer

import java.sql.Timestamp

import com.digi.xbee.api.listeners.IDataReceiveListener
import com.digi.xbee.api.models.XBeeMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._

class DataListener(val db: Database) extends IDataReceiveListener {
  val mapper: ObjectMapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def dataReceived(xbeeMessage: XBeeMessage) {
    try {
      val bytes = xbeeMessage.getData

      logger.debug(new String(bytes))
      println(new String(bytes))

      val message = mapper.readValue(bytes, classOf[Message])

      val op = message match {
        case metricData: Metric => {
          val metric = metricData.copy(created = new Timestamp(System.currentTimeMillis()))

          Tables.metrics += metric
        }
        case eventData: Event => {
          val event = eventData.copy(created = new Timestamp(System.currentTimeMillis()))

          Tables.events += event
        }

      }

      db.run(DBIO.seq(op))
    }
    catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
  }
}

