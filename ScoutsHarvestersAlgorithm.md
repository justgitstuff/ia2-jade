This algorithm has been studied and adapted by responsible of Scouts (Francesc Llaó) and of responsible of Harvest (David Perelló).

It Was readapted A **search algorithm according to the circumstances and needs that we encounter in our practice for the distance between start and end.**

Harverst:  Perform two searches. The first, through the pathway, the other can also happen along the way not found in the map. For these two cases have put some weight, when a cell are undiscovered, it has more weight than a discovered cell.

Scout: Perform two searches. In this case it reversed the priorities in the location of the distance between initial and final. That is more priority is given to the dark side (not discovered)

In two  cases, the optimal solution is the minimum distance.

The next step is the incorporation of this algorithm in our interface.