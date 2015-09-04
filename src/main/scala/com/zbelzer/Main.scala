package com.zbelzer

import com.digi.xbee.api.XBeeDevice
import com.digi.xbee.api.exceptions.InvalidOperatingModeException
import org.postgresql.util.PSQLException
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  val PORT = "/dev/tty.usbserial-DA011NL3"
  //  val PORT = "/dev/tty.usbserial-DA013OL0"
  val BAUD_RATE = 9600

  val xbee = new XBeeDevice(PORT, BAUD_RATE)
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
