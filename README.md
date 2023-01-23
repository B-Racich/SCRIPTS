# SCRIPTS

This repository contains my framework written on top of the OSBOT provided API.

The goals of this framework are to provide a cleaner, easier, and more dependable set of functions and functionality to be used to create better scripts.

Some of the main points of this framework are:
- modules to contain domain specific logic (fighting, banking, moving, etc) as one line functions!
- custom script launcher and wrapper allowing complete control over the script at any time, including:
  - dynamically loading a new script
  - controlling the anti ban or script completely independent/dependant of each other at any time
- multithreaded antiban and camera handler to operate concurrently to the main script
- multithreaded NPC tracker module used for tracking enemies/npcs

This work is for educational purposes
