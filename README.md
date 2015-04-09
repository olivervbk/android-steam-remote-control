# Steam Remote

A simple android implementation of the unreleased and undocumented remote control API for the Steam BigPicture interface.

####WARNING: this is still a WIP, and most things are not stable and far from being finished. Continue at your own risk.

Heavily based on the docs provided by the [SteamDatabase\RemoteControlDocs](https://github.com/SteamDatabase/RemoteControlDocs).
Great work from those guys.

This project is currently being tested on Android 5 (Lollipop) and uses Androids customization of the Apache HttpClient (which is currently deprecated...).

#####Working (kind of):
* authorization of remote control (must confirm in bigpicture manually)
* mouse movement (as touchpad) + left click
* steam game listing and starting a game/download

#####Not working:
* gamepad (xbox controller style). Does not release presses (see [issue](https://github.com/SteamDatabase/RemoteControlDocs/issues/4) )
* music control and music status

#####Currently working on:
* music control
* spaces management

#####Future
* keyboard input (sequences and specific keys)
* understanding the stream request (help appreciated)


#### Steam how-to:
1. Open steam with 'steam -bigpicture -enableremotecontrol'
2. Connect to a decent wifi network that's shared with your computer
3. Have fun (probably compiling this app)
