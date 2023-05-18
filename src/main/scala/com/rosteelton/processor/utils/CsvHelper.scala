package com.rosteelton.processor.utils

object CsvHelper {
  val delimiter              = '|'
  val lineTerminator: String = "\n"

  def splitLine(input: String): List[String] =
    input.split(CsvHelper.delimiter).toList

  def withLineTerminator(input: String): String =
    s"$input$lineTerminator"
}
