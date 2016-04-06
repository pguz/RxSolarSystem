import scala.language.reflectiveCalls
import scala.swing.Reactions.Reaction
import scala.swing.event.Event
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription

trait SwingApi {
  type ValueChanged <: Event
  
  val ValueChanged: {
    def unapply(x: Event): Option[Slider]
  }
  
  type Slider <: {
    def value: Int
    def subscribe(r: Reaction): Unit
    def unsubscribe(r: Reaction): Unit
  }
  
  implicit class SliderOps(slider: Slider) {
    
    def slides: Observable[Int] = 
    
      Observable.create(
        (observer: Observer[Int]) => {
          
          val reactor: Reaction 
            = {
              case ValueChanged(_) => observer.onNext(slider.value)
            }
            
          slider.subscribe(reactor)
          
          new Subscription {
            override def unsubscribe: Unit = 
               slider.unsubscribe(reactor)
          } 
        }
      )
  }
}
