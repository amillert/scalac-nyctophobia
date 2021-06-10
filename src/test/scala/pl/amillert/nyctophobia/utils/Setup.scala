package pl.amillert
package nyctophobia
package services

private object Setup {
  val emptyList: List[String]          = List.empty[String]
  val strListOneElement: List[String]  = "photos/mixed" :: Nil
  val strListTwoElements: List[String] = "photos/mixed" :: "photos/out" :: Nil
  val strListFourElementsSuffix: List[String] =
    "photos/mixed" :: "photos/out" :: "16" :: "xd" :: Nil
  val strListFourElementsPrefix: List[String] =
    "xd" :: "photos/mixed" :: "photos/out" :: "16" :: "xd" :: Nil

  val args: List[String] =
    "photos/mixed" :: "photos/out" :: "16" :: Nil
  val argsWrongInDir: List[String] =
    "photos/mixe" :: "photos/out" :: "16" :: Nil
  val argsWrongOutDir: List[String] =
    "photos/mixed" :: "photos/ou" :: "16" :: Nil
  val argsWrongBothDirs: List[String] =
    "photos/mixe" :: "photos/ou" :: "16" :: Nil
  val argsWrongThresholdFormat: List[String] =
    "photos/mixed" :: "photos/out" :: "16.1" :: Nil
  val argsThresholdStr: List[String] =
    "photos/mixed" :: "photos/out" :: "xd" :: Nil
  val argsThresholdTooBig: List[String] =
    "photos/mixed" :: "photos/out" :: "101" :: Nil
  val argsThresholdTooSmall: List[String] =
    "photos/mixed" :: "photos/out" :: "-1" :: Nil

  // I guess it's unhandled
  val config: Config            = Config("photos/mixed", "photos/out", 16)
  val configAllWrong: Config    = Config("xd", "lol", 1234)
  val configWrongInDir: Config  = Config("photos/mixe", "photos/out", 16)
  val configWrongOutDir: Config = Config("photos/mixed", "photos/ou", 16)
  val configNegThresh: Config   = Config("photos/mixed", "photos/out", -1)
}
