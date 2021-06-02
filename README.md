# SCRIPTS

This repository contains my API written on top of the OSBOT provided API.

The goals of this API are to provide a cleaner, easier, and more dependable set of functions and functionality to be used to create better scripts.

Some of the main points of this API are:
- modules to contain domain specific logic (fighting, banking, moving, etc) as one line functions!
- custom script launcher and wrapper allowing complete control over the script at any time, including:
  - dynamically loading a new script
  - controlling the anti ban or script completely independent of each other at any time
- multithreaded antiban and camera handler to operate concurrently to the main script
- multithreaded NPC tracker module used for tracking enemies/npcs


# Design of API and Script Launcher:

Every script uses a "Launcher" which replaces the default script entry point which is normally done by extending the OSBOT Script class.
The Launcher then creates a Client which controls all of the API functionality, the script is created with the Client reference allowing it to access the API through the client.
Finally the Client script is set to our newly created script (confused yet?) what this achieves is both the client and the script have control over each others execution, this allows the API to stop the script
as needed, or the script to stop the API and any of its components, this can be invaluable in developing precise scripts that shouldn't be interrupted during their execution.
As mentioned above this also allows for dynamic loading of new scripts since the Launcher is now the entry point.
