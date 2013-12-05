MapNavigator
============

Draw route between location

It is also draw alternative route between long distance such as mumbai,pune,etc...


Use :
========

Get the google map and in Navigator pass start and end location lat and long.

GoogleMap map = getMap();

Navigator nav = new Navigator(map,start,end);

nav.findDirections(true, false); for single path

If u get alternative path then set 

nav.findDirections(true, true); for alternative path
