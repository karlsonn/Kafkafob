package net.karlsonn.kafkafob.models

case class Counter(value: Int = 1)

case class CounterContainer
(
  counter1: Counter = Counter(),
  counter2: Counter = Counter(),
  counterList: Vector[Counter] = Vector(Counter(), Counter())
)

case class Model(var container: CounterContainer = CounterContainer())
