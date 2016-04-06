
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subscription
import rx.observables.SwingObservable

import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener


object SolarSystemApp {
  def main(args: Array[String]): Unit = {

    javax.swing.SwingUtilities.invokeLater(new Runnable {
      override def run(): Unit = {
        new MyFrame().run
      }
    });
  }
}




