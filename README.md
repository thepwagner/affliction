Affliction
=========

Affliction is a Java webapp that will play Simpsons: Tapped Out for you. Tapped out must be running locally (i.e. BlueStacks). Status information is available remotely, so you can leave this running on an old PC and watch your game play itself from your phone. (Way more cool than playing a game on your phone).

Why? Boredom.


Proof of concept/alpha lives in src/main/groovy. Yay for history.

Pretty guice-jersey webapp lives in src/main/java:

How does it work?
==
The  strategy is search and destroy:

1. The screen is captured every N ms (500 worked well or me), Guava EventBus shouts it from the hilltop.

1. The capture hits a PixelBuffer, which scans the bitmap. Since the game is a cartoon, we'll be looking for concentrated areas of particular colors of things we want to tap on.

1.  Every 5 seconds, a history in the PixelBuffer is checked, to ensure the pixels are moving (another importaint trait of things we want to tap on). Moving pixels get passed to a clusterer, which emits a list of likely targets.

1. The target list is consumed by a target buffer and the actual tapper, who work together to clear the board.

URLs
===


- /schedule?screen=X - set the screen to capture period
- /camera?dir=[n,e,s,w]  - pans the camera
- /screen/region?x=0&y=0&w=1980&h=1080  - sets the capture area
- /screen/image - view the captured pixels
- /pixels/, /pixels/image - view the pixel buffer as JSON/PNG
- /targets/, /targets/image - view the target buffer as JSON/PNG