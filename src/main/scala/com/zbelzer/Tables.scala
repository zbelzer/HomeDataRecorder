package com.zbelzer

import slick.lifted.TableQuery

object Tables {
  val metrics: TableQuery[Metrics] = TableQuery[Metrics]
  val events: TableQuery[Events] = TableQuery[Events]
}
