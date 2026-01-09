# heatingMonitor
Use a raspberry pi with widgetlords sensor boards to monitor a heating system

The heatng system consists of a boiler, 2 zones each with a number of honeywell zone valves splitting the main zones into smaller pieces.

There have been issues with zone valves failing and also concern that the boiler is not maintaining an adequate temperature. 

This led to the idea of building a sensor system that might provide alarms if components fail.

## Zone valves
Each zone valve has a mechanical valve operated by a motor. The motor is driven by the 24v supply switched by the attached thermostat(s).
Each valve has a swith actuated by the motor travelling to the fully open position. This switch is used to activate the circulator for the zone.
Each valve also has a manual lever to open the valve - this does NOT activate the switch.

In summary - the thermostat operates the valve motor, the valve operates a switch - that turns on the pump.

### Failure modes for zone valves
  * Switch fails - motor is working but switch fails to come on
  * Motor fails - just doesn't operate
  * Seizes - bearing problem or some other issue with the gears

All of these can be detected by the thermostat closing but no corresponding circulator activation.
Note that with multiple zone valves all the switches are connected together. A separate valve may mask the failure of a valve. However, at some point it should be possible to note the failure and alert.

## Temperature sensing
When one or more circulators are running the boiler is supposed to maintain the water temperature between some reasonable values. There have been some issues where the boiler temperature has falled too low and the fan convectors turn off because of low water temperature. This system shudl monitor those temperatures and treport any issues.

Also - during cold weather - it's possible for a zone to need more heat than supplied - perhaps because the circulation is unbalanced. This may provide information on how well the system is functioning.

## Hardware
### Raspberry pi
This system is built using a raspberry pi 5 with 8G of memory. Probably this will be replaced with a smaller version when the system is working. For the time being it makes for easier development if we aren't memory constrained.

### Analog sensor board
This is the widgetloards (https://widgetlords.com/) Pi-SPi-8AI+ Raspberry Pi Analog Input I/O Module configured for thermistor input (about $26). Attached to that will be a number of standard 10K thermistors.
This board can take up to 8 inputs. Expected inputs are:
 * Main boiler output
 * Main boiler input
 * Zone 1 output
 * Zone 2 output

Additionally, sensors may be added to zone subsections if needed to identify issues. Currently, we have 9 of these so they won't all fit on one board.

### Digital sensor board
This is the widgetloards (https://widgetlords.com/) Pi-SPi-8DI Raspberry Pi Digital Input I/O Module (about $26). this can take the standard 24v ac as input. There will be 2-3 of these.
Expected inputs are:
 * Zone 1 circulator
 * Zone 2 circulator
 * 9 thermostat inputs.

### Useful links
Or at least those I found useful:

http://www.mosaic-industries.com/embedded-systems/microcontroller-projects/temperature-measurement/ntc-thermistors/circuit-schematics[thermistor info]

