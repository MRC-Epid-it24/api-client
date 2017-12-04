package uk.ac.ncl.openlab.intake24

object PlatformUtils {

  def getEnv(variable: String): String = System.getenv(variable)
}
