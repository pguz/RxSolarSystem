import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription

object SolarSystem {  

  object Sun {
    val RADIUS: Double 
      = .2
  }
  
  object Earth {
    val RADIUS: Double 
      = .3 * Sun.RADIUS
    val DISTANCE_TO_SUN: Double 
      = .4
    val DAYS_REV_AROUND_SUN: Double     
      = 365.0
    val HOURS_REV_AROUND_AXIS: Integer
      = 24
  }
  
  object Moon {
    val RADIUS: Double 
      = .2 * Earth.RADIUS
    val DISTANCE_TO_EARTH : Double 
      = .1
    val DAYS_ORBIT_AROUND_EARTH: Double
      = 27.3
  }
}

class SolarSystem(
  val baseStream  : Observable[(Int, (Double, Double))],
  val sunStream   : Observable[(Int, (Double, Double))],
  val earthStream : Observable[(Int, (Double, Double))],
  val moonStream  : Observable[(Int, (Double, Double))]) {
}

object SolarSystemLogic {  
  import SolarSystem._
  
  val baseCoords = (0.0, 0.0)
  
  def create(coordsStream: Observable[(Int, (Double, Double))], scale: Int)
    = {

      val fromBaseCoord = {
        coords: (Int, (Double, Double)) =>
          (coords._1,
            Calc.translatePolarCoords(coords._2,
              baseCoords))
      }

      val moonToEarth = {
        coords: (Int, (Double, Double)) =>
          (coords._1,
            Calc.translatePolarCoords(coords._2,
              (Moon.DISTANCE_TO_EARTH * scale, coords._1 / Moon.DAYS_ORBIT_AROUND_EARTH * Calc.TWO_PI)))
      }

      val earthToSun = {
        coords: (Int, (Double, Double)) => (coords._1,
          Calc.translatePolarCoords(coords._2,
            (Earth.DISTANCE_TO_SUN * scale, coords._1/Earth.DAYS_REV_AROUND_SUN * Calc.TWO_PI)))
      }

      /*val earthStream : Observable[(Int, (Double, Double))]
        = coordsStream.map(fromBaseCoord)

      val sunStream : Observable[(Int, (Double, Double))]
        = earthStream.map(earthToSun)

      val moonStream : Observable[(Int, (Double, Double))]
        = earthStream.map(moonToEarth)*/

      /*val sunStream : Observable[(Int, (Double, Double))]
        = coordsStream.map(fromBaseCoord)

      val earthStream : Observable[(Int, (Double, Double))]
        = sunStream.map(earthToSun)

      val moonStream : Observable[(Int, (Double, Double))]
        = earthStream.map(moonToEarth)*/

      /*val moonStream : Observable[(Int, (Double, Double))]
        = coordsStream.map(fromBaseCoord)
       
      val earthStream : Observable[(Int, (Double, Double))] 
        = moonStream.map(moonToEarth)

      val sunStream : Observable[(Int, (Double, Double))] 
        = earthStream.map(earthToSun)*/
       

      (sunStream, earthStream, moonStream)
    }
}
