# A sheet that is a Navigation 3 destination leaves without its exit

When the destination pops, its scene leaves the composition and the producer removes its entry at once,
so the sheet vanishes instead of sliding out (as in the app the kit came from).

Options: keep the entry for the exit's duration after the producer leaves (the host would own a "leaving"
copy); or a scene strategy that keeps the scene composed until the sheet reports it is hidden. Decide when
an app needs it.
