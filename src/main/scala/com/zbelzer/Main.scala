package com.zbelzer

import com.digi.xbee.api.XBeeDevice
import com.digi.xbee.api.exceptions.InvalidOperatingModeException
import org.postgresql.util.PSQLException
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val PORT = System.getProperty("serial.port", "")
  val BAUD_RATE = System.getProperty("serial.baudRate", "")

  require(!BAUD_RATE.isEmpty, "A serial baud rate set through serial.baudRate is required")
  require(!PORT.isEmpty, "A port set through serial.port is required")

  val xbee = new XBeeDevice(PORT, BAUD_RATE.toInt)
  val db = Database.forConfig("mydb")

  try {
    try {
      val dbSchema = Tables.metrics.schema ++ Tables.events.schema
      println(dbSchema.create.statements.head)
      Await.result(db.run(dbSchema.create), Duration.Inf)

    } catch {
      case e: PSQLException => {
        if (e.getMessage.contains("already exists")) {
          println("Assuming already created")
        } else {
          e.printStackTrace()
          System.exit(-1)
        }
      }
    }

    try {
      xbee.open()
    } catch {
      case _: InvalidOperatingModeException => {
        println("Could not determine Operating Mode. Trying again")
        xbee.open()
      }
    }

    xbee.addDataListener(new DataListener(db))

    println("\n>> Waiting for data...")
    
    sys addShutdownHook {
      println("Shutdown hook caught.")

      db.close()
      xbee.close()

      println("Done shutting down.")
    }

  } catch {
    case e: Throwable => {
      e.printStackTrace()

      System.exit(1)
    }
  }

}
