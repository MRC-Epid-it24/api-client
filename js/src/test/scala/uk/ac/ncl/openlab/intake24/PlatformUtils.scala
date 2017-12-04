package uk.ac.ncl.openlab.intake24

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobalScope

@js.native
@JSGlobalScope
private object JSGlobal extends js.Object {
  val process: js.Dynamic = js.native
}


object PlatformUtils {

  def getEnv(variable: String): String = JSGlobal.process.env.selectDynamic(variable).toString
}
