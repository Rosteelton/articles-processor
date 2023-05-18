package com.rosteelton.processor.utils

import com.github.tototoshi.csv.DefaultCSVFormat

object CsvHelper {
  implicit object Format extends DefaultCSVFormat {
    override val delimiter              = '|'
    override val lineTerminator: String = "\n"
  }
}
