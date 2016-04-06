import javax.swing.JSlider
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JFrame
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Color
import java.awt.GradientPaint
import java.awt.Dimension

import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription

import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

import scala.swing._
import scala.swing.Component._
import scala.swing.event._
import swing.Swing._

import Orientation._

object MainFrame extends SimpleSwingApplication with ConcreteSwingApi {
  
  val testSlider = new Slider()
  
  def top = new MainFrame {
    initLayout
    val testStream: Observable[Int] = testSlider.slides
    title = "Solar System"
    contents = new BoxPanel(orientation = Vertical) {
      border = EmptyBorder(top = 5, left = 5, bottom = 5, right = 5)
      contents += new BoxPanel(orientation = Horizontal) {
        contents += testSlider
      }
    }
  }
    
  def initLayout = {
    initDaySlider
    initFrame
  }
  
  def initDaySlider = {
    testSlider.min = 0 
    testSlider.max = SolarSystem.Earth.DAYS_REV_AROUND_SUN.toInt
    testSlider.value = 0
    testSlider.paintLabels = true
    testSlider.paintTicks = true
    testSlider.majorTickSpacing = 100
  }
  
  def initFrame = {
    
    //add(panel)
    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    //pack()
    //setVisible(true)
  }
  
}

class MyFrame extends JFrame with ConcreteSwingApi {
  
  val panel: JPanel 
    = new JPanel()
  
  val daySlider: JSlider 
    = new JSlider(0, SolarSystem.Earth.DAYS_REV_AROUND_SUN.toInt)
  val solarSystemView: SolarSystemView
    = new SolarSystemView()
  
  def run = {
    init
    
    val daysStream: Observable[Int]
      = GuiStreams.dayStream(daySlider)
      
    val locStream: Observable[(Double, Double)] 
      = Observable.just(Calc.translateIntoPolar(
      SolarSystemView.DEFAULT_WIDTH / 2, 
      SolarSystemView.DEFAULT_HEIGHT / 2))
    val coordsStream: Observable[(Int, (Double, Double))] 
      = CoordinateSystem.coordsStream(daysStream, locStream)
    
    val elemLogic 
      = SolarSystemLogic.create(coordsStream, SolarSystemView.DEFAULT_WIDTH)
    val solarSystem 
      = new SolarSystem(
        coordsStream, 
        elemLogic._1, 
        elemLogic._2,
        elemLogic._3)
    
    daysStream.subscribe(Debugger.printInt(_))
    solarSystem.earthStream.subscribe(Debugger.printCooridnates(_))
    solarSystem.sunStream.subscribe(solarSystemView.drawSun(_))
    solarSystem.earthStream.subscribe(solarSystemView.drawEarth(_))
    solarSystem.moonStream.subscribe(solarSystemView.drawMoon(_))
    solarSystem.moonStream.subscribe(solarSystemView.drawMock(_))
  }
  
  def init = {
    solarSystemView.init
    initDaySlider
    initPanel
    initFrame
  }
  
  def initFrame = {
    setTitle("Solar System")
    add(panel)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    pack()
    setVisible(true)
  }
  
  def initPanel = {
    panel.add(solarSystemView)
    panel.add(daySlider)
  }
  
  def initDaySlider = {
    daySlider.setPaintLabels(true)
    daySlider.setPaintTicks(true)
    daySlider.setMajorTickSpacing(100)
    daySlider.setValue(0)
  }
}

class SolarSystemView extends JComponent {
  import SolarSystemView._
  import SolarSystem._
  
  val stars: List[(Int, Int, Int)] 
    = (0 to NUM_STARS).toList.map(_ => 
    ((new util.Random).nextInt(DEFAULT_WIDTH),
    (new util.Random).nextInt(DEFAULT_HEIGHT),
    (new util.Random).nextInt(MAX_STAR_RADIUS)))
  
  var image: Image = null
  var graphics2D: Graphics2D = null

  def init {
    setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))
  }

  override def paintComponent(g: Graphics) {
    if (image == null) {
      image = createImage(getSize().width, getSize().height)
      graphics2D = image.getGraphics().asInstanceOf[Graphics2D]
      drawSpaceBackdrop()
    }
    g.drawImage(image, 0, 0, null)
  }
  
  def drawSpaceBackdrop() {
    graphics2D.setColor(Color.BLACK)
    graphics2D.fillRect(0, 0, getWidth(), getHeight())
    
    graphics2D.setColor(Color.WHITE)
    stars map {case(x, y, r) => graphics2D.fillOval(x, y, r, r)}
  }
    
  def drawSun(p_coords: (Int, (Double, Double))) {
    import SunView._
   
    val locInCart = Calc.translateIntoCart(p_coords._2)
    val color: GradientPaint 
      = new GradientPaint(
        locInCart._1, 
        locInCart._2, 
        Color.YELLOW, 
        locInCart._1, 
        locInCart._2 + SunView.radius, 
        Color.RED);
    

    graphics2D.setPaint(color);
    
    graphics2D.fillOval(
      locInCart._1 - radius / 2, 
      locInCart._2 - radius / 2, 
      radius, 
      radius);
  }

  def drawEarth(p_coords: (Int, (Double, Double))) {
    import EarthView._
    
    val locInCart = Calc.translateIntoCart(p_coords._2)
    val color: GradientPaint 
      = new GradientPaint(
        locInCart._1, 
        locInCart._2, 
        Color.BLUE, 
        locInCart._1,
        locInCart._2 + EarthView.radius, 
        Color.GREEN.darker(), 
        true);
    
    graphics2D.setPaint(color);
    
    graphics2D.fillOval(
      locInCart._1 - radius / 2, 
      locInCart._2 - radius / 2, 
      radius, 
      radius);
    
  }
  
  def drawMoon(p_coords: (Int, (Double, Double))) {
    import MoonView._

    val locInCart = Calc.translateIntoCart(p_coords._2)
    
    graphics2D.setPaint(Color.WHITE);
    
    graphics2D.fillOval(
      locInCart._1 - radius / 2, 
      locInCart._2 - radius / 2, 
      radius, 
      radius);
  }
  
  def drawMock(p_coords: (Int, (Double, Double))) {
    drawSpaceBackdrop()
    repaint()
  }
  
  object SunView {
    import Sun._
    val radius: Int 
      = (RADIUS * getWidth()).toInt

  }
  
  object EarthView {
    import Earth._
    val radius: Int
      = (RADIUS * getWidth()).toInt
      
    val distance: Int
      = (DISTANCE_TO_SUN * DEFAULT_WIDTH).toInt
  }
  
  object MoonView {
    import Moon._
    val radius: Int
      = (RADIUS * getWidth()).toInt
      
    val distance: Int
      = (DISTANCE_TO_EARTH * DEFAULT_WIDTH).toInt
  }
  
}

object SolarSystemView {  
  
  val DEFAULT_WIDTH   :Int  = 640;
  val DEFAULT_HEIGHT  :Int  = 640;
  
  val NUM_STARS: Integer = 100
  val MAX_STAR_RADIUS: Integer = 3
}

object GuiStreams {
  def dayStream(daySlider: JSlider): Observable[Int] = Observable.create(
    (observer: Observer[Int]) => {
      val listener = new ChangeListener() {
        def stateChanged(e: ChangeEvent): Unit = {
            observer.onNext(daySlider.getValue()) 
        }
      }
      daySlider.addChangeListener(listener)
      new Subscription {
        override def unsubscribe: Unit = daySlider.removeChangeListener(listener)
      }
    }
  )
}

object CoordinateSystem {
  def coordsStream(
    timeStream  : Observable[Int],
    locStream   : Observable[(Double, Double)]
  ) : Observable[(Int, (Double, Double))] = {
    timeStream.combineLatest(locStream)
  }
}

trait ConcreteSwingApi extends SwingApi {
  type ValueChanged = scala.swing.event.ValueChanged
  
  object ValueChanged {
    def unapply(x: Event) = x match {
      case vc: ValueChanged => Some(vc.source.asInstanceOf[Slider])
      case _ => None
    }
  }
  
  type Slider = scala.swing.Slider
}
