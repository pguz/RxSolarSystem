object Calc {
  val TWO_PI: Double = 2.0 * Math.PI;
  
  def translatePolarCoords(p1 :(Double, Double), p2: (Double, Double)): (Double, Double)
  = {
    val x1 = Math.cos( p1._2 ) * p1._1
    val y1 = Math.sin( p1._2 ) * p1._1
    val x2 = Math.cos( p2._2 ) * p2._1
    val y2 = Math.sin( p2._2 ) * p2._1
    translateIntoPolar(x1 + x2, y1 + y2)
  }
    
  def translateIntoPolar(x: Double, y: Double): (Double, Double)
    = (Math.sqrt(x * x + y * y), Math.atan2(y, x))
    
  def translateIntoCart(locInPolar: (Double, Double)): (Int, Int)
    = ( (locInPolar._1 * Math.cos(locInPolar._2)).toInt, 
        (locInPolar._1 * Math.sin(locInPolar._2)).toInt)
}

object Debugger {
  def printInt(p_int: Int) {
    System.out.println(
      "Int: " + p_int)
    System.out.flush()
  }
  
  def printCooridnates(p_coords : (Int, (Double, Double))) {
    System.out.println(
      "time: " + p_coords._1 
      + "\tdistance: " + p_coords._2._1 
      + ", \tangle: " + p_coords._2._2 )
    System.out.flush()
  }
}

